package com.ruoyi.system.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 雷达图的Indicator
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Indicator {
    private String name;
    private final Integer max = 1;
}
