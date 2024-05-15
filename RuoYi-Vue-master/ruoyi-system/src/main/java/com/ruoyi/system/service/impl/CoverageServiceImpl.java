package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.ICoverageService;
import org.springframework.stereotype.Service;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Arthur
 */
@Service
public class CoverageServiceImpl implements ICoverageService {
    @Override
    public String generateCoverageReport(String codePath, ArrayList<String> testPath) throws IOException {
        try {
            // TODO: 执行你的测试代码

            // 生成JaCoCo执行文件加载器
            ExecFileLoader loader = new ExecFileLoader();
            loader.load(new File("target/jacoco.exec"));

            // 创建HTML格式化器
            HTMLFormatter htmlFormatter = new HTMLFormatter();

            // 创建XML格式化器
            XMLFormatter xmlFormatter = new XMLFormatter();

            // 创建目录源文件定位器
            DirectorySourceFileLocator locator = new DirectorySourceFileLocator(new File("src/main/java"), "UTF-8", 4);

            // 创建HTML报告
//            IReportVisitor htmlVisitor = htmlFormatter.createVisitor(new FileOutputStream("target/site/jacoco/index.html"));
//            htmlVisitor.visitInfo(loader.getSessionInfoStore().getInfos(), loader.getExecutionDataStore().getContents());
//            htmlVisitor.visitBundle(loader.getExecutionDataStore(), locator);
//
//            // 创建XML报告
//            IReportVisitor xmlVisitor = xmlFormatter.createVisitor(new FileOutputStream("target/site/jacoco/report.xml"));
//            xmlVisitor.visitInfo(loader.getSessionInfoStore().getInfos(), loader.getExecutionDataStore().getContents());
//            xmlVisitor.visitBundle(loader.getExecutionDataStore(), locator);

            // 关闭报告访问者
//            htmlVisitor.visitEnd();
//            xmlVisitor.visitEnd();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

}
