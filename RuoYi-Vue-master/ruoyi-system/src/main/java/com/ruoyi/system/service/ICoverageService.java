package com.ruoyi.system.service;

import com.ruoyi.system.domain.FIleLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Arthur
 */
public interface ICoverageService {


    String getCoverageFileC(String cFilePath, ArrayList<String> testFilePaths);

    String getCoverageFilePython(String cFilePath, ArrayList<String> testFilePaths);

    String getCoverageFileJava(FIleLocation codePath, ArrayList<FIleLocation> testPaths);
}
