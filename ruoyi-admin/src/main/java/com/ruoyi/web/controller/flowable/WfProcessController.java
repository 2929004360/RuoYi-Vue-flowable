package com.ruoyi.web.controller.flowable;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flowable.base.BaseController;
import com.ruoyi.flowable.core.domain.ProcessQuery;
import com.ruoyi.flowable.domain.bo.DdToBpmn;
import com.ruoyi.flowable.domain.bo.ResubmitProcess;
import com.ruoyi.flowable.domain.bo.WfCopyBo;
import com.ruoyi.flowable.domain.vo.*;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.IWfCopyService;
import com.ruoyi.flowable.service.IWfProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 工作流流程管理
 *
 * @author fengcheng
 * @createTime 2022/3/24 18:54
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/flowable/workflow/process")
@Validated
public class WfProcessController extends BaseController {

    private final IWfProcessService processService;
    private final IWfCopyService copyService;

    /**
     * 根据菜单id获取可发起列表
     */
    @GetMapping(value = "/getStartList/{menuId}")
    public R<List<WfDefinitionVo>> getStartList(@NotBlank(message = "menuId不能为空") @PathVariable("menuId") String menuId) {
        List<WfDefinitionVo> list = processService.getStartList(menuId);
        return R.ok(list);
    }

    /**
     * 查询可发起流程列表
     *
     * @param processQuery 查询参数
     */
    @GetMapping(value = "/list")
    @PreAuthorize("@ss.hasPermi('workflow:process:startList')")
    public R<List<WfDefinitionVo>> startProcessList(ProcessQuery processQuery) {
        return R.ok(processService.selectPageStartProcessList(processQuery));
    }

    /**
     * 我拥有的流程
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:ownList')")
    @GetMapping(value = "/ownList")
    public TableDataInfo<WfTaskVo> ownProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectPageOwnProcessList(processQuery, pageQuery);
    }

    /**
     * 获取待办列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:todoList')")
    @GetMapping(value = "/todoList")
    public TableDataInfo<WfTaskVo> todoProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectPageTodoProcessList(processQuery, pageQuery);
    }

    /**
     * 获取待签列表
     *
     * @param processQuery 流程业务对象
     * @param pageQuery    分页参数
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:claimList')")
    @GetMapping(value = "/claimList")
    public TableDataInfo<WfTaskVo> claimProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectPageClaimProcessList(processQuery, pageQuery);
    }

    /**
     * 获取已办列表
     *
     * @param pageQuery 分页参数
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:finishedList')")
    @GetMapping(value = "/finishedList")
    public TableDataInfo<WfTaskVo> finishedProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectPageFinishedProcessList(processQuery, pageQuery);
    }

    /**
     * 获取我的抄送列表
     *
     * @param copyBo    流程抄送对象
     * @param pageQuery 分页参数
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:myCopyList')")
    @GetMapping(value = "/myCopyList")
    public TableDataInfo<WfCopyVo> myCopyProcessList(WfCopyBo copyBo, PageQuery pageQuery) {
        if (!SecurityUtils.hasRole("admin")) {
            copyBo.setOriginatorId(SecurityUtils.getUserId());
        }
        return copyService.selectPageList(copyBo, pageQuery);
    }

    /**
     * 获取抄送列表
     *
     * @param copyBo    流程抄送对象
     * @param pageQuery 分页参数
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:copyList')")
    @GetMapping(value = "/copyList")
    public TableDataInfo<WfCopyVo> copyProcessList(WfCopyBo copyBo, PageQuery pageQuery) {
        if (!SecurityUtils.hasRole("admin")) {
            copyBo.setUserId(SecurityUtils.getUserId());
        }
        return copyService.selectPageList(copyBo, pageQuery);
    }

    /**
     * 删除抄送列表
     *
     * @param copyIds 抄送id
     * @return
     */
    @PreAuthorize("@ss.hasAnyPermi('workflow:process:removeCopy,workflow:process:removeMyCopy')")
    @DeleteMapping(value = "/delCopy/{copyIds}")
    public R<Void> deleteCopy(@PathVariable String[] copyIds) {
        copyService.deleteCopy(copyIds);
        return R.ok();
    }

