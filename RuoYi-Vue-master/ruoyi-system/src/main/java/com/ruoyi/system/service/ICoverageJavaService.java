package com.ruoyi.system.service;

import com.ruoyi.system.domain.FIleLocation;

import java.io.IOException;
import java.util.ArrayList;

public interface ICoverageJavaService {
    String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPaths) throws IOException, InterruptedException;
}
