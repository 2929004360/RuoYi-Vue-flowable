package com.ruoyi.flowable.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.domain.WfModelAssociationTemplate;
import com.ruoyi.flowable.domain.WfModelTemplate;
import com.ruoyi.flowable.domain.vo.WfModelVo;
import com.ruoyi.flowable.enums.FormType;
import com.ruoyi.flowable.mapper.WfModelTemplateMapper;
import com.ruoyi.flowable.service.IWfModelAssociationTemplateService;
import com.ruoyi.flowable.service.IWfModelService;
import com.ruoyi.flowable.service.IWfModelTemplateService;
import com.ruoyi.flowable.utils.IdWorker;
import com.ruoyi.flowable.utils.ModelUtils;
import com.ruoyi.system.service.ISysDeptService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 模型模板Service业务层处理
 *
 * @author fengcheng
 * @date 2024-07-17
 */
@Service
public class WfModelTemplateServiceImpl implements IWfModelTemplateService {
    @Autowired
    private WfModelTemplateMapper wfModelTemplateMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private IWfModelAssociationTemplateService wfModelAssociationTemplateService;

    @Autowired
    protected RepositoryService repositoryService;

    /**
     * 流程模型服务
     */
    @Autowired
    private IWfModelService modelService;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询模型模板
     *
     * @param modelTemplateId 模型模板主键
     * @return 模型模板
     */
    @Override
    public WfModelTemplate selectWfModelTemplateByModelTemplateId(String modelTemplateId) {
        return wfModelTemplateMapper.selectWfModelTemplateByModelTemplateId(modelTemplateId);
    }

    /**
     * 查询模型模板列表
     *
     * @param wfModelTemplate 模型模板
     * @return 模型模板
     */
    @Override
    public List<WfModelTemplate> selectWfModelTemplateList(WfModelTemplate wfModelTemplate) {
        if (SecurityUtils.hasRole("admin" )) {
            return wfModelTemplateMapper.selectWfModelTemplateList(wfModelTemplate);
        }

        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        List<Long> deptIdList = sysDeptService.selectBranchDeptId(deptId);
        deptIdList.add(deptId);
        wfModelTemplate.setDeptId(deptId);

        SysDept sysDept = sysDeptService.selectDeptById(deptId);
        String[] ancestorsArr = sysDept.getAncestors().split("," );
        List<Long> ancestorsList = Convert.toList(Long.class, ancestorsArr);

        return wfModelTemplateMapper.selectWfModelTemplateListVo(wfModelTemplate, deptIdList, ancestorsList);
    }

    /**
     * 新增模型模板
     *
     * @param wfModelTemplate 模型模板
     * @return 结果
     */
    @Override
    public int insertWfModelTemplate(WfModelTemplate wfModelTemplate) {
        wfModelTemplate.setModelTemplateId(String.valueOf(idWorker.nextId()));
        wfModelTemplate.setCreateTime(DateUtils.getNowDate());
        return wfModelTemplateMapper.insertWfModelTemplate(wfModelTemplate);
    }

    /**
     * 修改模型模板
     *
     * @param wfModelTemplate 模型模板
     * @return 结果
     */
    @Override
    public int updateWfModelTemplate(WfModelTemplate wfModelTemplate) {
        wfModelTemplate.setUpdateTime(DateUtils.getNowDate());
        return wfModelTemplateMapper.updateWfModelTemplate(wfModelTemplate);
    }

    /**
     * 批量删除模型模板
     *
     * @param modelTemplateIds 需要删除的模型模板主键
     * @return 结果
     */
    @Override
    public int deleteWfModelTemplateByModelTemplateIds(String[] modelTemplateIds) {
        for (String modelTemplateId : modelTemplateIds) {
            WfModelAssociationTemplate wfModelAssociationTemplate = new WfModelAssociationTemplate();
            wfModelAssociationTemplate.setModelTemplateId(modelTemplateId);
            if (wfModelAssociationTemplateService.selectWfModelAssociationTemplateList(wfModelAssociationTemplate).size() > 0) {
                throw new RuntimeException("模型模板已关联流程，请先解除关联！" );
            }
        }
        return wfModelTemplateMapper.deleteWfModelTemplateByModelTemplateIds(modelTemplateIds);
    }

    /**
     * 删除模型模板信息
     *
     * @param modelTemplateId 模型模板主键
     * @return 结果
     */
    @Override
    public int deleteWfModelTemplateByModelTemplateId(String modelTemplateId) {
        return wfModelTemplateMapper.deleteWfModelTemplateByModelTemplateId(modelTemplateId);
    }

    /**
     * 修改模型模板xml
     *
     * @param wfModelTemplate 模型模板
     * @return 结果
     */
    @Override
    public int editBpmnXml(WfModelTemplate wfModelTemplate) {
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(wfModelTemplate.getBpmnXml());
        if (ObjectUtil.isEmpty(bpmnModel)) {
            throw new RuntimeException("获取模型设计失败！" );
        }

        // 获取开始节点
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new RuntimeException("开始节点不存在，请检查流程设计是否有误！" );
        }
        if (FormType.PROCESS.getType().equals(wfModelTemplate.getFormType())) {
            // 获取开始节点配置的表单Key
            if (StrUtil.isBlank(startEvent.getFormKey())) {
                throw new RuntimeException("请配置流程表单" );
            }
        }
        //查看开始节点的后一个任务节点出口
        List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
        if (Objects.isNull(outgoingFlows)) {
            throw new RuntimeException("导入失败，流程配置错误！" );
        }

        // 保存 BPMN XML
        Process process = bpmnModel.getProcesses().get(0);
        WfModelAssociationTemplate wfModelAssociationTemplate = new WfModelAssociationTemplate();
        wfModelAssociationTemplate.setModelTemplateId(wfModelTemplate.getModelTemplateId());
        List<WfModelAssociationTemplate> wfModelAssociationTemplates = wfModelAssociationTemplateService.selectWfModelAssociationTemplateList(wfModelAssociationTemplate);
        for (WfModelAssociationTemplate modelAssociationTemplate : wfModelAssociationTemplates) {
            WfModelVo model = modelService.getModel(modelAssociationTemplate.getModelId());
            BpmnModel modelBpmnModel = ModelUtils.getBpmnModel(model.getBpmnXml());
            Process modelProcess = modelBpmnModel.getProcesses().get(0);

            process.setName(modelProcess.getName());
            process.setId(modelProcess.getId());
            repositoryService.addModelEditorSource(modelAssociationTemplate.getModelId(), ModelUtils.getBpmnXml(bpmnModel));
        }

//        //遍历返回下一个节点信息
//        for (SequenceFlow outgoingFlow : outgoingFlows) {
//            //类型自己判断（获取下个节点是任务节点）
//            FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
//            // 下个出口是用户任务，而且是要发起人节点才让保存
//            if (targetFlowElement instanceof UserTask) {
//                if (StringUtils.equals(((UserTask) targetFlowElement).getAssignee(), "${initiator}")) {
//                    break;
//                } else {
//                    throw new RuntimeException("导入失败，流程第一个用户任务节点必须是发起人节点");
//                }
//            }
//        }

        return updateWfModelTemplate(wfModelTemplate);
    }
}
