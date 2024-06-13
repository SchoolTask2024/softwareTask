package com.ruoyi.system.service;

import com.ruoyi.system.domain.FIleLocation;

import java.io.IOException;
import java.util.ArrayList;

public interface ICoverageCalculateService {
    String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPaths);

    String generateMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths);
}
