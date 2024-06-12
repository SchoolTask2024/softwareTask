package com.ruoyi.system.service;

import java.util.ArrayList;

public interface ICoverageCService {
    String generateCMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths);

    String generateC(String cFilePath, ArrayList<String> testFilePaths);
}
