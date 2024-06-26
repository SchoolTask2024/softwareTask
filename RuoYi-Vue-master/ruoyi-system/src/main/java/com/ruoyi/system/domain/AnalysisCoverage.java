package com.ruoyi.system.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计数据
 * example:
 * conditions ->["condition1", "condition2", "condition3"]
 * coverageData ->[1, 2, 3]
 */
@Data
@NoArgsConstructor
public class AnalysisCoverage {
    private Long id;
    private String resultName;
    private String time;
    private String conditions;
    private String coverageData;
    private String path;
}
