package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.time.LocalDate;

/**
 * 测试列表对象 test1
 * 
 * @author niujiazhen
 * @date 2024-04-24
 */
public class Test1 extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */

    private String name;

    /** $column.columnComment */
    private String path;

    /** $column.columnComment */
    private Long status;
    private Long codeId;
    private Code code;
    private String importUser;

    private Long userId;
    private String codeName;

    /** $column.columnComment */

    private LocalDate time;

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getImportUser() {
        return importUser;
    }

    public void setImportUser(String importUser) {
        this.importUser = importUser;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

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

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
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
            .toString();
    }
}
