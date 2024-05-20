package com.ruoyi.system.service;

import com.ruoyi.system.domain.FIleLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Arthur
 */
public interface ICoverageService {

    public String generateJavaCoverageReport(String codePath, ArrayList<String> testPaths) throws Exception;

    String generateC(String codePath, ArrayList<String> execFilePaths);
}
