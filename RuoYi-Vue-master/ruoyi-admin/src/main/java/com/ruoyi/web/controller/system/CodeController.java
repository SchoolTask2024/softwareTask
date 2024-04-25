package com.ruoyi.web.controller.system;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

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
    @Value("${code.path}")
    private String codePath;
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
    @PreAuthorize("@ss.hasPermi('code:codeList:list')")
    @GetMapping("/listName")
    public AjaxResult list()
    {
        List<String> list = codeService.selectCodeName();
        return new AjaxResult(200,"获取成功",list);
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
        code.setUserId(getUserId());
        code.setTime(LocalDateTime.now());
        return toAjax(codeService.insertCode(code));
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
            String filePath = codePath;
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
//            ajax.put("url", filePath + "/" + newFileName);
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
