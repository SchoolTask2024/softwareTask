package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码运行对象 result
 * 
 * @author niujiazhen
 * @date 2024-04-26
 */
public class Result extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 运行结果id */
    private Long id;

    /** 代码id */
    @Excel(name = "代码id")
    private Long codeId;

    /** 储存路径 */
    @Excel(name = "储存路径")
    private String path;

    /** 执行人id */
    @Excel(name = "执行人id")
    private Long userId;

    /** 运行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private ArrayList<Long> testIds;

    /** 覆盖率 */
    @Excel(name = "覆盖率")
    private String coverageRate;

    /** 测试名称 */
    @Excel(name = "测试名称")
    private String resultName;

    private String userName;
    private String codeName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public ArrayList<Long> getTestIds() {
        return testIds;
    }

    public void setTestIds(ArrayList<Long> testIds) {
        this.testIds = testIds;
    }

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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
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
            .append("resultName", getResultName()).append("testIds",getTestIds())
            .toString();
    }
}
