package com.ruoyi.system.domain;

import lombok.Data;

import java.util.List;

@Data
public class BaseData {
    private Integer id;
    private String name;
    private String values;
    private List<Float> value;
}
