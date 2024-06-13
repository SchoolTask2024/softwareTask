package com.ruoyi.system.service.impl;


import com.ruoyi.system.service.ICoverageCalculateService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CoveragePythonServiceImpl implements ICoverageCalculateService {
    @Override
    public String generateCMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths) {
        return "";
    }
}
