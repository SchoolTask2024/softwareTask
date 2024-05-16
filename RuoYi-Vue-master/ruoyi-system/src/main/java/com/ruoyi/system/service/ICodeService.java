package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Code;

/**
 * 代码列表Service接口
 * 
 * @author Arthur
 * @date 2024-04-24
 */
public interface ICodeService 
{
    /**
     * 查询代码列表
     * 
     * @param id 代码列表主键
     * @return 代码列表
     */
    public Code selectCodeById(Long id);

    /**
     * 查询代码列表列表
     * 
     * @param code 代码列表
     * @return 代码列表集合
     */
    public List<Code> selectCodeList(Code code);

    public List<Code> selectCodeName();

    /**
     * 新增代码列表
     * 
     * @param code 代码列表
     * @return 结果
     */
    public int insertCode(Code code);

    /**
     * 修改代码列表
     * 
     * @param code 代码列表
     * @return 结果
     */
    public int updateCode(Code code);

    /**
     * 批量删除代码列表
     * 
     * @param ids 需要删除的代码列表主键集合
     * @return 结果
     */
    public int deleteCodeByIds(Long[] ids);

    /**
     * 删除代码列表信息
     * 
     * @param id 代码列表主键
     * @return 结果
     */
    public int deleteCodeById(Long id);
}
