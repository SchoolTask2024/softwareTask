package com.ruoyi.system.service;

import com.ruoyi.system.domain.FIleLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Arthur
 */
public interface ICoverageService {

    String generateCoverageReport(FIleLocation codePath1, ArrayList<FIleLocation> testPath1) throws Exception;


    //获取MC/DC覆盖率
    String generateCMCDCCoverage(String cFilePath, ArrayList<String> testFilePaths);

}
