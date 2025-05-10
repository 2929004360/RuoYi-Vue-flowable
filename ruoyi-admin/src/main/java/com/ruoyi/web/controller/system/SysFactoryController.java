package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.entity.SysFactory;
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
import com.ruoyi.system.service.ISysFactoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 厂区Controller
 *
 * @author ruoyi
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/system/factory")
public class SysFactoryController extends BaseController
{
    @Autowired
    private ISysFactoryService sysFactoryService;

    /**
     * 查询厂区列表
     */
    @PreAuthorize("@ss.hasPermi('system:factory:list')")
    @GetMapping("/listFactoryVo")
    public AjaxResult listFactoryVo()
    {
        List<SysFactory> list = sysFactoryService.selectSysFactoryList(new SysFactory());
        return success(list);
    }

    /**
     * 查询厂区列表
     */
    @PreAuthorize("@ss.hasPermi('system:factory:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFactory sysFactory)
    {
        startPage();
        List<SysFactory> list = sysFactoryService.selectSysFactoryList(sysFactory);
        return getDataTable(list);
    }

    /**
     * 导出厂区列表
     */
    @PreAuthorize("@ss.hasPermi('system:factory:export')")
    @Log(title = "厂区", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFactory sysFactory)
    {
        List<SysFactory> list = sysFactoryService.selectSysFactoryList(sysFactory);
        ExcelUtil<SysFactory> util = new ExcelUtil<SysFactory>(SysFactory.class);
        util.exportExcel(response, list, "厂区数据");
    }

    /**
     * 获取厂区详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:factory:query')")
    @GetMapping(value = "/{factoryId}")
    public AjaxResult getInfo(@PathVariable("factoryId") Long factoryId)
    {
        return success(sysFactoryService.selectSysFactoryByFactoryId(factoryId));
    }

    /**
     * 新增厂区
     */
    @PreAuthorize("@ss.hasPermi('system:factory:add')")
    @Log(title = "厂区", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysFactory sysFactory)
    {
        return toAjax(sysFactoryService.insertSysFactory(sysFactory));
    }

    /**
     * 修改厂区
     */
    @PreAuthorize("@ss.hasPermi('system:factory:edit')")
    @Log(title = "厂区", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysFactory sysFactory)
    {
        return toAjax(sysFactoryService.updateSysFactory(sysFactory));
    }

    /**
     * 删除厂区
     */
    @PreAuthorize("@ss.hasPermi('system:factory:remove')")
    @Log(title = "厂区", businessType = BusinessType.DELETE)
	@DeleteMapping("/{factoryIds}")
    public AjaxResult remove(@PathVariable Long[] factoryIds)
    {
        return toAjax(sysFactoryService.deleteSysFactoryByFactoryIds(factoryIds));
    }
}
