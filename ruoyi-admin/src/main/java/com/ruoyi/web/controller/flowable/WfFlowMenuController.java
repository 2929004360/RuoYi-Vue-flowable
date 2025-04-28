package com.ruoyi.web.controller.flowable;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flowable.domain.WfFlowMenu;
import com.ruoyi.flowable.service.IWfFlowMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流程菜单Controller
 *
 * @author fengcheng
 * @date 2024-07-12
 */
@RestController
@RequestMapping("/flowable/wfFlowMenu" )
public class WfFlowMenuController extends BaseController {
    @Autowired
    private IWfFlowMenuService wfFlowMenuService;

    /**
     * 查询流程菜单列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfFlowMenu:list')")
    @GetMapping("/list" )
    public TableDataInfo list(WfFlowMenu wfFlowMenu) {
        startPage();
        List<WfFlowMenu> list = wfFlowMenuService.selectWfFlowMenuList(wfFlowMenu);
        return getDataTable(list);
    }

    /**
     * 导出流程菜单列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfFlowMenu:export')")
    @Log(title = "流程菜单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export" )
    public void export(HttpServletResponse response, WfFlowMenu wfFlowMenu) {
        List<WfFlowMenu> list = wfFlowMenuService.selectWfFlowMenuList(wfFlowMenu);
        ExcelUtil<WfFlowMenu> util = new ExcelUtil<WfFlowMenu>(WfFlowMenu.class);
        util.exportExcel(response, list, "流程菜单数据" );
    }

    /**
     * 获取流程菜单详细信息
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfFlowMenu:query')")
    @GetMapping(value = "/{flowMenuId}" )
    public AjaxResult getInfo(@PathVariable("flowMenuId" ) String flowMenuId) {
        return success(wfFlowMenuService.selectWfFlowMenuByFlowMenuId(flowMenuId));
    }

    /**
     * 获取流程菜单详细信息
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfFlowMenu:query')")
    @GetMapping(value = "/getWfFlowMenuInfo/{menuId}" )
    public AjaxResult getWfFlowMenuInfo(@PathVariable("menuId" ) String menuId) {
        return success(wfFlowMenuService.getWfFlowMenuInfo(menuId));
    }

    /**
     * 新增流程菜单
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfFlowMenu:add')")
    @Log(title = "流程菜单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfFlowMenu wfFlowMenu) {
        return toAjax(wfFlowMenuService.insertWfFlowMenu(wfFlowMenu));
    }

    /**
     * 修改流程菜单
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfFlowMenu:edit')")
    @Log(title = "流程菜单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfFlowMenu wfFlowMenu) {
        return toAjax(wfFlowMenuService.updateWfFlowMenu(wfFlowMenu));
    }

    /**
     * 删除流程菜单
     *
     * @param flowMenuIds
     * @return
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfFlowMenu:remove')")
    @Log(title = "流程菜单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{flowMenuIds}" )
    public AjaxResult remove(@PathVariable Long[] flowMenuIds) {
        return toAjax(wfFlowMenuService.deleteWfFlowMenuByFlowMenuIds(flowMenuIds));
    }
}
