package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.CoverageData;
import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.domain.InstrumentData;
import com.ruoyi.system.service.ICoverageCService;
import com.ruoyi.system.service.ICoverageJavaService;
import com.ruoyi.system.service.ICoverageService;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.xml.XMLFormatter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ICoverageCService coverageCService;
    @Autowired
    private ICoverageJavaService coverageJavaService;
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
        return coverageCService.generateCMCDCCoverage(cFilePath,testFilePaths);
    }



}
