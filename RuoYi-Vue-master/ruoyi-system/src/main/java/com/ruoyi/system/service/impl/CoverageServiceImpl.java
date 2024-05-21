package com.ruoyi.system.service.impl;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CoverageServiceImpl implements ICoverageService {
    @Value("${result.path}")
     private String resultPath;
    private final String fileType = ".txt";

    //获取MC/DC覆盖率
    @Override
    public String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPath) {
        //需要补充
        return null;
    }
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
