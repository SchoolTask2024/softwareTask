package com.ruoyi.system.service.impl;

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
    public String getCoverageFileC(String cFilePath, ArrayList<String> testFilePaths){
        return coverageCService.generateMCDCCoverage(cFilePath, testFilePaths);
    }
    @Override
    public String getCoverageFilePython(String cFilePath, ArrayList<String> testFilePaths){
        return coveragePythonService.generateMCDCCoverage(cFilePath, testFilePaths);
    }
    @Override
    public String getCoverageFileJava(FIleLocation codePath, ArrayList<FIleLocation> testPaths){
        return coverageJavaService.generateCoverageReport(codePath, testPaths);
    }


}
