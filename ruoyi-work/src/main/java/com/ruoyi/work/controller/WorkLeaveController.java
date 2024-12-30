package com.ruoyi.work.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flowable.api.domain.vo.WorkLeaveVo;
import com.ruoyi.work.service.IWorkLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 请假管理Controller
 *
 * @author fengcheng
 * @date 2024-12-30
 */
@Validated
@RestController
@RequestMapping("/work/leave")
public class WorkLeaveController extends BaseController {
    @Autowired
    private IWorkLeaveService workLeaveService;

    /**
     * 提交审核
     *
     * @param leaveId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('work:leave:submit')")
    @Log(title = "请假管理", businessType = BusinessType.UPDATE)
    @PutMapping("/submit/{leaveId}")
    public AjaxResult submit(@NotNull(message = "leaveId为空") @PathVariable("leaveId") Long leaveId) throws Exception {
        return toAjax(workLeaveService.submit(leaveId));
    }

    /**
     * 撤销审核
     *
     * @param leaveId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('work:leave:revoke')")
    @Log(title = "请假管理", businessType = BusinessType.UPDATE)
    @PutMapping("/revoke/{leaveId}")
    public AjaxResult revoke(@NotNull(message = "leaveId为空") @PathVariable("leaveId") Long leaveId) throws Exception {
        return toAjax(workLeaveService.revoke(leaveId));
    }

    /**
     * 重新申请
     *
     * @param leaveId
     * @return
     */
    @PreAuthorize("@ss.hasPermi('work:leave:resubmit')")
    @Log(title = "请假管理", businessType = BusinessType.UPDATE)
    @PutMapping("/reapply/{leaveId}")
    public AjaxResult reapply(@NotNull(message = "leaveId为空") @PathVariable("leaveId") Long leaveId) throws Exception {
        return toAjax(workLeaveService.reapply(leaveId));
    }

    /**
     * 查询请假管理列表
     */
    @PreAuthorize("@ss.hasPermi('work:leave:list')")
    @GetMapping("/list")
    public TableDataInfo list(WorkLeaveVo workLeaveVo) {
        startPage();
        List<WorkLeaveVo> list = workLeaveService.selectWorkLeaveList(workLeaveVo);
        return getDataTable(list);
    }

    /**
     * 导出请假管理列表
     */
    @PreAuthorize("@ss.hasPermi('work:leave:export')")
    @Log(title = "请假管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WorkLeaveVo workLeaveVo) {
        List<WorkLeaveVo> list = workLeaveService.selectWorkLeaveList(workLeaveVo);
        ExcelUtil<WorkLeaveVo> util = new ExcelUtil<WorkLeaveVo>(WorkLeaveVo.class);
        util.exportExcel(response, list, "请假管理数据");
    }

    /**
     * 获取请假管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('work:leave:query')")
    @GetMapping(value = "/{leaveId}")
    public AjaxResult getInfo(@PathVariable("leaveId") Long leaveId) {
        return success(workLeaveService.selectWorkLeaveByLeaveId(leaveId));
    }

    /**
     * 新增请假管理
     */
    @PreAuthorize("@ss.hasPermi('work:leave:add')")
    @Log(title = "请假管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WorkLeaveVo workLeaveVo) {
        return toAjax(workLeaveService.insertWorkLeave(workLeaveVo));
    }

    /**
     * 修改请假管理
     */
    @PreAuthorize("@ss.hasPermi('work:leave:edit')")
    @Log(title = "请假管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WorkLeaveVo workLeaveVo) {
        return toAjax(workLeaveService.updateWorkLeave(workLeaveVo));
    }

    /**
     * 删除请假管理
     */
    @PreAuthorize("@ss.hasPermi('work:leave:remove')")
    @Log(title = "请假管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{leaveIds}")
    public AjaxResult remove(@PathVariable Long[] leaveIds) {
        return toAjax(workLeaveService.deleteWorkLeaveByLeaveIds(leaveIds));
    }
}
