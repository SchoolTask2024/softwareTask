package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ResultTestMapper;
import com.ruoyi.system.domain.ResultTest;
import com.ruoyi.system.service.IResultTestService;

/**
 * 测试Service业务层处理
 * 
 * @author Arthur
 * @date 2024-04-26
 */
@Service
public class ResultTestServiceImpl implements IResultTestService 
{
    @Autowired
    private ResultTestMapper resultTestMapper;

    /**
     * 查询测试
     * 
     * @param id 测试主键
     * @return 测试
     */
    @Override
    public ResultTest selectResultTestById(Long id)
    {
        return resultTestMapper.selectResultTestById(id);
    }

    /**
     * 查询测试列表
     * 
     * @param resultTest 测试
     * @return 测试
     */
    @Override
    public List<ResultTest> selectResultTestList(ResultTest resultTest)
    {
        return resultTestMapper.selectResultTestList(resultTest);
    }

    /**
     * 新增测试
     * 
     * @param resultTest 测试
     * @return 结果
     */
    @Override
    public int insertResultTest(ResultTest resultTest)
    {
        return resultTestMapper.insertResultTest(resultTest);
    }

    /**
     * 修改测试
     * 
     * @param resultTest 测试
     * @return 结果
     */
    @Override
    public int updateResultTest(ResultTest resultTest)
    {
        return resultTestMapper.updateResultTest(resultTest);
    }

    /**
     * 批量删除测试
     * 
     * @param ids 需要删除的测试主键
     * @return 结果
     */
    @Override
    public int deleteResultTestByIds(Long[] ids)
    {
        return resultTestMapper.deleteResultTestByIds(ids);
    }

    /**
     * 删除测试信息
     * 
     * @param id 测试主键
     * @return 结果
     */
    @Override
    public int deleteResultTestById(Long id)
    {
        return resultTestMapper.deleteResultTestById(id);
    }
}
