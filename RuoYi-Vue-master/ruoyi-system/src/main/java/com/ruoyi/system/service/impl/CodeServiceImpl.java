package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CodeMapper;
import com.ruoyi.system.domain.Code;
import com.ruoyi.system.service.ICodeService;

/**
 * 代码列表Service业务层处理
 * 
 * @author Arthur
 * @date 2024-04-24
 */
@Service
public class CodeServiceImpl implements ICodeService 
{
    @Autowired
    private CodeMapper codeMapper;

    /**
     * 查询代码列表
     * 
     * @param id 代码列表主键
     * @return 代码列表
     */
    @Override
    public Code selectCodeById(Long id)
    {
        return codeMapper.selectCodeById(id);
    }

    /**
     * 查询代码列表列表
     * 
     * @param code 代码列表
     * @return 代码列表
     */
    @Override
    public List<Code> selectCodeList(Code code)
    {
        return codeMapper.selectCodeList(code);
    }
    @Override
    public List<Code> selectCodeName()
    {
        return codeMapper.selectCodeName();
    }

    /**
     * 新增代码列表
     * 
     * @param code 代码列表
     * @return 结果
     */
    @Override
    public int insertCode(Code code)
    {
        return codeMapper.insertCode(code);
    }

    /**
     * 修改代码列表
     * 
     * @param code 代码列表
     * @return 结果
     */
    @Override
    public int updateCode(Code code)
    {
        return codeMapper.updateCode(code);
    }

    /**
     * 批量删除代码列表
     * 
     * @param ids 需要删除的代码列表主键
     * @return 结果
     */
    @Override
    public int deleteCodeByIds(Long[] ids)
    {
        return codeMapper.deleteCodeByIds(ids);
    }

    /**
     * 删除代码列表信息
     * 
     * @param id 代码列表主键
     * @return 结果
     */
    @Override
    public int deleteCodeById(Long id)
    {
        return codeMapper.deleteCodeById(id);
    }
}
