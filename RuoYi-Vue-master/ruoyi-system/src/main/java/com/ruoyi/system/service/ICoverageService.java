package com.ruoyi.system.service;

import com.ruoyi.system.domain.FIleLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Arthur
 */
public interface ICoverageService {

    String generateCoverageReport(FIleLocation codePath, ArrayList<FIleLocation> testPath) throws IOException;
}
