package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Test1Optimize;
import com.ruoyi.system.service.ITest1OptimizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 测试用例集优化Controller
 * 
 * @author njz
 * @date 2024-06-26
 */
@RestController
@RequestMapping("/test1/test1Optimize")
public class Test1OptimizeController extends BaseController
{
    @Autowired
    private ITest1OptimizeService test1OptimizeService;

    /**
     * 查询测试用例集优化列表
     */
    @PreAuthorize("@ss.hasPermi('test1:test1Optimize:list')")
    @GetMapping("/list")
    public TableDataInfo list(Test1Optimize test1Optimize)
    {
        startPage();
        List<Test1Optimize> list = test1OptimizeService.selectTest1OptimizeList(test1Optimize);
        return getDataTable(list);
    }

    /**
     * 导出测试用例集优化列表
     */
    @PreAuthorize("@ss.hasPermi('test1:test1Optimize:export')")
    @Log(title = "测试用例集优化", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Test1Optimize test1Optimize)
    {
        List<Test1Optimize> list = test1OptimizeService.selectTest1OptimizeList(test1Optimize);
        ExcelUtil<Test1Optimize> util = new ExcelUtil<Test1Optimize>(Test1Optimize.class);
        util.exportExcel(response, list, "测试用例集优化数据");
    }

    /**
     * 获取测试用例集优化详细信息
     */
    @PreAuthorize("@ss.hasPermi('test1:test1Optimize:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(test1OptimizeService.selectTest1OptimizeById(id));
    }

    /**
     * 新增测试用例集优化
     */
    @PreAuthorize("@ss.hasPermi('test1:test1Optimize:add')")
    @Log(title = "测试用例集优化", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Test1Optimize test1Optimize)
    {
        return toAjax(test1OptimizeService.insertTest1Optimize(test1Optimize));
    }

    /**
     * 执行测试用例集优化
     */
    @PreAuthorize("@ss.hasPermi('test1:test1Optimize:edit')")
    @Log(title = "测试用例集优化", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Test1Optimize test1Optimize)
    {
        return toAjax(test1OptimizeService.updateTest1Optimize(test1Optimize));
    }

    /**
     * 删除测试用例集优化
     */
    @PreAuthorize("@ss.hasPermi('test1:test1Optimize:remove')")
    @Log(title = "测试用例集优化", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(test1OptimizeService.deleteTest1OptimizeByIds(ids));
    }
}
