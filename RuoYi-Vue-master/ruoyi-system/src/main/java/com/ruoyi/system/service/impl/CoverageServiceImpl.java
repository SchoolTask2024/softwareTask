package com.ruoyi.system.service.impl;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICoverageService;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.xml.XMLFormatter;
import org.junit.platform.launcher.core.LauncherFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import org.junit.platform.console.ConsoleLauncher;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
//import org.junit.platform.launcher.LauncherFactory;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoverageServiceImpl implements ICoverageService {


    //generate C report
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

            //Step 3: Run each test executable
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
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File("/Users/niujiazhen/Desktop/作业/大三下/软件工程/课程设计/niujiazhen/RuoYi-Vue-master"));
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






    @Override
    public String generateCoverageReport(String codePath, ArrayList<String> testPaths) throws Exception {
        // Step 1: Create test directory if it doesn't exist
        Path srcTestDir = Paths.get("ruoyi-system/src/main/java/test");
        if (!Files.exists(srcTestDir)) {
            Files.createDirectories(srcTestDir);
        }

        // Step 2: Copy source file and add package declaration
        Path copiedCodePath = srcTestDir.resolve(new File(codePath).getName());
        copyAndModifyFile(Paths.get(codePath), copiedCodePath, "test");
        System.out.println("Copied code file to: " + copiedCodePath.toAbsolutePath());

        // Step 3: Copy and modify test files
        List<Path> copiedTestPaths = new ArrayList<>();
        for (String testPath : testPaths) {
            Path copiedTestPath = srcTestDir.resolve(new File(testPath).getName());
            copyAndModifyFile(Paths.get(testPath), copiedTestPath, "test");
            copiedTestPaths.add(copiedTestPath);
            System.out.println("Copied test file to: " + copiedTestPath.toAbsolutePath());
        }

        // Step 2: Read and instrument source code
        String instrumentedCode = instrumentCode(copiedCodePath.toString());

        // Write instrumented code back to the file
        try (FileWriter out = new FileWriter(copiedCodePath.toString())) {
            out.write(instrumentedCode);
        }

        // Step 3: Compile source code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, copiedCodePath.toString());

        // Step 4: Compile test code
        for (Path copiedTestPath : copiedTestPaths) {
            compiler.run(null, null, null, copiedTestPath.toString());
        }


        // Step 5: Run tests and capture output
        List<String> outputs = new ArrayList<>();
        for (Path copiedTestPath : copiedTestPaths) {
            String classFilePath = copiedTestPath.toString().replace(".java", ".class");
            String testOutput = runTest(classFilePath);
            outputs.add(testOutput);
        }

        // Step 6: Analyze coverage and generate report
        String report = analyzeCoverage(outputs);

//        // Step 7: Delete copied files
//        Files.delete(copiedCodePath);
//        for (Path copiedTestPath : copiedTestPaths) {
//            Files.delete(copiedTestPath);
//        }

        return report;
    }
    private void copyAndModifyFile(Path sourcePath, Path destinationPath, String packageName) throws IOException {
        // Read content of source file
        List<String> lines = Files.readAllLines(sourcePath);

        // Add package declaration to the beginning of the content
        lines.add(0, "package " + packageName + ";");

        // Write modified content to destination file
        Files.write(destinationPath, lines);
    }

    private String instrumentCode(String filePath) throws Exception {
        FileInputStream in = new FileInputStream(filePath);
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new Exception("Parsing error"));
        in.close();

        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt n, Void arg) {
                super.visit(n, arg);
                if (n.getCondition() instanceof BinaryExpr) {
                    BinaryExpr condition = (BinaryExpr) n.getCondition();
                    condition.replace(new MethodCallExpr("logCondition", condition.clone()));
                }
            }
        }, null);

        cu.addImport("static " + CoverageServiceImpl.class.getName() + ".logCondition");

        return cu.toString();
    }

    public static boolean logCondition(boolean condition) {
        // Log the condition evaluation
        System.out.println("Condition evaluated: " + condition);
        return condition;
    }

    private String getClassName(String filePath) {
        String fileName = new File(filePath).getName();
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex != -1) {
            return fileName.substring(0, lastIndex);
        } else {
            // 如果没有找到扩展名，则直接返回文件名
            return fileName;
        }
    }


    private String runTest(String classFilePath) {
        try {
            // 创建自定义类加载器以加载测试类所在的目录
            ClassLoader customClassLoader = new URLClassLoader(new URL[]{new File(classFilePath).getParentFile().toURI().toURL()});

            // 获取测试类的类名（去掉后缀名）
            String className = "test."+getClassName(classFilePath);

            // 加载测试类
            Class<?> testClass = customClassLoader.loadClass(className);

            // 创建测试运行器
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(testClass))
                    .build();
            Launcher launcher = LauncherFactory.create();

            // 运行测试类
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);

            // 获取测试结果
            TestExecutionSummary summary = listener.getSummary();
            return summary.getTestsSucceededCount() + " tests succeeded, " +
                    summary.getTestsFailedCount() + " tests failed.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while running tests.";
        }
    }






    private String analyzeCoverage(List<String> outputs) {
        // Analyze the captured output for coverage information
        int totalConditions = 0;
        int coveredConditions = 0;

        for (String output : outputs) {
            String[] lines = output.split("\n");
            for (String line : lines) {
                if (line.startsWith("Condition evaluated: ")) {
                    totalConditions++;
                    if (Boolean.parseBoolean(line.split(": ")[1])) {
                        coveredConditions++;
                    }
                }
            }
        }

        double coverage = (double) coveredConditions / totalConditions * 100;
        return "MC/DC Coverage: " + coverage + "%";
    }

    private void addPackageDeclaration(Path filePath, String packageName) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty() || !lines.get(0).startsWith("package ")) {
            lines.add(0, "package " + packageName + ";");
            Files.write(filePath, lines);
        }
    }




    public static void main(String[] args) throws Exception {
        String codePath = "D:/manager/code/pro2.java";
        ArrayList<String> testPaths = new ArrayList<>();
        testPaths.add("D:/manager/test/pro2Test1.java");
        testPaths.add("D:/manager/test/pro2Test2.java");
        testPaths.add("D:/manager/test/pro2Test3.java");
        testPaths.add("D:/manager/test/pro2Test4.java");

        CoverageServiceImpl coverageService = new CoverageServiceImpl();
        String report = coverageService.generateCoverageReport(codePath, testPaths);
        System.out.println(report);
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
