package com.ruoyi.system.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Arthur
 */
public interface ICoverageService {
    String generateCoverageReport(String codePath, ArrayList<String> testPath) throws IOException;
}
