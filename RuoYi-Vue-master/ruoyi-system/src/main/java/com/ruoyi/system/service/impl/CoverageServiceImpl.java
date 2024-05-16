package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICoverageService;
import org.apache.commons.io.IOUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.xml.XMLFormatter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CoverageServiceImpl implements ICoverageService {

    //获取MC/DC覆盖率
    @Override
    public String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPath) {
        //需要补充
        return null;
    }

    public static String generateCoverageReport1(String codePath, ArrayList<String> execFilePaths) {
        try {
            // 生成JaCoCo执行文件加载器
            ExecFileLoader loader = new ExecFileLoader();

            // 循环加载每个执行文件
            for (String execFilePath : execFilePaths) {
                try (InputStream inputStream = new FileInputStream(execFilePath)) {
                    loader.load(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "error: " + e.getMessage();
                }
            }

            // 创建覆盖率分析器
            CoverageBuilder coverageBuilder = new CoverageBuilder();
            Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), coverageBuilder);

            // 分析代码覆盖率
            analyzer.analyzeAll(new File(codePath));

            // 创建XML格式化器
            XMLFormatter xmlFormatter = new XMLFormatter();

            // 创建XML报告
            IReportVisitor xmlVisitor = xmlFormatter.createVisitor(new FileOutputStream("target/site/jacoco/report.xml"));
            xmlVisitor.visitInfo(loader.getSessionInfoStore().getInfos(), loader.getExecutionDataStore().getContents());
            xmlVisitor.visitBundle(coverageBuilder.getBundle("Code Coverage Report"), new DirectorySourceFileLocator(new File(codePath), StandardCharsets.UTF_8.name(), 4));

            // 关闭报告访问者
            xmlVisitor.visitEnd();

            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static void main(String[] args) {
        // 创建测试代码和执行数据文件路径
        String codePath = "/Users/niujiazhen/Desktop/作业/大三下/软件工程/课程设计/niujiazhen/RuoYi-Vue-master/ruoyi-system/src/test/java/pro2.java";
        List<String> testPaths = Arrays.asList(
                "pro2Test1",
                "pro2Test2",
                "pro2Test3",
                "pro2Test4"
        );

        // 创建覆盖率服务实例
        ICoverageService coverageService = new CoverageServiceImpl();

        // 执行测试代码并生成执行数据文件
        ArrayList<String> execFilePaths = new ArrayList<>(); // 修改此处为 ArrayList<String>
        for (String testPath : testPaths) {
            // 执行测试代码并生成执行数据文件
            String execFilePath = executeTestAndGenerateExecFile(testPath);
            execFilePaths.add(execFilePath);
        }

        // 生成覆盖率报告
        String result = generateCoverageReport1(codePath, execFilePaths);
        System.out.println("Coverage Report Generation Result: " + result);
    }

    private static String executeTestAndGenerateExecFile(String testClassName) {
        // 执行测试代码并生成执行数据文件的逻辑
        try {
            // 使用 Runtime.exec() 方法执行测试代码
            Process process = Runtime.getRuntime().exec("mvn test -Dtest=" + testClassName);
            // 等待测试执行完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 测试执行成功，返回执行数据文件路径
                return "/Users/niujiazhen/Desktop/作业/大三下/软件工程/课程设计/niujiazhen/RuoYi-Vue-master/ruoyi-system/src/test/java/target/jacoco.exec";
            } else {
                // 测试执行失败，返回空字符串或者抛出异常
                throw new RuntimeException("Test execution failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ""; // 或者抛出异常
        }
    }
}
