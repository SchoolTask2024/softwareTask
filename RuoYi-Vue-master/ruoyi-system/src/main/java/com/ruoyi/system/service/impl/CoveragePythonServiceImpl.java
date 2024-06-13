package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.FIleLocation;
import com.ruoyi.system.service.ICoverageCalculateService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class CoveragePythonServiceImpl implements ICoverageCalculateService {
    @Override
    public String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPaths) throws IOException, InterruptedException {
        return "";
    }

    @Override
    public String generateMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths) {
        return "";
    }
}
