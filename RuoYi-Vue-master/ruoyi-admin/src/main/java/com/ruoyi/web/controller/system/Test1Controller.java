package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Test1;
import com.ruoyi.system.service.ITest1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试列表Controller
 * 
 * @author niujiazhen
 * @date 2024-04-24
 */
@RestController
@RequestMapping("/test1/test1List")
public class Test1Controller extends BaseController
{
    @Autowired
    private ITest1Service test1Service;
    @Value("${test.path}")
    private String testPath;
    /**
     * 查询测试列表列表
     */
    @PreAuthorize("@ss.hasPermi('test1:test1List:list')")
    @GetMapping("/list")
    public TableDataInfo list(Test1 test1)
    {
        startPage();
        List<Test1> list = test1Service.selectTest1List(test1);
        return getDataTable(list);
    }

    /**
     * 导出测试列表列表
     */
    @PreAuthorize("@ss.hasPermi('test1:test1List:export')")
    @Log(title = "测试列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Test1 test1)
    {
        List<Test1> list = test1Service.selectTest1List(test1);
        ExcelUtil<Test1> util = new ExcelUtil<Test1>(Test1.class);
        util.exportExcel(response, list, "测试列表数据");
    }
    /**
     * 上传代码文件
     */
    @PreAuthorize("@ss.hasPermi('code:codeList:add')")
    @Log(title = "上传代码文件", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file)throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = testPath;
            // 获取当前时间戳作为文件名
            String timeStamp = String.valueOf(System.currentTimeMillis());
            // 获取文件的原始名称
            String originalFilename = file.getOriginalFilename();
            // 获取文件的扩展名
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 新文件名为时间戳加上文件扩展名
            String newFileName = timeStamp + fileExtension;
            // 在指定路径下创建新文件
            File newFile = new File(filePath + File.separator + newFileName);
            file.transferTo(newFile);
            // 构造返回结果
            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", newFileName);
            ajax.put("newFileName", newFileName);
            ajax.put("originalFilename", originalFilename);

            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }
    /**
     * 获取测试列表详细信息
     */
    @PreAuthorize("@ss.hasPermi('test1:test1List:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(test1Service.selectTest1ById(id));
    }

    /**
     * 新增测试列表
     */
    @PreAuthorize("@ss.hasPermi('test1:test1List:add')")
    @Log(title = "测试列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Test1 test1)
    {
        test1.setUserId(getUserId());
        test1.setTime(LocalDateTime.now());
        return toAjax(test1Service.insertTest1(test1));
    }

    /**
     * 修改测试列表
     */
    @PreAuthorize("@ss.hasPermi('test1:test1List:edit')")
    @Log(title = "测试列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Test1 test1)
    {
        return toAjax(test1Service.updateTest1(test1));
    }

    /**
     * 删除测试列表
     */
    @PreAuthorize("@ss.hasPermi('test1:test1List:remove')")
    @Log(title = "测试列表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(test1Service.deleteTest1ByIds(ids));
    }
}
