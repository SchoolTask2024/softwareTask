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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class CoverageJavaServiceImpl implements ICoverageCalculateService {
    @Autowired
    private ICommonCoverageService commonCoverageService;

    private final Object lock = new Object(); // 创建一个对象用于加锁

    // JavaCoverageReport
    public void calculateJavaMCDC(ArrayList<FIleLocation> testPaths, String sourceCodeFilename) throws IOException, InterruptedException {
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
            executeMavenCommand();

            String filePath = "ruoyi-system/target/site/jacoco/default/" + sourceCodeFilename + ".html";
            System.out.println(filePath);
            Queue<Element> siblingQueue = parser(filePath);
            siblingQueues.add(siblingQueue);

            // 删除复制的文件
            Files.delete(targetTestCodePath);
        }

        ArrayList<Boolean> filteredQueue = new ArrayList<>();
        // 输出每个队列的元素信息
        ArrayList<ArrayList<CoverageData>> cDataArray = new ArrayList<>();

        for (int i = 0; i < siblingQueues.size(); i++) {
            Queue<Element> siblingQueue = siblingQueues.get(i);
            System.out.println("\n第 " + (i + 1) + " 个文件的含有 bpc 类的元素的下一个兄弟元素：");

            ArrayList<CoverageData> javaDataArray = new ArrayList<>();
            while (!siblingQueue.isEmpty()) {
                Element nextElement = siblingQueue.poll();
                String className = nextElement.className();
                CoverageData data = new CoverageData();

                if (i == 0) {
                    data.setParam("4 3 2");
                } else {
                    data.setParam("4 2 3");
                }
                // 根据类别筛选
                if (className.contains("nc")) {
                    data.setResult(false);
                } else {
                    data.setResult(true);
                }
                javaDataArray.add(data);
                System.out.println(data);
            }
            cDataArray.add(javaDataArray);
        }

        ArrayList<ArrayList<CoverageData>> transposedArray = transpose(cDataArray);

        for (ArrayList i : transposedArray) {
            System.out.println(commonCoverageService.calculateCoverage(i));
        }
    }

    private void executeMavenCommand() throws IOException, InterruptedException {
        // 构建 Maven 命令
        ProcessBuilder processBuilder = new ProcessBuilder("mvn","test", "jacoco:report");
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
            if (!inputFile.exists()) {
                System.out.println("File not found: " + path);
                return siblingQueue;
            }

            // 使用 Jsoup 解析 HTML 文件
            Document doc = Jsoup.parse(inputFile, "UTF-8");

            // 获取所有包含 "bpc"、"bfc"、"bnc" 类的元素
            Elements elements = doc.select("span.bpc, span.bfc, span.bnc");
            List<Element> bpcElements = new ArrayList<>();

            for (Element element : elements) {
                bpcElements.add(element);
                // 获取包含 "bpc" 类的行号
                String id = element.id();
                if (id.startsWith("L")) {
                    try {
                        int lineNumber = Integer.parseInt(id.substring(1));
                        // 获取下一个兄弟元素并放入队列
                        Element nextElement = getNextSiblingElement(element);
                        if (nextElement != null) {
                            siblingQueue.add(nextElement);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid line number format in id: " + id);
                    }
                }
            }

            // 输出含有 "bpc"、"bfc"、"bnc" 类的行号
            System.out.println("文件 " + path + " 含有 bpc、bfc、bnc 类的行号：");
            for (Element bpcElement : bpcElements) {
                String lineNumber = bpcElement.id().substring(1); // 获取行号
                System.out.println(lineNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred while parsing the HTML file: " + e.getMessage());
        }

        return siblingQueue;
    }

    // 辅助方法：获取元素的下一个兄弟元素（排除空白文本节点）
    private Element getNextSiblingElement(Element element) {
        Element sibling = element.nextElementSibling();
        return sibling; // 可能返回 null，调用方需处理
    }

    @Override
    public String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPaths) {
        // 目标目录
        String targetDir = "ruoyi-system/src/main/java";

        // 复制源代码文件
        String sourceCodeFilename = codePath.getFilename();
        Path sourceCodePath = Paths.get(codePath.getFilepath());
        Path targetSourceCodePath = Paths.get(targetDir, sourceCodeFilename);
        try {
            Files.copy(sourceCodePath, targetSourceCodePath, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            calculateJavaMCDC(testPaths, sourceCodeFilename);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // 返回 JaCoCo 报告的路径
        return "ruoyi-system/target/site/jacoco";
    }

    @Override
    public String generateMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths) {
        return "";
    }
}
