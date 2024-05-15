package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.ICoverageService;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Service implementation for generating code coverage reports.
 */
@Service
public class CoverageServiceImpl implements ICoverageService {
    @Override
    public String generateCoverageReport(String codePath, ArrayList<String> testPath) throws IOException {
        try {
            // 1. 运行测试用例
            for (String test : testPath) {
                // 使用 ProcessBuilder 来运行测试用例
                ProcessBuilder builder = new ProcessBuilder("mvn", "-Dtest=" + test, "test");
                builder.redirectErrorStream(true);
                Process process = builder.start();
                process.waitFor();
            }

            // 2. 加载 JaCoCo 执行数据
            ExecFileLoader execFileLoader = new ExecFileLoader();
            execFileLoader.load(new File("target/jacoco.exec"));

            // 3. 分析覆盖率
            CoverageBuilder coverageBuilder = new CoverageBuilder();
            Analyzer analyzer = new Analyzer(execFileLoader.getExecutionDataStore(), coverageBuilder);
            analyzer.analyzeAll(new File(codePath));

            // 4. 生成 HTML 覆盖率报告
            HTMLFormatter htmlFormatter = new HTMLFormatter();
            File reportDir = new File("target/coverage-report");
            IReportVisitor visitor = htmlFormatter.createVisitor(new FileMultiReportOutput(reportDir));

            visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
                    execFileLoader.getExecutionDataStore().getContents());

            for (IClassCoverage cc : coverageBuilder.getClasses()) {
                visitor.visitBundle((IBundleCoverage) cc, new DirectorySourceFileLocator(new File(codePath), "utf-8", 4));
            }

            visitor.visitEnd();

            return "target/coverage-report/index.html";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
