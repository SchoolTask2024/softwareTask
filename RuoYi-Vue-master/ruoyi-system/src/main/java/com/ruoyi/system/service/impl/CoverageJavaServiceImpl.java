package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.ICommonCoverageService;
import com.ruoyi.system.service.ICoverageJavaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoverageJavaServiceImpl implements ICoverageJavaService {
    @Autowired
    private ICommonCoverageService commonCoverageService;

}
