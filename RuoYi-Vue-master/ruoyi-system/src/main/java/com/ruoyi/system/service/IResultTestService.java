package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.ResultTest;

/**
 * 测试Service接口
 * 
 * @author Arthur
 * @date 2024-04-26
 */
public interface IResultTestService 
{
    /**
     * 查询测试
     * 
     * @param id 测试主键
     * @return 测试
     */
    public ResultTest selectResultTestById(Long id);

    /**
     * 查询测试列表
     * 
     * @param resultTest 测试
     * @return 测试集合
     */
    public List<ResultTest> selectResultTestList(ResultTest resultTest);

    /**
     * 新增测试
     * 
     * @param resultTest 测试
     * @return 结果
     */
    public int insertResultTest(ResultTest resultTest);

    /**
     * 修改测试
     * 
     * @param resultTest 测试
     * @return 结果
     */
    public int updateResultTest(ResultTest resultTest);

    /**
     * 批量删除测试
     * 
     * @param ids 需要删除的测试主键集合
     * @return 结果
     */
    public int deleteResultTestByIds(Long[] ids);

    /**
     * 删除测试信息
     * 
     * @param id 测试主键
     * @return 结果
     */
    public int deleteResultTestById(Long id);
}
