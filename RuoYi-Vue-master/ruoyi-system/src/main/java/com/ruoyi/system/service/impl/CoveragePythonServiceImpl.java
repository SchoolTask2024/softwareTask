package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICommonCoverageService;
import com.ruoyi.system.service.ICoverageCalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        return "";
    }
}
