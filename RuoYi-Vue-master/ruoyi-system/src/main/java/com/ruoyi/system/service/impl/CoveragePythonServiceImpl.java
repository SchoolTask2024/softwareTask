package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.AnalysisCoverage;
import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICommonCoverageService;
import com.ruoyi.system.service.ICoverageCalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CoveragePythonServiceImpl implements ICoverageCalculateService {
    @Autowired
    private ICommonCoverageService commonCoverageService;
    @Override
    public String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPaths){
        return "";
    }

    @Override
    public String generateMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths) {
        String outputPath = "D:\\output1.txt"; // 输出文件路径
        System.out.println(cFilePath);
        //1、合并用例文件
        try {
            // 创建输出流
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            // 逐一读取每个文件的内容
            for (String filePath : testFilePaths) {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = reader.readLine()) != null) {
                    // 写入输出文件
                    writer.write(line);
                    writer.newLine();
                }
                reader.close();
            }
            // 关闭输出流
            writer.close();
            System.out.println("Files merged successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2、传参给python并且获取python文件输出
        try {
            String pythonScriptDir ="D:\\python code\\software1";
            ProcessBuilder pb = new ProcessBuilder("python", "coverage_test.py",cFilePath,outputPath);
            pb.directory(new File(pythonScriptDir));

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            try (BufferedReader readerTwo = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String lineTwo;
                while ((lineTwo = reader.readLine()) != null) {
                    System.out.println(lineTwo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public AnalysisCoverage generateMCDCCoverageNew(String cFilePath, ArrayList<String> testFilePaths) {
        String outputPath = "D:/output1.txt"; // 输出文件路径
        System.out.println(cFilePath);
        //1、合并用例文件
        try {
            // 创建输出流
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            // 逐一读取每个文件的内容
            for (String filePath : testFilePaths) {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = reader.readLine()) != null) {
                    // 写入输出文件
                    writer.write(line);
                    writer.newLine();
                }
                reader.close();
            }
            // 关闭输出流
            writer.close();
            System.out.println("Files merged successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
//2、传参给python并且获取python文件输出
        String lastLine = null; // 存储最后一行内容
        try {
            String pythonScriptDir ="D:/manager/software1";
            ProcessBuilder pb = new ProcessBuilder("python", "coverage_test.py",cFilePath,outputPath);
            pb.directory(new File(pythonScriptDir));

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 输出每一行内容
                lastLine = line; // 每次循环更新最后一行内容
            }
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.out.println(errorLine); // 输出错误流内容
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] line=lastLine.split("@");
        // 使用正则表达式提取最后一个斜杠后的内容
        String fileName = null;
        if (line[0] != null) {
            Pattern pattern = Pattern.compile("[^/]+$");
            Matcher matcher = pattern.matcher(line[0]);
            if (matcher.find()) {
                fileName = matcher.group();
            }
        }
        AnalysisCoverage analysis=new AnalysisCoverage();
        analysis.setPath(fileName);
        analysis.setConditions(line[1]);
        analysis.setCoverageData(line[2]);
        return analysis; // 返回对象
    }
}
