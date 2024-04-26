package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 测试对象 result_test
 * 
 * @author Arthur
 * @date 2024-04-26
 */
public class ResultTest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long resultId;

    private Long testId;

    public ResultTest(Long resultId, Long testId) {
        this.resultId = resultId;
        this.testId = testId;
    }

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setResultId(Long resultId) 
    {
        this.resultId = resultId;
    }

    public Long getResultId() 
    {
        return resultId;
    }
    public void setTestId(Long testId) 
    {
        this.testId = testId;
    }

    public Long getTestId() 
    {
        return testId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("resultId", getResultId())
            .append("testId", getTestId())
            .toString();
    }
}
