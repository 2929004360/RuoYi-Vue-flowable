package com.ruoyi.web.controller.flowable;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flowable.domain.WfDeployForm;
import com.ruoyi.flowable.domain.bo.WfFormBo;
import com.ruoyi.flowable.domain.vo.WfFormVo;
import com.ruoyi.flowable.service.IWfDeployFormService;
import com.ruoyi.flowable.service.IWfFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 流程表单Controller
 *
 * @author fengcheng
 * @createTime 2022/3/7 22:07
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/flowable/workflow/form" )
public class WfFormController extends BaseController {

    private final IWfFormService formService;

    private final IWfDeployFormService deployFormService;

    /**
     * 查询流程表单列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:form:list')")
    @GetMapping("/queryList" )
    public AjaxResult queryList(WfFormBo bo) {
        return success(formService.queryPageList(bo));
    }

    /**
     * 查询流程表单列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:form:list')")
    @GetMapping("/list" )
    public TableDataInfo list(WfFormBo bo) {
        startPage();
        List<WfFormVo> list = formService.queryPageList(bo);
        return getDataTable(list);
    }

    /**
     * 导出流程表单列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:form:export')")
    @Log(title = "流程表单" , businessType = BusinessType.EXPORT)
    @PostMapping("/export" )
    public void export(WfFormBo bo, HttpServletResponse response) {
        List<WfFormVo> list = formService.queryList(bo);
        ExcelUtil<WfFormVo> util = new ExcelUtil<WfFormVo>(WfFormVo.class);
        util.exportExcel(response, list, "流程表单" );
    }

    /**
     * 获取流程表单详细信息
     *
     * @param formId 主键
     */
    @PreAuthorize("@ss.hasPermi('workflow:form:query')")
    @GetMapping(value = "/{formId}" )
    public R<WfFormVo> getInfo(@NotNull(message = "主键不能为空" ) @PathVariable("formId" ) String formId) {
        return R.ok(formService.queryById(formId));
    }

    /**
     * 新增流程表单
     */
    @PreAuthorize("@ss.hasPermi('workflow:form:add')")
    @Log(title = "流程表单" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfFormBo bo) {
        return toAjax(formService.insertForm(bo));
    }

    /**
     * 修改流程表单
     */
    @PreAuthorize("@ss.hasPermi('workflow:form:edit')")
    @Log(title = "流程表单" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfFormBo bo) {
        return toAjax(formService.updateForm(bo));
    }

    /**
     * 删除流程表单
     *
     * @param formIds 主键串
     */
    @PreAuthorize("@ss.hasPermi('workflow:form:remove')")
    @Log(title = "流程表单" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{formIds}" )
    public AjaxResult remove(@NotEmpty(message = "主键不能为空" ) @PathVariable String[] formIds) {
        return toAjax(formService.deleteWithValidByIds(Arrays.asList(formIds)) ? 1 : 0);
    }

    /**
     * 挂载流程表单
     */
    @Log(title = "流程表单" , businessType = BusinessType.INSERT)
    @PostMapping("/addDeployForm" )
    public AjaxResult addDeployForm(@RequestBody WfDeployForm deployForm) {
        return toAjax(deployFormService.insertWfDeployForm(deployForm));
    }
}
