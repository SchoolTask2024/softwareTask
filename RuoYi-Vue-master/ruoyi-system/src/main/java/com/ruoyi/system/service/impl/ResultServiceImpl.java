package com.ruoyi.system.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ruoyi.system.mapper.CodeMapper;
import com.ruoyi.system.mapper.ResultTestMapper;
import com.ruoyi.system.mapper.Test1Mapper;
import com.ruoyi.system.service.ICoverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ResultMapper;
import com.ruoyi.system.domain.Result;
import com.ruoyi.system.service.IResultService;

/**
 * 代码运行Service业务层处理
 * 
 * @author niujiazhen
 * @date 2024-04-26
 */
@Service
public class ResultServiceImpl implements IResultService 
{
    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private Test1Mapper test1Mapper;
    @Autowired
    private ResultTestMapper resultTestMapper;
    @Autowired
    private ICoverageService coverageService;
    @Value("${code.path}")
    private String codeLocalPath;
    @Value("${test.path}")
    private String testLocalPath;
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
        List<Result> resultList = resultMapper.selectResultList(result);
        for(Result r:resultList){
            r.setTest1List(test1Mapper.selectByResultId(r.getId()));
        }
        return resultList;
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
        resultTestMapper.deleteResultTestByResultIds(ids);
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

    /**
     * 计算MC/DC
     */
    @Override
    public void calculateMcDc(Result result){
        String codePath = codeMapper.selectPathById(result.getCodeId());
        ArrayList<String> testPaths = test1Mapper.selectPathsByIds(result.getTestIds().toArray(new Long[0]));
        result.setPath(codePath);
        File codeFile = new File(codeLocalPath +"/"+codePath);
        if (!codeFile.exists() || !codeFile.isFile()) {
            System.out.println("文件不存在");
        }
        else {
            try{
                String reportPath = coverageService.generateCoverageReport(codeFile);
                System.out.println(reportPath);
                System.out.println("-----------------------------------------------");
            }catch (IOException ignored){
                System.out.println("error");
            }
        }
//        for (String path:testPaths){
//            File file = new File(path);
//        }
    }

}
