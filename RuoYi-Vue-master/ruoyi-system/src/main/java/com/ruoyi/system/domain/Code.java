package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 代码列表对象 code
 * 
 * @author Arthur
 * @date 2024-04-24
 */
public class Code extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 代码id */
    private Long id;

    /** 代码名称 */
    @Excel(name = "代码名称")
    private String name;

    /** 代码路径 */
    private String path;

    /** 代码状态 */
    private Long status;

    /** 导入人员 */
    @Excel(name = "导入人员")
    private Long userId;

    /** 导入时间 */
    @Excel(name = "导入时间")
    private String time;

    /** 代码类型 */
    @Excel(name = "代码类型")
    private Long type;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setPath(String path) 
    {
        this.path = path;
    }

    public String getPath() 
    {
        return path;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
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
    public void setType(Long type) 
    {
        this.type = type;
    }

    public Long getType() 
    {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("path", getPath())
            .append("status", getStatus())
            .append("userId", getUserId())
            .append("time", getTime())
            .append("remark", getRemark())
            .append("type", getType())
            .toString();
    }
}
