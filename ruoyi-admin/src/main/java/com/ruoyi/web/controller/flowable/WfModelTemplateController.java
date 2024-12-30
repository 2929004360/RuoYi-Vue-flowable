package com.ruoyi.web.controller.flowable;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flowable.domain.WfModelTemplate;
import com.ruoyi.flowable.service.IWfModelTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 模型模板Controller
 *
 * @author fengcheng
 * @date 2024-07-17
 */
@RestController
@RequestMapping("/flowable/wfModelTemplate")
public class WfModelTemplateController extends BaseController {
    @Autowired
    private IWfModelTemplateService wfModelTemplateService;

    /**
     * 查询模型模板列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:list')")
    @GetMapping("/listWfModelTemplate")
    public AjaxResult listWfModelTemplate(WfModelTemplate wfModelTemplate) {
        List<WfModelTemplate> list = wfModelTemplateService.selectWfModelTemplateList(wfModelTemplate);
        return success(list);
    }


    /**
     * 查询模型模板列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(WfModelTemplate wfModelTemplate) {
        startPage();
        List<WfModelTemplate> list = wfModelTemplateService.selectWfModelTemplateList(wfModelTemplate);
        return getDataTable(list);
    }

    /**
     * 导出模型模板列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:export')")
    @Log(title = "模型模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WfModelTemplate wfModelTemplate) {
        List<WfModelTemplate> list = wfModelTemplateService.selectWfModelTemplateList(wfModelTemplate);
        ExcelUtil<WfModelTemplate> util = new ExcelUtil<WfModelTemplate>(WfModelTemplate.class);
        util.exportExcel(response, list, "模型模板数据");
    }

    /**
     * 获取模型模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:query')")
    @GetMapping(value = "/{modelTemplateId}")
    public AjaxResult getInfo(@PathVariable("modelTemplateId") String modelTemplateId) {
        return success(wfModelTemplateService.selectWfModelTemplateByModelTemplateId(modelTemplateId));
    }

    /**
     * 新增模型模板
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:add')")
    @Log(title = "模型模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfModelTemplate wfModelTemplate) {
        return toAjax(wfModelTemplateService.insertWfModelTemplate(wfModelTemplate));
    }

    /**
     * 修改模型模板
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:edit')")
    @Log(title = "模型模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfModelTemplate wfModelTemplate) {
        return toAjax(wfModelTemplateService.updateWfModelTemplate(wfModelTemplate));
    }

    /**
     * 修改模型模板xml
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:edit')")
    @Log(title = "模型模板", businessType = BusinessType.UPDATE)
    @PutMapping("/editBpmnXml")
    public AjaxResult editBpmnXml(@RequestBody WfModelTemplate wfModelTemplate) {
        return toAjax(wfModelTemplateService.editBpmnXml(wfModelTemplate));
    }

    /**
     * 删除模型模板
     */
    @PreAuthorize("@ss.hasPermi('workflow:wfModelTemplate:remove')")
    @Log(title = "模型模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{modelTemplateIds}")
    public AjaxResult remove(@PathVariable String[] modelTemplateIds) {
        return toAjax(wfModelTemplateService.deleteWfModelTemplateByModelTemplateIds(modelTemplateIds));
    }
}
