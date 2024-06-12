package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.CoverageData;
import com.ruoyi.system.domain.InstrumentData;
import com.ruoyi.system.service.ICommonCoverageService;
import com.ruoyi.system.service.ICoverageCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
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
public class CoverageCServiceImpl implements ICoverageCService{
    @Autowired
    private ICommonCoverageService commonCoverageService;
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
                        return "编译失败: " + commonCoverageService.getProcessOutput(gccProcess);
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
            coverage += "Condition"+(i+1)+": "+commonCoverageService.calculateCoverage(cDataArray[i])+"\n";
        }
        //加上时间戳
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"); // Format the date and time as YYYYMMDD_HHMMSS
        String dateTimeStr = now.format(dateTimeFormatter);
        newContent += "Coverage Data: \n"+coverage;
        String name = "_coverage_"+dateTimeStr+commonCoverageService.getFileType();
        try {
            Path filePath = Paths.get(cFilePath);
            name = filePath.getFileName().toString().replace(".c", "")+name;
            Files.write(Path.of(commonCoverageService.getResultPath() +"\\"+ name), newContent.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return name;

    }

    /**
     * 行覆盖率
     */
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
                return "编译失败: " + commonCoverageService.getProcessOutput(gccProcess);
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
                return "运行失败: " + commonCoverageService.getProcessOutput(runProcess);
            }

            // 生成覆盖率报告
            ProcessBuilder gcovBuilder = new ProcessBuilder("gcov", fileName);
            gcovBuilder.directory(new File(fileDir));
            Process gcovProcess = gcovBuilder.start();
            if (gcovProcess.waitFor() != 0) {
                return "生成覆盖率报告失败: " + commonCoverageService.getProcessOutput(gcovProcess);
            }

            // 读取并解析覆盖率报告
            return parseGcovReport(new File(fileDir, fileName + ".gcov").getAbsolutePath());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "执行失败：" + e.getMessage();
        }
    }
    /**
     * 插装
     * @param filePath 文件地址
     */
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
}