    /**
     * 导出我拥有流程列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:ownExport')")
    @Log(title = "我拥有流程", businessType = BusinessType.EXPORT)
    @PostMapping("/ownExport")
    public void ownExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectOwnProcessList(processQuery);
        List<WfOwnTaskExportVo> listVo = BeanUtil.copyToList(list, WfOwnTaskExportVo.class);
        for (WfOwnTaskExportVo exportVo : listVo) {
            exportVo.setStatus(ObjectUtil.isNull(exportVo.getFinishTime()) ? "进行中" : "已完成");
        }
        ExcelUtil<WfOwnTaskExportVo> util = new ExcelUtil<WfOwnTaskExportVo>(WfOwnTaskExportVo.class);
        util.exportExcel(response, listVo, "我拥有流程");
    }

    /**
     * 导出待办流程列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:todoExport')")
    @Log(title = "待办流程", businessType = BusinessType.EXPORT)
    @PostMapping("/todoExport")
    public void todoExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectTodoProcessList(processQuery);
        List<WfTodoTaskExportVo> listVo = BeanUtil.copyToList(list, WfTodoTaskExportVo.class);
        ExcelUtil<WfTodoTaskExportVo> util = new ExcelUtil<WfTodoTaskExportVo>(WfTodoTaskExportVo.class);
        util.exportExcel(response, listVo, "待办流程");
    }

    /**
     * 导出待签流程列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:claimExport')")
    @Log(title = "待签流程", businessType = BusinessType.EXPORT)
    @PostMapping("/claimExport")
    public void claimExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectClaimProcessList(processQuery);
        List<WfClaimTaskExportVo> listVo = BeanUtil.copyToList(list, WfClaimTaskExportVo.class);
        ExcelUtil<WfClaimTaskExportVo> util = new ExcelUtil<WfClaimTaskExportVo>(WfClaimTaskExportVo.class);
        util.exportExcel(response, listVo, "待签流程");
    }

    /**
     * 导出已办流程列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:finishedExport')")
    @Log(title = "已办流程", businessType = BusinessType.EXPORT)
    @PostMapping("/finishedExport")
    public void finishedExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectFinishedProcessList(processQuery);
        List<WfFinishedTaskExportVo> listVo = BeanUtil.copyToList(list, WfFinishedTaskExportVo.class);
        ExcelUtil<WfFinishedTaskExportVo> util = new ExcelUtil<WfFinishedTaskExportVo>(WfFinishedTaskExportVo.class);
        util.exportExcel(response, listVo, "已办流程");
    }

    /**
     * 导出抄送流程列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:copyExport')")
    @Log(title = "抄送流程", businessType = BusinessType.EXPORT)
    @PostMapping("/copyExport")
    public void copyExport(WfCopyBo copyBo, HttpServletResponse response) {
        if (!SecurityUtils.hasRole("admin")) {
            copyBo.setUserId(SecurityUtils.getUserId());
        }
        List<WfCopyVo> list = copyService.selectList(copyBo);
        ExcelUtil<WfCopyVo> util = new ExcelUtil<WfCopyVo>(WfCopyVo.class);
        util.exportExcel(response, list, "抄送流程");
    }

    /**
     * 导出我的抄送流程列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:myCopyExport')")
    @Log(title = "抄送流程", businessType = BusinessType.EXPORT)
    @PostMapping("/myCopyExport")
    public void myCopyExport(WfCopyBo copyBo, HttpServletResponse response) {
        if (!SecurityUtils.hasRole("admin")) {
            copyBo.setOriginatorId(SecurityUtils.getUserId());
        }
        List<WfCopyVo> list = copyService.selectList(copyBo);
        ExcelUtil<WfCopyVo> util = new ExcelUtil<WfCopyVo>(WfCopyVo.class);
        util.exportExcel(response, list, "抄送流程");
    }

    /**
     * 查询流程部署关联表单信息
     *
     * @param definitionId 流程定义id
     * @param deployId     流程部署id
     */
    @GetMapping("/getProcessForm")
    @PreAuthorize("@ss.hasPermi('workflow:process:start')")
    public R<?> getForm(@RequestParam(value = "definitionId") String definitionId,
                        @RequestParam(value = "deployId") String deployId,
                        @RequestParam(value = "procInsId", required = false) String procInsId) {
        return R.ok(processService.selectFormContent(definitionId, deployId, procInsId));
    }

    /**
     * 根据流程定义id启动流程实例
     *
     * @param processDefId 流程定义id
     * @param variables    变量集合,json对象
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:start')")
    @PostMapping("/start/{processDefId}")
    public R<String> start(@PathVariable(value = "processDefId") String processDefId, @RequestBody Map<String, Object> variables) {
        processService.startProcessByDefId(processDefId, variables);
        return R.ok(null, "流程启动成功");
    }

    /**
     * 重新发起流程实例
     *
     * @param resubmitProcess 重新发起
     */
    @PreAuthorize("@ss.hasPermi('workflow:process:start')")
    @PostMapping("/resubmit")
    public R<String> resubmit(@RequestBody ResubmitProcess resubmitProcess) {
        processService.resubmitProcess(resubmitProcess);
        return R.ok(null, "流程启动成功");
    }

    /**
     * 删除流程实例
     *
     * @param instanceIds 流程实例ID串
     */
    @DeleteMapping("/instance/{instanceIds}")
    public R<Void> delete(@PathVariable String[] instanceIds) {
        processService.deleteProcessByIds(instanceIds);
        return R.ok();
    }

    /**
     * 读取xml文件
     *
     * @param processDefId 流程定义ID
     */
    @GetMapping("/bpmnXml/{processDefId}")
    public R<String> getBpmnXml(@PathVariable(value = "processDefId") String processDefId) {
        return R.ok(processService.queryBpmnXmlById(processDefId), null);
    }

    /**
     * 查询流程详情信息
     *
     * @param procInsId 流程实例ID
     * @param taskId    任务ID
     * @param formType  表单类型
     */
    @GetMapping("/detail")
    public R detail(String procInsId, String taskId, @NotNull(message = "表单类型不能为空") String formType) {
        return R.ok(processService.queryProcessDetail(procInsId, taskId, formType));
    }

    /**
     * 根据钉钉流程json转flowable的bpmn的xml格式
     *
     * @param ddToBpmn
     */
    @PostMapping("/ddtobpmn")
    public R<String> ddToBpmn(@RequestBody DdToBpmn ddToBpmn) {
        return processService.dingdingToBpmn(ddToBpmn);
    }

    /**
     * 查询流程是否结束
     *
     * @param procInsId
     * @param
     */
    @GetMapping("/iscompleted")
    public R processIsCompleted(String procInsId) {
        return R.ok(processService.processIsCompleted(procInsId));
    }
}
