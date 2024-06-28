package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Test1Optimize;

/**
 * 测试用例集优化Service接口
 * 
 * @author njz
 * @date 2024-06-26
 */
public interface ITest1OptimizeService 
{
    /**
     * 查询测试用例集优化
     * 
     * @param id 测试用例集优化主键
     * @return 测试用例集优化
     */
    public Test1Optimize selectTest1OptimizeById(Long id);

    /**
     * 查询测试用例集优化列表
     * 
     * @param test1Optimize 测试用例集优化
     * @return 测试用例集优化集合
     */
    public List<Test1Optimize> selectTest1OptimizeList(Test1Optimize test1Optimize);

    /**
     * 新增测试用例集优化
     * 
     * @param test1Optimize 测试用例集优化
     * @return 结果
     */
    public int insertTest1Optimize(Test1Optimize test1Optimize);

    /**
     * 修改测试用例集优化
     * 
     * @param test1Optimize 测试用例集优化
     * @return 结果
     */
    public int updateTest1Optimize(Test1Optimize test1Optimize);

    /**
     * 批量删除测试用例集优化
     * 
     * @param ids 需要删除的测试用例集优化主键集合
     * @return 结果
     */
    public int deleteTest1OptimizeByIds(Long[] ids);

    /**
     * 删除测试用例集优化信息
     * 
     * @param id 测试用例集优化主键
     * @return 结果
     */
    public int deleteTest1OptimizeById(Long id);
}