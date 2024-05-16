package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Code;

/**
 * 代码列表Mapper接口
 * 
 * @author Arthur
 * @date 2024-04-24
 */
public interface CodeMapper 
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
     * 删除代码列表
     * 
     * @param id 代码列表主键
     * @return 结果
     */
    public int deleteCodeById(Long id);

    /**
     * 批量删除代码列表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCodeByIds(Long[] ids);

    public Code selectPathById(Long id);


}
