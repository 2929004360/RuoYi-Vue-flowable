package com.ruoyi.web.controller.work;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flowable.api.domain.vo.WorkRiskVo;
import com.ruoyi.work.service.IWorkRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 隐患上报Controller
 *
 * @author fengcheng
 * @date 2025-03-01
 */
@RestController
@RequestMapping("/work/risk")
@Validated
public class WorkRiskController extends BaseController {
    @Autowired
    private IWorkRiskService workRiskService;

    /**
     * 提交审核
     *
     * @param riskId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('work:risk:submit')")
    @Log(title = "隐患上报", businessType = BusinessType.UPDATE)
    @PutMapping("/submit/{riskId}")
    public AjaxResult submit(@NotNull(message = "riskId为空") @PathVariable("riskId") Long riskId) throws Exception {
        return toAjax(workRiskService.submit(riskId));
    }

    /**
     * 撤销审核
     *
     * @param riskId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('work:risk:revoke')")
    @Log(title = "隐患上报", businessType = BusinessType.UPDATE)
    @PutMapping("/revoke/{riskId}")
    public AjaxResult revoke(@NotNull(message = "riskId为空") @PathVariable("riskId") Long riskId) throws Exception {
        return toAjax(workRiskService.revoke(riskId));
    }

    /**
     * 重新申请
     *
     * @param riskId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('work:risk:resubmit')")
    @Log(title = "隐患上报", businessType = BusinessType.UPDATE)
    @PutMapping("/reapply/{riskId}")
    public AjaxResult reapply(@NotNull(message = "riskId为空") @PathVariable("riskId") Long riskId) throws Exception {
        return toAjax(workRiskService.reapply(riskId));
    }

    /**
     * 查询隐患上报列表
     */
    @PreAuthorize("@ss.hasPermi('work:risk:list')")
    @GetMapping("/list")
    public TableDataInfo list(WorkRiskVo workRiskVo, Boolean checked) {
        startPage();
        List<WorkRiskVo> list = workRiskService.selectWorkRiskList(workRiskVo, checked);
        return getDataTable(list);
    }

    /**
     * 导出隐患上报列表
     */
    @PreAuthorize("@ss.hasPermi('work:risk:export')")
    @Log(title = "隐患上报", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WorkRiskVo workRiskVo) {
        startPage();
        List<WorkRiskVo> list = workRiskService.selectWorkRiskList(workRiskVo, true);
        TableDataInfo dataTable = getDataTable(list);
        ExcelUtil<WorkRiskVo> util = new ExcelUtil<WorkRiskVo>(WorkRiskVo.class);
        util.exportExcel(response, (List<WorkRiskVo>) dataTable.getRows(), "隐患上报数据");
    }

    /**
     * 获取隐患上报详细信息
     */
    @PreAuthorize("@ss.hasPermi('work:risk:query')")
    @GetMapping(value = "/{riskId}")
    public AjaxResult getInfo(@PathVariable("riskId") Long riskId) {
        return success(workRiskService.selectWorkRiskByRiskId(riskId));
    }

    /**
     * 新增隐患上报
     */
    @PreAuthorize("@ss.hasPermi('work:risk:add')")
    @Log(title = "隐患上报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WorkRiskVo workRiskVo) {
        return toAjax(workRiskService.insertWorkRisk(workRiskVo));
    }

    /**
     * 修改隐患上报
     */
    @PreAuthorize("@ss.hasPermi('work:risk:edit')")
    @Log(title = "隐患上报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WorkRiskVo workRiskVo) {
        return toAjax(workRiskService.updateWorkRisk(workRiskVo));
    }

    /**
     * 删除隐患上报
     */
    @PreAuthorize("@ss.hasPermi('work:risk:remove')")
    @Log(title = "隐患上报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{riskIds}")
    public AjaxResult remove(@PathVariable Long[] riskIds) {
        return toAjax(workRiskService.deleteWorkRiskByRiskIds(riskIds));
    }
}
