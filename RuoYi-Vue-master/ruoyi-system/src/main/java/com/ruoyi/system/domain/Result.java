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

    /**  */
    private Long id;

    /**  */
    @Excel(name = "")
    private Long codeId;

    /**  */
    @Excel(name = "")
    private String path;

    /**  */
    @Excel(name = "")
    private Long userId;

    /**  */
    @Excel(name = "")
    private String time;

    /**  */
    @Excel(name = "")
    private String coverageRate;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String resultName;

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
    public void setResultName(String resultName) 
    {
        this.resultName = resultName;
    }

    public String getResultName() 
    {
        return resultName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("codeId", getCodeId())
            .append("path", getPath())
            .append("userId", getUserId())
            .append("time", getTime())
            .append("coverageRate", getCoverageRate())
            .append("resultName", getResultName())
            .toString();
    }
}
