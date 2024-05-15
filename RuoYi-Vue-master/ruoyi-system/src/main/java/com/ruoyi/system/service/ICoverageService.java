package com.ruoyi.system.service;

import java.io.File;
import java.io.IOException;

/**
 * @author Arthur
 */
public interface ICoverageService {
    String generateCoverageReport(File codeFile) throws IOException;

}
