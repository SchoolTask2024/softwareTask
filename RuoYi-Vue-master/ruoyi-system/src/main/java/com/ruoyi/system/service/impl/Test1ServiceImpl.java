package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.Test1Mapper;
import com.ruoyi.system.domain.Test1;
import com.ruoyi.system.service.ITest1Service;

/**
 * 测试列表Service业务层处理
 * 
 * @author niujiazhen
 * @date 2024-04-24
 */
@Service
public class Test1ServiceImpl implements ITest1Service 
{
    @Autowired
    private Test1Mapper test1Mapper;

    /**
     * 查询测试列表
     * 
     * @param id 测试列表主键
     * @return 测试列表
     */
    @Override
    public Test1 selectTest1ById(Long id)
    {
        return test1Mapper.selectTest1ById(id);
    }

    /**
     * 查询测试列表列表
     * 
     * @param test1 测试列表
     * @return 测试列表
     */
    @Override
    public List<Test1> selectTest1List(Test1 test1)
    {
        return test1Mapper.selectTest1List(test1);
    }


    @Override
    public List<Test1> selectTestListByCodeName(String codeName){return test1Mapper.selectTestListByCodeName(codeName);}

    /**
     * 新增测试列表
     * 
     * @param test1 测试列表
     * @return 结果
     */
    @Override
    public int insertTest1(Test1 test1)
    {
        return test1Mapper.insertTest1(test1);
    }

    /**
     * 修改测试列表
     * 
     * @param test1 测试列表
     * @return 结果
     */
    @Override
    public int updateTest1(Test1 test1)
    {
        return test1Mapper.updateTest1(test1);
    }

    /**
     * 批量删除测试列表
     * 
     * @param ids 需要删除的测试列表主键
     * @return 结果
     */
    @Override
    public int deleteTest1ByIds(Long[] ids)
    {
        return test1Mapper.deleteTest1ByIds(ids);
    }

    /**
     * 删除测试列表信息
     * 
     * @param id 测试列表主键
     * @return 结果
     */
    @Override
    public int deleteTest1ById(Long id)
    {
        return test1Mapper.deleteTest1ById(id);
    }
}
