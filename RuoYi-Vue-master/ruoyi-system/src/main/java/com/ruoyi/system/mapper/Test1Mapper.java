package com.ruoyi.system.mapper;

import java.util.ArrayList;
import java.util.List;
import com.ruoyi.system.domain.Test1;

/**
 * 测试列表Mapper接口
 * 
 * @author niujiazhen
 * @date 2024-04-24
 */
public interface Test1Mapper 
{
    /**
     * 查询测试列表
     * 
     * @param id 测试列表主键
     * @return 测试列表
     */
    public Test1 selectTest1ById(Long id);

    /**
     * 查询测试列表列表
     * 
     * @param test1 测试列表
     * @return 测试列表集合
     */
    public List<Test1> selectTest1List(Test1 test1);
    public List<Test1> selectTestListByCodeName(String codeName);

    /**
     * 新增测试列表
     * 
     * @param test1 测试列表
     * @return 结果
     */
    public int insertTest1(Test1 test1);

    /**
     * 修改测试列表
     * 
     * @param test1 测试列表
     * @return 结果
     */
    public int updateTest1(Test1 test1);

    /**
     * 删除测试列表
     * 
     * @param id 测试列表主键
     * @return 结果
     */
    public int deleteTest1ById(Long id);

    /**
     * 批量删除测试列表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTest1ByIds(Long[] ids);


    public ArrayList<String> selectPathsByIds(Long[] ids);
}
