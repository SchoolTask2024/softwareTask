package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.AnalysisCoverage;
import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICoverageCalculateService;
import com.ruoyi.system.service.ICoverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CoverageServiceImpl implements ICoverageService {
    @Qualifier("coverageCServiceImpl")
    @Autowired
    private ICoverageCalculateService  coverageCService;
    @Qualifier("coverageJavaServiceImpl")
    @Autowired
    private ICoverageCalculateService  coverageJavaService;
    @Qualifier("coveragePythonServiceImpl")
    @Autowired
    private ICoverageCalculateService  coveragePythonService;

    @Override
    public AnalysisCoverage getCoverageFileC(String cFilePath, ArrayList<String> testFilePaths){
        return coverageCService.generateMCDCCoverageNew(cFilePath, testFilePaths);
    }
    @Override
    public AnalysisCoverage getCoverageFilePython(String cFilePath, ArrayList<String> testFilePaths){
        return coveragePythonService.generateMCDCCoverageNew(cFilePath, testFilePaths);
    }
    @Override
    public String getCoverageFileJava(FIleLocation codePath, ArrayList<FIleLocation> testPaths){
        return coverageJavaService.generateCoverageReport(codePath, testPaths);
    }


}
