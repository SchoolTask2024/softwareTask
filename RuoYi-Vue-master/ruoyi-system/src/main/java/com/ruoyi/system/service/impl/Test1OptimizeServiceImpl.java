package com.ruoyi.system.service.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ruoyi.system.domain.AnalysisCoverage;
import com.ruoyi.system.domain.ResultTest;
import com.ruoyi.system.domain.Test1;
import com.ruoyi.system.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.Test1Optimize;
import com.ruoyi.system.service.ITest1OptimizeService;

/**
 * 测试用例集优化Service业务层处理
 *
 * @author njz
 * @date 2024-06-26
 */
@Service
public class Test1OptimizeServiceImpl implements ITest1OptimizeService
{
    @Autowired
    private Test1OptimizeMapper test1OptimizeMapper;

    @Autowired
    private Test1Mapper test1Mapper;

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private ResultTestMapper resultTestMapper;

    @Autowired
    private CodeMapper codeMapper;

    /**
     * 查询测试用例集优化
     *
     * @param id 测试用例集优化主键
     * @return 测试用例集优化
     */
    @Override
    public Test1Optimize selectTest1OptimizeById(Long id)
    {
        return test1OptimizeMapper.selectTest1OptimizeById(id);
    }

    /**
     * 查询测试用例集优化列表
     *
     * @param test1Optimize 测试用例集优化
     * @return 测试用例集优化
     */
    @Override
    public List<Test1Optimize> selectTest1OptimizeList(Test1Optimize test1Optimize)
    {
        return test1OptimizeMapper.selectTest1OptimizeList(test1Optimize);
    }

    /**
     * 新增测试用例集优化
     *
     * @param test1Optimize 测试用例集优化
     * @return 结果
     */
    @Override
    public int insertTest1Optimize(Test1Optimize test1Optimize)
    {
        return test1OptimizeMapper.insertTest1Optimize(test1Optimize);
    }

    /**
     * 执行测试用例集优化
     *
     * @param test1Optimize 测试用例集优化
     * @return 结果
     */
    @Override
    public int updateTest1Optimize(Test1Optimize test1Optimize)
    {
        //调用Python的覆盖率
        String fileName=test1Optimize.getName();
        String fileVersion= String.valueOf(codeMapper.selectCodeById(test1Optimize.getId()).getVersion());
        String filePath="D:/manager/code/";
        filePath+=test1Optimize.getPath();
        Long fileID=test1Optimize.getId();
        //该代码对应的所有AnalysisCoverage对象
        List<AnalysisCoverage> analysisCoverageList=resultMapper.selectResultByCodeId(fileID);
        Long fullCoverageResultID= 0L;//默认没有满足MCDC覆盖率100的测试
        double maxAvrgCoverage=0;
        //判断是否有MCDC覆盖率100的运行记录
        for (int i = 0; i < analysisCoverageList.size(); i++) {
            boolean isFullMCDC = true;
            double allCoverage=0;
            //"[1.0,1.0,1.0,1.0,1.0]"
            String coverageData=analysisCoverageList.get(i).getCoverageData();
            // 使用正则表达式提取字符串中的数字
            Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
            Matcher matcher = pattern.matcher(coverageData);

            List<Double> list = new ArrayList<>();
            while (matcher.find()) {
                list.add(Double.parseDouble(matcher.group()));
                allCoverage+=Double.parseDouble(matcher.group());
            }
            // 检查列表中的所有元素是否都是1.0
            for (double num : list) {
                if (num != 1.0) {
                    //本次测试覆盖率没满，跳过
                    isFullMCDC = false;
                    break;
                }
            }
            if(isFullMCDC){
                //本次测试MCDC覆盖率100，记录这次测试的id
                fullCoverageResultID=analysisCoverageList.get(i).getId();
                maxAvrgCoverage=1;
                break;
            }
            else{
                if(allCoverage/list.size()>maxAvrgCoverage){
                    maxAvrgCoverage=allCoverage/ list.size();
                }
            }
        }
        //保存测试文件的List
        List<Test1> tests;
        String testPaths="";//保存测试文件路径的字符串
        //如果存在满足MCDC覆盖率为100的测试
        if (fullCoverageResultID != 0) {
            ResultTest resultTest=new ResultTest(fullCoverageResultID);
            List<ResultTest> resultTestList=resultTestMapper.selectResultTestList(resultTest);
            for (int i = 0; i < resultTestList.size(); i++) {
                Long testID=resultTestList.get(i).getTestId();
                String testPath=test1Mapper.selectTest1ById(testID).getPath();
                if(i<resultTestList.size()-1){
                    testPaths+=testPath+"@";
                }
                else{
                    testPaths+=testPath;
                }
            }
        }
        //否则传改代码对应的全部用例
        else{
            //通过源代码名字获取到对应的所有测试代码的路径
            tests=test1Mapper.selectTestListByCodeName(fileName);

            for (int i = 0; i < tests.size(); i++) {
                //每个测试文件的路径用@分割
                if(i<tests.size()-1){
                    testPaths+=tests.get(i).getPath()+"@";
                }
                else{
                    testPaths+=tests.get(i).getPath();
                }
            }
        }
        //判断当前程序所有运行结果中最高的平均覆盖率是否为100（t代表100，f代表覆盖率没满）
        String avgCoverageRate=Double.toString(maxAvrgCoverage);

        //执行python命令（命令还要改）
        ProcessBuilder pb = new ProcessBuilder("python", "Users_optimize.py",filePath,testPaths,avgCoverageRate);
        pb.directory(new File("D:/manager/software1"));
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            List<String> outputLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 输出每一行内容
                outputLines.add(line);
            }
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.out.println(errorLine); // 输出错误流内容
                }
            }

            // 获取最后三行数据
            int size = outputLines.size();
            String coverageRateExpected = size > 2 ? outputLines.get(size - 3) : null;
            String originalTest = size > 1 ? outputLines.get(size - 2) : null;
            String optimizedTest = size > 0 ? outputLines.get(size - 1) : null;
            coverageRateExpected+="%";

            // 处理最后三行数据（例如，保存到数据库或日志）
            System.out.println("Coverage Rate Expected: " + coverageRateExpected);
            System.out.println("Original Test: " + originalTest);
            System.out.println("Optimized Test: " + optimizedTest);

            // 写入文件
            writeResultToFile(fileName,fileVersion, coverageRateExpected, originalTest, optimizedTest);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private void writeResultToFile(String fileName, String fileVersion,String coverageRateExpected, String originalTest, String optimizedTest) {
        String filePath = "D:/manager/result/" + fileName +" v"+fileVersion+"优化结果.txt";
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "GBK"))) {
            writer.write("原本测试数据：" + originalTest + "\n");
            writer.write("优化后测试数据：" + optimizedTest + "\n");
            writer.write("预期平均MC/DC覆盖率：" + coverageRateExpected + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 批量删除测试用例集优化
     *
     * @param ids 需要删除的测试用例集优化主键
     * @return 结果
     */
    @Override
    public int deleteTest1OptimizeByIds(Long[] ids)
    {
        return test1OptimizeMapper.deleteTest1OptimizeByIds(ids);
    }

    /**
     * 删除测试用例集优化信息
     *
     * @param id 测试用例集优化主键
     * @return 结果
     */
    @Override
    public int deleteTest1OptimizeById(Long id)
    {
        return test1OptimizeMapper.deleteTest1OptimizeById(id);
    }
}
