package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 代码运行对象 result
 * 
 * @author niujiazhen
 * @date 2024-04-25
 */
public class Result extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 执行结果id */
    private Long id;

    /** 代码id */
    @Excel(name = "代码id")
    private Long codeId;

    /** 测试批次id */
    @Excel(name = "测试批次id")
    private Long batchId;

    /** 存储路径 */
    private String path;

    /** 执行人员id */
    @Excel(name = "执行人员id")
    private Long userId;

    /** 执行时间 */
    @Excel(name = "执行时间")
    private String time;

    /** 覆盖率 */
    @Excel(name = "覆盖率")
    private String coverageRate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCodeId(Long codeId) 
    {
        this.codeId = codeId;
    }

    public Long getCodeId() 
    {
        return codeId;
    }
    public void setBatchId(Long batchId) 
    {
        this.batchId = batchId;
    }

    public Long getBatchId() 
    {
        return batchId;
    }
    public void setPath(String path) 
    {
        this.path = path;
    }

    public String getPath() 
    {
        return path;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setTime(String time) 
    {
        this.time = time;
    }

    public String getTime() 
    {
        return time;
    }
    public void setCoverageRate(String coverageRate) 
    {
        this.coverageRate = coverageRate;
    }

    public String getCoverageRate() 
    {
        return coverageRate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("codeId", getCodeId())
            .append("batchId", getBatchId())
            .append("path", getPath())
            .append("userId", getUserId())
            .append("time", getTime())
            .append("coverageRate", getCoverageRate())
            .toString();
    }
}
