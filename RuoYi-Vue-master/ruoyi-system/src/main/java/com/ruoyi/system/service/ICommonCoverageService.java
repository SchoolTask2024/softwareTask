package com.ruoyi.system.service;

import com.ruoyi.system.domain.CoverageData;

import java.io.IOException;
import java.util.ArrayList;

public interface ICommonCoverageService {
    String getResultPath();

    String getFileType();

    String calculateCoverage(ArrayList<CoverageData> cData);

    // 获取进程输出
    String getProcessOutput(Process process) throws IOException;
}
