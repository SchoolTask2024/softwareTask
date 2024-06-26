package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.CodeMapper;
import com.ruoyi.system.mapper.ResultTestMapper;
import com.ruoyi.system.mapper.Test1Mapper;
import com.ruoyi.system.service.ICoverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ResultMapper;
import com.ruoyi.system.service.IResultService;

/**
 * 代码运行Service业务层处理
 * 
 * @author niujiazhen
 * @date 2024-04-26
 */
@Service
public class ResultServiceImpl implements IResultService 
{
    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private Test1Mapper test1Mapper;
    @Autowired
    private ResultTestMapper resultTestMapper;
    @Autowired
    private ICoverageService coverageService;
    @Value("${code.path}")
    private String codeLocalPath;
    @Value("${test.path}")
    private String testLocalPath;
    /**
     * 查询代码运行
     * 
     * @param id 代码运行主键
     * @return 代码运行
     */
    @Override
    public Result selectResultById(Long id)
    {
        return resultMapper.selectResultById(id);
    }

    /**
     * 查询代码运行列表
     * 
     * @param result 代码运行
     * @return 代码运行
     */
    @Override
    public List<Result> selectResultList(Result result)
    {
        List<Result> resultList = resultMapper.selectResultList(result);
        for(Result r:resultList){
            r.setTest1List(test1Mapper.selectByResultId(r.getId()));
        }
        return resultList;
    }
    @Override
    public List<AnalysisCoverage> selectResultByCodeId(Long codeId){
        return resultMapper.selectResultByCodeId(codeId);
    }
    /**
     * 新增代码运行
     * 
     * @param result 代码运行
     * @return 结果
     */
    @Override
    public int insertResult(Result result)
    {
        return resultMapper.insertResult(result);
    }

    /**
     * 修改代码运行
     * 
     * @param result 代码运行
     * @return 结果
     */
    @Override
    public int updateResult(Result result)
    {
        return resultMapper.updateResult(result);
    }

    /**
     * 批量删除代码运行
     * 
     * @param ids 需要删除的代码运行主键
     * @return 结果
     */
    @Override
    public int deleteResultByIds(Long[] ids)
    {
        resultTestMapper.deleteResultTestByResultIds(ids);
        return resultMapper.deleteResultByIds(ids);
    }

    /**
     * 删除代码运行信息
     * 
     * @param id 代码运行主键
     * @return 结果
     */
    @Override
    public int deleteResultById(Long id)
    {
        return resultMapper.deleteResultById(id);
    }

    /**
     * 计算MC/DC
     */
    @Override
    public void calculateMcDc(Result result){
        Code code = codeMapper.selectPathById(result.getCodeId());
        //java
        if(code.getType() ==0){
            String codePath =code.getPath();
            String codeFilePath = codeLocalPath +"/"+codePath;
            ArrayList<FIleLocation> tests = new ArrayList<>();
            ArrayList<Test1> testPaths = test1Mapper.selectPathsByIds(result.getTestIds().toArray(new Long[0]));
            for(Test1 test1:testPaths){
                tests.add(new FIleLocation(test1.getName(),testLocalPath+"/"+test1.getPath()));
            }
            try {
                result.setPath(coverageService.getCoverageFileJava(new FIleLocation(code.getName(),codeFilePath),tests));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //python
        else if(code.getType() ==1){
            String codeFilePath = codeLocalPath +"/"+code.getPath();
            ArrayList<String> tests = new ArrayList<>();
            ArrayList<Test1> testPaths = test1Mapper.selectPathsByIds(result.getTestIds().toArray(new Long[0]));
            for(Test1 test1:testPaths){
                tests.add(testLocalPath+"/"+test1.getPath());
            }
            AnalysisCoverage analysisCoverage = coverageService.getCoverageFilePython(codeFilePath,tests);
            result.setCoverageRate(analysisCoverage.getPath());
            result.setConditions(analysisCoverage.getConditions());
            result.setCoverageData(analysisCoverage.getCoverageData());
        }
        //c
        else if(code.getType() ==2){
            String codeFilePath = codeLocalPath +"/"+code.getPath();
            ArrayList<String> tests = new ArrayList<>();
            ArrayList<Test1> testPaths = test1Mapper.selectPathsByIds(result.getTestIds().toArray(new Long[0]));
            for(Test1 test1:testPaths){
                tests.add(testLocalPath+"/"+test1.getPath());
            }
            AnalysisCoverage analysisCoverage = coverageService.getCoverageFileC(codeFilePath,tests);
            result.setCoverageRate(analysisCoverage.getPath());
            result.setConditions(analysisCoverage.getConditions());
            result.setCoverageData(analysisCoverage.getCoverageData());
        }


    }

}
