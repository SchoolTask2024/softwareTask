package com.ruoyi.system.service;

import com.ruoyi.system.domain.AnalysisCoverage;
import com.ruoyi.system.domain.FIleLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Arthur
 */
public interface ICoverageService {


    AnalysisCoverage getCoverageFileC(String cFilePath, ArrayList<String> testFilePaths);

    AnalysisCoverage getCoverageFilePython(String cFilePath, ArrayList<String> testFilePaths);

    String getCoverageFileJava(FIleLocation codePath, ArrayList<FIleLocation> testPaths);
}
