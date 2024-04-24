package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Code;
import com.ruoyi.system.service.ICodeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 代码列表Controller
 * 
 * @author Arthur
 * @date 2024-04-24
 */
@RestController
@RequestMapping("/code/codeList")
public class CodeController extends BaseController
{
    @Autowired
    private ICodeService codeService;

    /**
     * 查询代码列表列表
     */
    @PreAuthorize("@ss.hasPermi('code:codeList:list')")
    @GetMapping("/list")
    public TableDataInfo list(Code code)
    {
        startPage();
        List<Code> list = codeService.selectCodeList(code);
        return getDataTable(list);
    }

    /**
     * 导出代码列表列表
     */
    @PreAuthorize("@ss.hasPermi('code:codeList:export')")
    @Log(title = "代码列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Code code)
    {
        List<Code> list = codeService.selectCodeList(code);
        ExcelUtil<Code> util = new ExcelUtil<Code>(Code.class);
        util.exportExcel(response, list, "代码列表数据");
    }

    /**
     * 获取代码列表详细信息
     */
    @PreAuthorize("@ss.hasPermi('code:codeList:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(codeService.selectCodeById(id));
    }

    /**
     * 新增代码列表
     */
    @PreAuthorize("@ss.hasPermi('code:codeList:add')")
    @Log(title = "代码列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Code code)
    {
        return toAjax(codeService.insertCode(code));
    }

    /**
     * 修改代码列表
     */
    @PreAuthorize("@ss.hasPermi('code:codeList:edit')")
    @Log(title = "代码列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Code code)
    {
        return toAjax(codeService.updateCode(code));
    }

    /**
     * 删除代码列表
     */
    @PreAuthorize("@ss.hasPermi('code:codeList:remove')")
    @Log(title = "代码列表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(codeService.deleteCodeByIds(ids));
    }
}
