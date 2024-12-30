package com.ruoyi.web.controller.flowable;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.R;

import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.base.BaseController;
import com.ruoyi.flowable.core.domain.ProcessQuery;
import com.ruoyi.flowable.domain.bo.WfModelBo;
import com.ruoyi.flowable.domain.vo.WfDeployVo;
import com.ruoyi.flowable.domain.vo.WfFormVo;
import com.ruoyi.flowable.domain.vo.WfModelVo;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.IWfDeployFormService;
import com.ruoyi.flowable.service.IWfDeployService;
import com.ruoyi.flowable.service.IWfModelProcdefService;
import com.ruoyi.flowable.service.IWfModelService;
import com.ruoyi.flowable.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程部署
 *
 * @author fengcheng
 * @createTime 2022/3/24 20:57
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/flowable/workflow/deploy")
public class WfDeployController extends BaseController {

    private final IWfDeployService deployService;
    private final IWfDeployFormService deployFormService;

    private final IWfModelService wfModelService;

    private final IWfModelProcdefService wfModelProcdefService;

    /**
     * 查询流程部署列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:deploy:list')")
    @GetMapping("/list")
    public R<List<WfDeployVo>> list(ProcessQuery processQuery) {
        if (SecurityUtils.hasRole("admin")) {
            List<WfDeployVo> list = deployService.selectWfDeployList(processQuery, null);
            return R.ok(list);
        } else {
            List<WfModelVo> wfModelVoList = wfModelService.selectList(new WfModelBo());
            List<String> modelIdList = wfModelVoList.stream().map(WfModelVo::getModelId).collect(Collectors.toList());
            if (modelIdList.size() == 0) {
                return R.ok(new ArrayList<>());
            }
            List<String> procdefIdList = wfModelProcdefService.selectWfModelProcdefListByModelIdList(modelIdList);

            List<WfDeployVo> list = deployService.selectWfDeployList(processQuery, procdefIdList);
            return R.ok(list);
        }
    }

    /**
     * 查询流程部署版本列表
     */
    @PreAuthorize("@ss.hasPermi('workflow:deploy:list')")
    @GetMapping("/publishList")
    public TableDataInfo<WfDeployVo> publishList(@RequestParam String processKey, PageQuery pageQuery) {
        return deployService.queryPublishList(processKey, pageQuery);
    }

    /**
     * 激活或挂起流程
     *
     * @param state        状态（active:激活 suspended:挂起）
     * @param definitionId 流程定义ID
     */
    @PreAuthorize("@ss.hasPermi('workflow:deploy:state')")
    @PutMapping(value = "/changeState")
    public R<Void> changeState(@RequestParam String state, @RequestParam String definitionId) {
        deployService.updateState(definitionId, state);
        return R.ok();
    }

    /**
     * 读取xml文件
     *
     * @param definitionId 流程定义ID
     * @return
     */
    @PreAuthorize("@ss.hasPermi('workflow:deploy:query')")
    @GetMapping("/bpmnXml/{definitionId}")
    public R<String> getBpmnXml(@PathVariable(value = "definitionId") String definitionId) {
        return R.ok(deployService.queryBpmnXmlById(definitionId), null);
    }

    /**
     * 删除流程部署
     *
     * @param deployIds 流程部署ids
     */
    @PreAuthorize("@ss.hasPermi('workflow:deploy:remove')")
    @Log(title = "删除流程部署", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deployIds}")
    public R<String> remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] deployIds) {
        deployService.deleteByIds(Arrays.asList(deployIds));
        return R.ok();
    }

    /**
     * 查询流程部署关联表单信息
     *
     * @param deployId 流程部署id
     */
    @GetMapping("/form/{deployId}")
    public R<?> start(@PathVariable(value = "deployId") String deployId) {
        WfFormVo formVo = deployFormService.selectDeployFormByDeployId(deployId);
        if (Objects.isNull(formVo)) {
            return R.fail("请先配置流程表单");
        }
        return R.ok(JsonUtils.parseObject(formVo.getContent(), Map.class));
    }
}
