package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.ICoverageService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Arthur
 */
@Service
public class CoverageServiceImpl implements ICoverageService {
    @Override
    public String generateCoverageReport(File codeFile) throws IOException {
        // 设置工作目录
        File workDir = new File(System.getProperty("java.io.tmpdir"), "codeCoverage");
        if (!workDir.exists()) {
            workDir.mkdirs();
        }

        // 复制上传的文件到工作目录
        Path destination = Paths.get(workDir.getPath(), codeFile.getName());
        Files.copy(codeFile.toPath(), destination);

        // 编译并运行gcov
        String fileName = codeFile.getName();
        String compileCommand = String.format("gcc -fprofile-arcs -ftest-coverage %s -o %s", fileName, "test");
        String runCommand = "./test";
        String gcovCommand = String.format("gcov %s", fileName);

        executeCommand(compileCommand, workDir);
        executeCommand(runCommand, workDir);
        executeCommand(gcovCommand, workDir);

        // 使用lcov生成覆盖率报告
        String lcovCommand = "lcov --capture --directory . --output-file coverage.info";
        String genHtmlCommand = "genhtml coverage.info --output-directory out";

        executeCommand(lcovCommand, workDir);
        executeCommand(genHtmlCommand, workDir);

        // 返回生成的覆盖率报告路径
        return new File(workDir, "out/index.html").getAbsolutePath();
    }
    @Override
    public void executeCommand(String command, File dir) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        processBuilder.directory(dir);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Command failed: " + command);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Command interrupted: " + command, e);
        }
    }
}
