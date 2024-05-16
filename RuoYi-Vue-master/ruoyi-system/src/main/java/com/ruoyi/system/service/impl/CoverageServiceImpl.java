package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICoverageService;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
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
    @Override
    public String generateC(String codePath, ArrayList<String> execFilePaths) {
        try {
            // Extract the base name of the code file without extension
            String baseName = new File(codePath).getName().replace(".c", "");

            // Step 1: Compile the main code with coverage flags
            String compileCommand = String.format("gcc -fprofile-arcs -ftest-coverage -o %s %s", baseName, codePath);
            runCommand(compileCommand);

            // Step 2: Compile each test file
            for (String testPath : execFilePaths) {
                String testBaseName = new File(testPath).getName().replace(".c", "");
                String testCompileCommand = String.format("gcc -fprofile-arcs -ftest-coverage -o %s %s", testBaseName, testPath);
                runCommand(testCompileCommand);
            }

            // Step 3: Run each test executable
            for (String testPath : execFilePaths) {
                String testBaseName = new File(testPath).getName().replace(".c", "");
                String execCommand = String.format("./%s", testBaseName);
                runCommand(execCommand);
            }

            // Step 4: Generate the coverage report using gcov
            String gcovCommand = String.format("gcov %s", codePath);
            runCommand(gcovCommand);

            // Step 5: Read and return the coverage report
            String reportFileName = baseName + ".c.gcov";
            return readReport(reportFileName);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void runCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // Optionally log the output
        }
    }

    private String readReport(String reportFileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(reportFileName));
        StringBuilder reportContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            reportContent.append(line).append("\n");
        }
        return reportContent.toString();
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








    //python report
    public  String generatePythonCoverageReport(ArrayList<String>path, String type,String name){
        String message = null;
        try {
            String pythonScriptDir = path.get(0);
            ProcessBuilder pb = new ProcessBuilder(type, name);
            pb.directory(new File(pythonScriptDir));

            Process process = pb.start();

            BufferedReader reader1 = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line1;
            while ((line1 = reader1.readLine()) != null) {
                // 只打印包含类别名称的行
                System.out.println(line1);
            }
            //输出报错行数
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
