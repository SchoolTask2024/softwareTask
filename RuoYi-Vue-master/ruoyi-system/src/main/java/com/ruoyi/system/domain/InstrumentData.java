package com.ruoyi.system.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstrumentData {
    private String code;
    private Integer counter;
    private String filePath;
}
