package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.CoverageData;
import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICommonCoverageService;
import com.ruoyi.system.service.ICoverageCalculateService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class CoverageJavaServiceImpl implements ICoverageCalculateService {
    @Autowired
    private ICommonCoverageService commonCoverageService;


    @Override
    public String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPaths) {
        // 目标目录
        String targetDir = "ruoyi-system/src/main/java";

        // 复制源代码文件
        String sourceCodeFilename = codePath.getFilename();
        Path sourceCodePath = Paths.get(codePath.getFilepath());
        Path targetSourceCodePath = Paths.get(targetDir, sourceCodeFilename);
        try {
            Files.copy(sourceCodePath, targetSourceCodePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to copy source code file.";
        }

        calculateJavaMCDC(testPaths, sourceCodeFilename);

        // 返回 JaCoCo 报告的路径
        return targetDir;
    }

    public void calculateJavaMCDC(ArrayList<FIleLocation> testPaths, String sourceCodeFilename) {
        String targetDir = "ruoyi-system/src/main/java";
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            for (FIleLocation testPath : testPaths) {
                Path testFilePath = Paths.get(testPath.getFilepath());
                Path targetTestFilePath = Paths.get(targetDir, testPath.getFilename());

                System.out.println("Copying test file " + testFilePath + " to " + targetTestFilePath);
                Files.copy(testFilePath, targetTestFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Executing mvn clean test jacoco:report");
                ProcessBuilder processBuilder = new ProcessBuilder("mvn", "clean", "test", "jacoco:report");
                processBuilder.directory(new File("ruoyi-system"));
                Process process = processBuilder.start();

                // 等待进程执行完毕
                synchronized (process) {
                    while (process.isAlive()) {
                        process.wait();
                    }
                }

                // 删除测试文件
                System.out.println("Deleting test file " + targetTestFilePath);
                Files.delete(targetTestFilePath);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            executor.shutdown();
        }
    }




    @Override
    public String generateMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths) {
        return null;
    }

    // 假设 parser 方法签名如下
    private CoverageData parser(String reportPath) {
        // 解析报告的具体逻辑
        return new CoverageData();
    }
}
