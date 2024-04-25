package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Result;

/**
 * 代码运行Mapper接口
 * 
 * @author niujiazhen
 * @date 2024-04-25
 */
public interface ResultMapper 
{
    /**
     * 查询代码运行
     * 
     * @param id 代码运行主键
     * @return 代码运行
     */
    public Result selectResultById(Long id);

    /**
     * 查询代码运行列表
     * 
     * @param result 代码运行
     * @return 代码运行集合
     */
    public List<Result> selectResultList(Result result);

    /**
     * 新增代码运行
     * 
     * @param result 代码运行
     * @return 结果
     */
    public int insertResult(Result result);

    /**
     * 修改代码运行
     * 
     * @param result 代码运行
     * @return 结果
     */
    public int updateResult(Result result);

    /**
     * 删除代码运行
     * 
     * @param id 代码运行主键
     * @return 结果
     */
    public int deleteResultById(Long id);

    /**
     * 批量删除代码运行
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteResultByIds(Long[] ids);
}
