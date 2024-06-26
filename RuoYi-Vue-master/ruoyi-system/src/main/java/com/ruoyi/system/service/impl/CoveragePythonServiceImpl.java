package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.AnalysisCoverage;
import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICommonCoverageService;
import com.ruoyi.system.service.ICoverageCalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;

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
        return null;
    }
}
