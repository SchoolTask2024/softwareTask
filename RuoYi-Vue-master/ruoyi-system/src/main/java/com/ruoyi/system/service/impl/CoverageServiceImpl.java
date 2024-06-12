package com.ruoyi.system.service.impl;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.ruoyi.system.domain.CoverageData;
import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.domain.InstrumentData;
import com.ruoyi.system.service.ICoverageService;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.xml.XMLFormatter;
import org.junit.platform.launcher.core.LauncherFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class CoverageServiceImpl implements ICoverageService {


    @Value("${result.path}")
    private String resultPath;
    private final String fileType = ".txt";


    /**
     * 生成C语言的MCDC覆盖率
     * @param cFilePath C文件路径
     * @param testFilePaths 测试文件路径列表
     * @return 返回行覆盖率获取MC/DC覆盖率
     */
    @Override
    public String generateCMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths) {
        String newContent = "instrumented code:\n";
        InstrumentData instrumentData = instrumentC(cFilePath);
        newContent += instrumentData.getCode()+"\n";
        ArrayList<CoverageData>[] cDataArray = new ArrayList[instrumentData.getCounter()];
        for (int i = 0; i < instrumentData.getCounter(); i++) {
            cDataArray[i] = new ArrayList<>();
        }
        for (String testFilePath : testFilePaths) {
            //测试参数
            try (BufferedReader reader = new BufferedReader(new FileReader(testFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    //设置参数
                    String[] parameters = line.trim().split(" ");
                    newContent += "parameters:  "+line+"\n";
                    //将c语言里面的scanf代码替换为参数并获取文件
                    File cFile = new File(replaceScanfWithParameter(instrumentData.getFilePath(), parameters));
                    if (!cFile.exists()) {
                        return "文件不存在：" + cFilePath;
                    }
                    String fileName = cFile.getName();
                    String fileDir = cFile.getParent();
                    String exeName = fileName.replace(".c", ".exe");
//                    ProcessBuilder gccBuilder = new ProcessBuilder("gcc", "-fprofile-arcs", "-ftest-coverage", "-o", exeName, fileName);
                    ProcessBuilder gccBuilder = new ProcessBuilder("gcc","-o", exeName, fileName);
                    gccBuilder.directory(new File(fileDir));
                    Process gccProcess = gccBuilder.start();
                    if (gccProcess.waitFor() != 0) {
                        return "编译失败: " + getProcessOutput(gccProcess);
                    }
                    //编译完成后删除代码
                    cFile.delete();
                    //运行C代码
                    ProcessBuilder runBuilder = new ProcessBuilder("cmd", "/c", exeName);
                    runBuilder.directory(new File(fileDir));
                    Process runProcess = runBuilder.start();
                    BufferedReader readerC = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                    String totalPrint = "";
                    String lineC;
                    while ((lineC = readerC.readLine()) != null) {
                        totalPrint += lineC;
                    }
                    newContent +="result: "+totalPrint+"\n";
                    ArrayList<Integer> counters = getCounters(totalPrint);
                    for(int i = 0; i < instrumentData.getCounter(); i++){
                        CoverageData data = new CoverageData();
                        data.setParam(line.trim());
                        data.setResult(counters.contains(i+1));
                        cDataArray[i].add(data);
                    }
                    runProcess.waitFor();
                    //运行结束后删除exe文件
                    File exeFile = new File(fileDir, exeName);
                    exeFile.delete();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                return "读取测试文件失败：" + e.getMessage();
            }

        }
        //删除instrumented.c文件
        File instrumentedFile = new File(instrumentData.getFilePath());
        instrumentedFile.delete();
        //计算覆盖率
        String coverage ="";
        for (int i = 0; i < instrumentData.getCounter(); i++) {
            coverage += "Condition"+(i+1)+": "+calculateCoverage(cDataArray[i])+"\n";
        }
        //加上时间戳
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"); // Format the date and time as YYYYMMDD_HHMMSS
        String dateTimeStr = now.format(dateTimeFormatter);
        newContent += "Coverage Data: \n"+coverage;
        String name = "_coverage_"+dateTimeStr+fileType;
        try {
            Path filePath = Paths.get(cFilePath);
            name = filePath.getFileName().toString().replace(".c", "")+name;
            Files.write(Path.of(resultPath +"\\"+ name), newContent.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return name;

    }
    private ArrayList<Integer> getCounters(String print){
        String regex = "Condition (\\d+) is true!";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(print);
        ArrayList<Integer> counters = new ArrayList<>();

        // Find all matches and add the counter values to the list
        while (matcher.find()) {
            // matcher.group(1) is the first capturing group, which is the number
            int counter = Integer.parseInt(matcher.group(1));
            counters.add(counter);
        }
        return counters;

    }
    private InstrumentData instrumentC(String filePath) {
        try {
            InstrumentData instrumentData = new InstrumentData();
            // Read the content of the C file
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            // Regular expression to find if conditions
            String ifConditionRegex = "(if\\s*\\(.*?\\)|else\\s+if\\s*\\(.*?\\)|else)\\s*\\{([^}]*)\\}";
            // Pattern and Matcher for finding if conditions
            Pattern pattern = Pattern.compile(ifConditionRegex);
            Matcher matcher = pattern.matcher(content);

            StringBuilder modifiedContent = new StringBuilder();
            int counter = 0;
            while (matcher.find()) {
                counter++;
                String body = matcher.group(2).trim();

                // Insert printf statement into the body of if condition
                String modifiedBody = body + "\nprintf(\"Condition " + counter + " is true!\");\n";

                // Replace original body with modified body
                matcher.appendReplacement(modifiedContent, matcher.group(1) + " {" + modifiedBody + "}\n");
            }

            // Append the rest of the content after the last match
            matcher.appendTail(modifiedContent);
            // Write modified content back to the file
            String newFilePath = filePath.replace(".c","_instrumented.c");
            Files.write(Paths.get(newFilePath), modifiedContent.toString().getBytes());
            instrumentData.setFilePath(newFilePath);
            instrumentData.setCounter(counter);
            instrumentData.setCode(modifiedContent.toString());
            return instrumentData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String replaceScanfWithParameter(String filePath, String[] parameters){
        StringBuilder modifiedCode = new StringBuilder();
        int counter = 0;
        String newFilePath = filePath.replace(".c","_final.c");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 使用正则表达式匹配scanf语句
                String regex = "\\bscanf\\s*\\(.*?\\)\\s*;";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                // 如果匹配到了scanf语句，则替换为参数赋值
                if (matcher.find()) {
                    String replacedLine = line.replace(matcher.group(), parameters[counter]+";");
                    modifiedCode.append(replacedLine).append("\n");
                    counter++;
                } else {
                    // 如果没有匹配到scanf语句，则保持原样
                    modifiedCode.append(line).append("\n");
                }
            }
            Files.write(Paths.get(newFilePath), modifiedCode.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFilePath;
    }
    private Integer compare(ArrayList<Integer> a, ArrayList<Integer> b){
        if(a.size() != b.size()){
            return -1;
        }
        boolean flag = false;
        Integer index = -1;
        for(int i = 0; i < a.size(); i++){
            if(a.get(i) != b.get(i)){
                if(!flag){
                    flag = true;
                    index = i;
                    continue;
                }
                if(flag){
                    return -1;
                }
            }
        }
        return index;
    }
    private String calculateCoverage(ArrayList<CoverageData> cData){
        ArrayList<ArrayList<Integer>> trueList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> falseList = new ArrayList<>();

        for (CoverageData data : cData) {
            ArrayList<Integer> paramList = parseParam(data.getParam());
            if (data.getResult()) {
                if (!containsList(trueList, paramList)) {
                    trueList.add(paramList);
                }
            } else {
                if (!containsList(falseList, paramList)) {
                    falseList.add(paramList);
                }
            }
        }

        int testsize;
        if(trueList.size()!=0){
            testsize=trueList.get(0).size();
        }else {
            testsize=falseList.get(0).size();
        }

        int[] result = new int[testsize]; // 用于记录结果的数组


        for (int i = 0; i < trueList.size(); i++) {
            ArrayList<Integer> row0 = trueList.get(i);
            for(int j =0 ;j<falseList.size();j++){
                ArrayList<Integer> row1 = falseList.get(j);
                if(compare(row0,row1)!=-1){
                    result[compare(row0,row1)]=1;
                }
//                System.out.println(i);
            }

        }

        double great=0;
        double all=result.length;
        // 打印结果数组
        System.out.println("Comparison result:");
        for (int i = 0; i < result.length; i++) {
            if(result[i]==1){
                great++;
            }
//            System.out.println("array[" + i + "] = " + result[i]);
        }

        return String.valueOf(great/all);
    }
    // 检查列表中是否包含某个 ArrayList<Integer>
    private boolean containsList(ArrayList<ArrayList<Integer>> list, ArrayList<Integer> targetList) {
        for (ArrayList<Integer> innerList : list) {
            if (innerList.equals(targetList)) {
                return true;
            }
        }
        return false;
    }
    // 解析 param 字符串为 ArrayList<Integer>
    private ArrayList<Integer> parseParam(String param) {
        ArrayList<Integer> list = new ArrayList<>();
        String[] tokens = param.split("\\s+");
        for (String token : tokens) {
            list.add(Integer.parseInt(token));
        }
        return list;
    }
    //生成行覆盖率
    @Override
    public String generateC(String cFilePath, ArrayList<String> testFilePaths) {
        File cFile = new File(cFilePath);
        if (!cFile.exists()) {
            return "文件不存在：" + cFilePath;
        }

        StringBuilder combinedTestContent = new StringBuilder();

        // 读取测试文件内容并合并
        for (String testFilePath : testFilePaths) {
            try (BufferedReader reader = new BufferedReader(new FileReader(testFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    combinedTestContent.append(line).append(" ");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "读取测试文件失败：" + e.getMessage();
            }
        }
        try {
            // 获取文件名和目录
            String fileName = cFile.getName();
            String fileDir = cFile.getParent();
            String exeName = fileName.replace(".c", ".exe");

            // 编译C代码
            ProcessBuilder gccBuilder = new ProcessBuilder("gcc", "-fprofile-arcs", "-ftest-coverage", "-o", exeName, fileName);
            gccBuilder.directory(new File(fileDir));
            Process gccProcess = gccBuilder.start();
            if (gccProcess.waitFor() != 0) {
                return "编译失败: " + getProcessOutput(gccProcess);
            }

            // 运行生成的可执行文件并传递合并后的测试文件内容作为参数
            String[] parameters = combinedTestContent.toString().split(" ");
            List<String> command = new ArrayList<>();
            command.add("cmd");
            command.add("/c");
            command.add(exeName);
            command.addAll(Arrays.asList(parameters));
            ProcessBuilder runBuilder = new ProcessBuilder(command);
            runBuilder.directory(new File(fileDir));
            Process runProcess = runBuilder.start();
            if (runProcess.waitFor() != 0) {
                return "运行失败: " + getProcessOutput(runProcess);
            }

            // 生成覆盖率报告
            ProcessBuilder gcovBuilder = new ProcessBuilder("gcov", fileName);
            gcovBuilder.directory(new File(fileDir));
            Process gcovProcess = gcovBuilder.start();
            if (gcovProcess.waitFor() != 0) {
                return "生成覆盖率报告失败: " + getProcessOutput(gcovProcess);
            }

            // 读取并解析覆盖率报告
            return parseGcovReport(new File(fileDir, fileName + ".gcov").getAbsolutePath());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "执行失败：" + e.getMessage();
        }
    }
    // 获取进程输出
    private String getProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    // 解析gcov生成的报告文件并计算覆盖率
    private String parseGcovReport(String gcovFilePath) throws IOException {
        int totalLines = 0;
        int executedLines = 0;
        StringBuilder report = new StringBuilder();
        File gcovFile = new File(gcovFilePath);
        if (!gcovFile.exists()) {
            return "gcov报告文件不存在：" + gcovFilePath;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(gcovFile)))) {
            String line;
            Pattern pattern = Pattern.compile("^\\s*(\\d+):\\s*(\\d+|-)");

            while ((line = reader.readLine()) != null) {
                report.append(line).append("\n");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String execCount = matcher.group(2);
                    if (!execCount.equals("-")) {
                        int count = Integer.parseInt(execCount);
                        if (count > 0) {
                            executedLines++;
                        }
                    }
                }
                totalLines = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        double coverage = totalLines > 0 ? (double) executedLines / totalLines * 100 : 0;
        report.append(String.format("总行数: %d, 执行行数: %d, 覆盖率: %.2f%%", totalLines, executedLines, coverage));
//        return report.toString();
        return String.valueOf(coverage);
    }











    public String generateCoverageReport1(FIleLocation codePath1, ArrayList<FIleLocation> testPath1) throws Exception {
        String codePath = codePath1.getFilepath();
        ArrayList<String> testPaths = new ArrayList<>();

        // 使用 for-each 循环将每个 FileLocation 的 getFilepath() 添加到 testPaths
        for (FIleLocation testLocation : testPath1) {
            testPaths.add(testLocation.getFilepath());
        }
        // Step 1: Create test directory if it doesn't exist
        Path srcTestDir = Paths.get("ruoyi-system/src/main/java/test");
        if (!Files.exists(srcTestDir)) {
            Files.createDirectories(srcTestDir);
        }

        // Step 2: Copy source file and add package declaration
        String codeFileName = codePath1.getFilename();  // 获取文件名
        Path copiedCodePath = srcTestDir.resolve(codeFileName);
        copyAndModifyFile(Paths.get(codePath), copiedCodePath, "test");
        System.out.println("Copied code file to: " + copiedCodePath.toAbsolutePath());

        // Step 3: Copy and modify test files
        List<Path> copiedTestPaths = new ArrayList<>();
        for (FIleLocation testLocation : testPath1) {
            String testFileName = testLocation.getFilename();  // 获取文件名
            Path copiedTestPath = srcTestDir.resolve(testFileName);
            copyAndModifyFile(Paths.get(testLocation.getFilepath()), copiedTestPath, "test");
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



    private final Object lock = new Object(); // 创建一个对象用于加锁
    //JavaCoverageReport
    public void calculateJavaMCDC(ArrayList<FIleLocation> testPaths,String sourceCodeFilename) throws IOException, InterruptedException{
        List<Queue<Element>> siblingQueues = new ArrayList<>();
        int htmlsize = testPaths.size();
        System.out.println(htmlsize);
        String targetDir = "ruoyi-system/src/main/java";
        List<String> testCodeFilenames = new ArrayList<>();

        for (FIleLocation testPath : testPaths) {
            String testFilename = testPath.getFilename();
            Path testPathFull = Paths.get(testPath.getFilepath());
            Path targetTestCodePath = Paths.get(targetDir, testFilename);

            // 复制测试代码文件
            Files.copy(testPathFull, targetTestCodePath, StandardCopyOption.REPLACE_EXISTING);
            testCodeFilenames.add(testFilename);

            // 在每次循环中执行 Maven 命令（加锁）
            synchronized (lock) {
                try {
                    executeMavenCommand();
                } catch (InterruptedException e) {
                    // 处理 InterruptedException，可以根据需要进行日志记录或其他操作
                    Thread.currentThread().interrupt(); // 重新设置线程的中断状态
                }
            }

            String filePath = "ruoyi-system/target/site/jacoco/default/" + sourceCodeFilename + ".html";
            Queue<Element> siblingQueue = parser(filePath);
            siblingQueues.add(siblingQueue);

            // 删除复制的文件
            Files.delete(targetTestCodePath);
        }



        ArrayList<Boolean> filteredQueue = new ArrayList<Boolean>();
        // 输出每个队列的元素信息
        ArrayList<ArrayList<CoverageData>> cDataArray = new ArrayList();

        for (int i = 0; i < siblingQueues.size(); i++) {
            Queue<Element> siblingQueue = siblingQueues.get(i);
            System.out.println("\n第 " + (i + 1) + " 个文件的含有 bpc 类的元素的下一个兄弟元素：");

            // 生成新队列


            ArrayList<CoverageData> javaDataArray = new ArrayList<>();
            while (!siblingQueue.isEmpty()) {
                Element nextElement = siblingQueue.poll();
                String className = nextElement.getAttribute("class");
                CoverageData data = new CoverageData();

                if (i == 0) {
                    data.setParam("4 3 2");
                }else {
                    data.setParam("4 2 3");
                }
                // 根据类别筛选
                if (className.contains("nc")) {
                    // 添加到新队列
                    data.setResult(false);
                }else {
                    data.setResult(true);
                }
                //System.out.println(data);
                javaDataArray.add(data);
            }
            cDataArray.add(javaDataArray);
        }

        ArrayList<ArrayList<CoverageData>> transposedArray = transpose(cDataArray);




        for(ArrayList i:transposedArray) {
            System.out.println(calculateCoverage(i));
        }
    }

    private void executeMavenCommand() throws IOException, InterruptedException {
        // 构建 Maven 命令
        ProcessBuilder processBuilder = new ProcessBuilder("mvn", "test", "jacoco:report");
        processBuilder.directory(new File("ruoyi-system")); // 项目根目录
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Maven command failed with exit value " + exitCode);
        }
    }



    // 方法：转置 ArrayList<ArrayList<CoverageData>>
    private ArrayList<ArrayList<CoverageData>> transpose(ArrayList<ArrayList<CoverageData>> original) {
        ArrayList<ArrayList<CoverageData>> transposed = new ArrayList<>();

        // 获取原始数组的行数和列数
        int rows = original.size();
        int cols = original.get(0).size();

        // 遍历原始数组进行转置
        for (int col = 0; col < cols; col++) {
            ArrayList<CoverageData> newRow = new ArrayList<>();
            for (int row = 0; row < rows; row++) {
                newRow.add(original.get(row).get(col));
            }
            transposed.add(newRow);
        }

        return transposed;
    }
    private Queue<Element> parser(String path) {
        Queue<Element> siblingQueue = new LinkedList<>();

        try {
            // 读取 HTML 文件
            File inputFile = new File(path);

            // 创建 DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 创建 DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 解析 HTML 文件
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // 获取所有包含 "bpc"、"bfc"、"bnc" 类的元素
            NodeList nodeList = doc.getElementsByTagName("span");
            List<Element> bpcElements = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String className = element.getAttribute("class");
                if (className.contains("bpc") || className.contains("bfc") || className.contains("bnc")) {
                    bpcElements.add(element);
                    // 获取包含 "bpc" 类的行号
                    String id = element.getAttribute("id");
                    if (id.startsWith("L")) {
                        int lineNumber = Integer.parseInt(id.substring(1));
                        // 获取下一个兄弟元素并放入队列
                        Element nextElement = getNextSiblingElement(element);
                        if (nextElement != null) {
                            siblingQueue.add(nextElement);
                        }
                    }
                }
            }

            // 输出含有 "bpc"、"bfc"、"bnc" 类的行号
            System.out.println("文件 " + path + " 含有 bpc、bfc、bnc 类的行号：");
            for (Element bpcElement : bpcElements) {
                String lineNumber = bpcElement.getAttribute("id").substring(1); // 获取行号
                System.out.println(lineNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return siblingQueue;
    }

    // 辅助方法：获取元素的下一个兄弟元素（排除空白文本节点）
    private Element getNextSiblingElement(Element element) {
        Node sibling = element.getNextSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling; // 可能返回 null，调用方需处理
    }

    @Override
    public String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPaths) throws IOException, InterruptedException {
        // 目标目录
        String targetDir = "ruoyi-system/src/main/java";

        // 复制源代码文件
        String sourceCodeFilename = codePath.getFilename();
        Path sourceCodePath = Paths.get(codePath.getFilepath());
        Path targetSourceCodePath = Paths.get(targetDir, sourceCodeFilename);
        Files.copy(sourceCodePath, targetSourceCodePath, REPLACE_EXISTING);

        calculateJavaMCDC(testPaths,sourceCodeFilename);

        // 返回 JaCoCo 报告的路径
        return "ruoyi-system/target/site/jacoco";
    }

}
