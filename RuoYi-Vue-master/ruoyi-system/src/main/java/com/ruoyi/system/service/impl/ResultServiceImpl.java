package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ResultMapper;
import com.ruoyi.system.domain.Result;
import com.ruoyi.system.service.IResultService;

/**
 * 代码运行Service业务层处理
 * 
 * @author niujiazhen
 * @date 2024-04-25
 */
@Service
public class ResultServiceImpl implements IResultService 
{
    @Autowired
    private ResultMapper resultMapper;

    /**
     * 查询代码运行
     * 
     * @param id 代码运行主键
     * @return 代码运行
     */
    @Override
    public Result selectResultById(Long id)
    {
        return resultMapper.selectResultById(id);
    }

    /**
     * 查询代码运行列表
     * 
     * @param result 代码运行
     * @return 代码运行
     */
    @Override
    public List<Result> selectResultList(Result result)
    {
        return resultMapper.selectResultList(result);
    }

    /**
     * 新增代码运行
     * 
     * @param result 代码运行
     * @return 结果
     */
    @Override
    public int insertResult(Result result)
    {
        return resultMapper.insertResult(result);
    }

    /**
     * 修改代码运行
     * 
     * @param result 代码运行
     * @return 结果
     */
    @Override
    public int updateResult(Result result)
    {
        return resultMapper.updateResult(result);
    }

    /**
     * 批量删除代码运行
     * 
     * @param ids 需要删除的代码运行主键
     * @return 结果
     */
    @Override
    public int deleteResultByIds(Long[] ids)
    {
        return resultMapper.deleteResultByIds(ids);
    }

    /**
     * 删除代码运行信息
     * 
     * @param id 代码运行主键
     * @return 结果
     */
    @Override
    public int deleteResultById(Long id)
    {
        return resultMapper.deleteResultById(id);
    }
}
