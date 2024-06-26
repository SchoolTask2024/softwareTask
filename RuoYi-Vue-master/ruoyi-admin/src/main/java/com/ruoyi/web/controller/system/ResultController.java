package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Result;
import com.ruoyi.system.domain.ResultTest;
import com.ruoyi.system.service.IResultService;
import com.ruoyi.system.service.IResultTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码运行Controller
 * 
 * @author niujiazhen
 * @date 2024-04-26
 */
@RestController
@RequestMapping("/codeRunning/result")
public class ResultController extends BaseController
{
    @Autowired
    private IResultService resultService;
    @Autowired
    private IResultTestService resultTestService;

    /**
     * 查询代码运行列表
     */
    @PreAuthorize("@ss.hasPermi('codeRunning:result:list')")
    @GetMapping("/list")
    public TableDataInfo list(Result result)
    {
        startPage();
        List<Result> list = resultService.selectResultList(result);
        return getDataTable(list);
    }

    /**
     * 导出代码运行列表
     */
    @PreAuthorize("@ss.hasPermi('codeRunning:result:export')")
    @Log(title = "代码运行", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Result result)
    {
        List<Result> list = resultService.selectResultList(result);
        ExcelUtil<Result> util = new ExcelUtil<Result>(Result.class);
        util.exportExcel(response, list, "代码运行数据");
    }

    /**
     * 获取代码运行详细信息
     */
    @PreAuthorize("@ss.hasPermi('codeRunning:result:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(resultService.selectResultById(id));
    }

    /**
     *
     * @param id
     */
    @GetMapping(value = "/analysis/{id}")
    public AjaxResult getResultTest(@PathVariable("id") Long id) {
        return success(resultService.selectResultByCodeId(id));

    }
    /**
     * 新增代码运行
     */
    @PreAuthorize("@ss.hasPermi('codeRunning:result:add')")
    @Log(title = "代码运行", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Result result)
    {
        result.setUserId(getUserId());
        result.setTime(LocalDateTime.now());
        resultService.calculateMcDc(result);
        resultService.insertResult(result);
        for (Long id:result.getTestIds()){
            resultTestService.insertResultTest(new ResultTest(result.getId(),id));
        }
        return toAjax(1);
    }

    /**
     * 修改代码运行
     */
    @PreAuthorize("@ss.hasPermi('codeRunning:result:edit')")
    @Log(title = "代码运行", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Result result)
    {
        return toAjax(resultService.updateResult(result));
    }

    /**
     * 删除代码运行
     */
    @PreAuthorize("@ss.hasPermi('codeRunning:result:remove')")
    @Log(title = "代码运行", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(resultService.deleteResultByIds(ids));
    }
}
