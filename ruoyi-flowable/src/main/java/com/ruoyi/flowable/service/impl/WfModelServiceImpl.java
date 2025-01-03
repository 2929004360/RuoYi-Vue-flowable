package com.ruoyi.flowable.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.constant.ProcessConfigConstants;
import com.ruoyi.flowable.constant.ProcessConstants;
import com.ruoyi.flowable.domain.*;
import com.ruoyi.flowable.domain.bo.WfModelBo;
import com.ruoyi.flowable.domain.dto.WfMetaInfoDto;
import com.ruoyi.flowable.domain.vo.WfFormVo;
import com.ruoyi.flowable.domain.vo.WfModelVo;
import com.ruoyi.flowable.enums.FormType;
import com.ruoyi.flowable.enums.PermissionEnum;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.mapper.WfModelMapper;
import com.ruoyi.flowable.mapper.WfModelProcdefMapper;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.*;
import com.ruoyi.flowable.utils.IdWorker;
import com.ruoyi.flowable.utils.JsonUtils;
import com.ruoyi.flowable.utils.ModelUtils;
import com.ruoyi.flowable.utils.StringUtils;
import com.ruoyi.system.api.service.ISysDeptServiceApi;
import com.ruoyi.system.api.service.ISysUserServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengcheng
 * @createTime 2022/6/21 9:11
 */
@Service
@Slf4j
public class WfModelServiceImpl extends FlowServiceFactory implements IWfModelService {

    @Lazy
    @Autowired
    private IWfFormService formService;

    @Lazy
    @Autowired
    private IWfDeployFormService deployFormService;

    @Lazy
    @Autowired
    private IWfIconService wfIconService;

    @Lazy
    @Autowired
    private IWfModelPermissionService wfModelPermissionService;

    @Lazy
    @Autowired
    private IdWorker idWorker;

    @Lazy
    @Autowired
    private WfModelMapper wfModelMapper;

    @Lazy
    @Autowired
    private ISysUserServiceApi userServiceApi;

    @Lazy
    @Autowired
    private ISysDeptServiceApi deptServiceApi;

    @Lazy
    @Autowired
    private WfModelProcdefMapper wfModelProcdefMapper;

    @Lazy
    @Autowired
    private IWfModelProcdefService wfModelProcdefService;

    @Lazy
    @Autowired
    private IWfModelAssociationTemplateService wfModelAssociationTemplateService;

    @Lazy
    @Autowired
    private WfDeployServiceImpl wfDeployService;

    @Override
    public TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery) {
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByCreateTime().desc();
        // 构建查询条件
        if (StringUtils.isNotBlank(modelBo.getModelKey())) {
            modelQuery.modelKey(modelBo.getModelKey());
        }
        if (StringUtils.isNotBlank(modelBo.getModelName())) {
            modelQuery.modelNameLike("%" + modelBo.getModelName() + "%");
        }
        if (StringUtils.isNotBlank(modelBo.getCategory())) {
            modelQuery.modelCategory(modelBo.getCategory());
        }

        // 执行查询
        long pageTotal = modelQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);

        List<Model> modelList = modelQuery.listPage(offset, pageQuery.getPageSize());

        List<WfModelVo> modelVoList = new ArrayList<>(modelList.size());

        modelList.forEach(model -> {
            WfModelVo modelVo = new WfModelVo();
            modelVo.setModelId(model.getId());
            modelVo.setModelName(model.getName());
            modelVo.setModelKey(model.getKey());
            modelVo.setCategory(model.getCategory());
            modelVo.setCreateTime(model.getCreateTime());
            modelVo.setVersion(model.getVersion());
            WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
            if (metaInfo != null) {
                modelVo.setDescription(metaInfo.getDescription());
                modelVo.setFormType(metaInfo.getFormType());
                modelVo.setFormId(metaInfo.getFormId());
                modelVo.setFormName(metaInfo.getFormName());
                modelVo.setFormCustomCreatePath(metaInfo.getFormCustomCreatePath());
                modelVo.setFormCustomViewPath(metaInfo.getFormCustomViewPath());
                modelVo.setIcon(metaInfo.getIcon());
            }
            modelVoList.add(modelVo);
        });
        Page<WfModelVo> page = new Page<>();
        page.setRecords(modelVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    @Override
    public List<WfModelVo> list(WfModelBo modelBo) {
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByCreateTime().desc();
        // 构建查询条件
        if (StringUtils.isNotBlank(modelBo.getModelKey())) {
            modelQuery.modelKey(modelBo.getModelKey());
        }
        if (StringUtils.isNotBlank(modelBo.getModelName())) {
            modelQuery.modelNameLike("%" + modelBo.getModelName() + "%");
        }
        if (StringUtils.isNotBlank(modelBo.getCategory())) {
            modelQuery.modelCategory(modelBo.getCategory());
        }
        List<Model> modelList = modelQuery.list();
        List<WfModelVo> modelVoList = new ArrayList<>(modelList.size());
        modelList.forEach(model -> {
            WfModelVo modelVo = new WfModelVo();
            modelVo.setModelId(model.getId());
            modelVo.setModelName(model.getName());
            modelVo.setModelKey(model.getKey());
            modelVo.setCategory(model.getCategory());
            modelVo.setCreateTime(model.getCreateTime());
            modelVo.setVersion(model.getVersion());
            WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
            if (metaInfo != null) {
                modelVo.setDescription(metaInfo.getDescription());
                modelVo.setFormType(metaInfo.getFormType());
                modelVo.setFormId(metaInfo.getFormId());
            }
            modelVoList.add(modelVo);
        });
        return modelVoList;
    }

    @Override
    public TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery) {
        ModelQuery modelQuery = repositoryService.createModelQuery().modelKey(modelBo.getModelKey()).orderByModelVersion().desc();
        // 执行查询（不显示最新版，-1）
        long pageTotal = modelQuery.count() - 1;
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        // offset+1，去掉最新版
        int offset = 1 + pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Model> modelList = modelQuery.listPage(offset, pageQuery.getPageSize());
        List<WfModelVo> modelVoList = new ArrayList<>(modelList.size());
        modelList.forEach(model -> {
            WfModelVo modelVo = new WfModelVo();
            modelVo.setModelId(model.getId());
            modelVo.setModelName(model.getName());
            modelVo.setModelKey(model.getKey());
            modelVo.setCategory(model.getCategory());
            modelVo.setCreateTime(model.getCreateTime());
            modelVo.setVersion(model.getVersion());
            WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
            if (metaInfo != null) {
                modelVo.setDescription(metaInfo.getDescription());
                modelVo.setFormType(metaInfo.getFormType());
                modelVo.setFormId(metaInfo.getFormId());
            }
            modelVoList.add(modelVo);
        });
        Page<WfModelVo> page = new Page<>();
        page.setRecords(modelVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    @Override
    public WfModelVo getModel(String modelId) {
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        // 获取流程图
        String bpmnXml = queryBpmnXmlById(modelId);
        WfModelVo modelVo = new WfModelVo();
        modelVo.setModelId(model.getId());
        modelVo.setModelName(model.getName());
        modelVo.setModelKey(model.getKey());
        modelVo.setCategory(model.getCategory());
        modelVo.setCreateTime(model.getCreateTime());
        modelVo.setVersion(model.getVersion());
        modelVo.setBpmnXml(bpmnXml);
        WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
        if (metaInfo != null) {
            modelVo.setDescription(metaInfo.getDescription());
            modelVo.setFormType(metaInfo.getFormType());
            modelVo.setFormId(metaInfo.getFormId());
            modelVo.setFormName(metaInfo.getFormName());
            modelVo.setFormCustomCreatePath(metaInfo.getFormCustomCreatePath());
            modelVo.setFormCustomViewPath(metaInfo.getFormCustomViewPath());
            modelVo.setIcon(metaInfo.getIcon());
            modelVo.setMenuId(metaInfo.getMenuId());
            modelVo.setMenuName(metaInfo.getMenuName());
            modelVo.setProcessConfig(metaInfo.getProcessConfig());
            modelVo.setModelTemplateId(metaInfo.getModelTemplateId());
            if (FormType.PROCESS.getType().equals(metaInfo.getFormType())) {
                WfFormVo wfFormVo = formService.queryById(metaInfo.getFormId());
                modelVo.setContent(wfFormVo.getContent());
            }
        }

        WfModelPermission permission = new WfModelPermission();
        permission.setModelId(modelId);
        permission.setType(PermissionEnum.USER_PERMISSION.getCode());
        List<Long> permissionIdList = new ArrayList<>();
        if (!SecurityUtils.hasRole("admin")) {
            //获取当前登录用户
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            permissionIdList = userServiceApi.listUserIdByDeptId(sysUser.getDeptId());
        }
        modelVo.setSelectUserList(wfModelPermissionService.selectWfModelPermissionList(permission, permissionIdList));


        permission.setType(PermissionEnum.DEPT_PERMISSION.getCode());
        if (!SecurityUtils.hasRole("admin")) {
            //获取当前登录用户
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            List<Long> deptList = deptServiceApi.selectBranchDeptId(sysUser.getDeptId());
            deptList.add(sysUser.getDeptId());
            permissionIdList = deptList;
        }
        modelVo.setSelectDeptList(wfModelPermissionService.selectWfModelPermissionList(permission, permissionIdList));
        return modelVo;
    }

    @Override
    public String queryBpmnXmlById(String modelId) {
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        return StrUtil.utf8Str(bpmnBytes);
    }

    /**
     * 新增模型信息
     *
     * @param modelBo 流程模型对象
     * @return 模型id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertModel(WfModelBo modelBo) {
        // 创建一个新的模型对象
        Model model = repositoryService.newModel();
        // 设置模型名称
        model.setName(modelBo.getModelName());
        // 设置模型键
        model.setKey(modelBo.getModelKey());
        // 设置流程分类
        model.setCategory(modelBo.getCategory());
        // 构建元数据信息
        String metaInfo = buildMetaInfo(new WfMetaInfoDto(),
                modelBo.getDescription(),
                modelBo.getIcon(),
                modelBo.getFormType(),
                modelBo.getFormId(),
                modelBo.getFormName(),
                modelBo.getFormCustomCreatePath(),
                modelBo.getFormCustomViewPath(),
                modelBo.getMenuId(),
                modelBo.getMenuName(),
                modelBo.getProcessConfig(),
                modelBo.getModelTemplateId()
        );
        // 设置元数据信息
        model.setMetaInfo(metaInfo);
        // 保存流程模型
        repositoryService.saveModel(model);

        checkModel(modelBo, model.getId());
        insertWfModelPermissionList(model.getId(), modelBo);

        if (ProcessConfigConstants.MODEL_TEMPLATE.equals(modelBo.getProcessConfig())) {
            WfModelAssociationTemplate wfModelAssociationTemplate = new WfModelAssociationTemplate();
            wfModelAssociationTemplate.setModelId(model.getId());
            wfModelAssociationTemplate.setModelTemplateId(modelBo.getModelTemplateId());
            wfModelAssociationTemplateService.insertWfModelAssociationTemplate(wfModelAssociationTemplate);
        }

        return model.getId();
    }

    /**
     * 新增模型权限
     *
     * @param id
     * @param modelBo
     */
    private void insertWfModelPermissionList(String id, WfModelBo modelBo) {
        List<WfModelPermission> permissionsList = new ArrayList<>();
        List<WfModelPermission> deptList = modelBo.getSelectDeptList();
        List<WfModelPermission> userList = modelBo.getSelectUserList();
        if (ObjectUtil.isNotNull(deptList) && deptList.size() > 0) {
            for (WfModelPermission permission : deptList) {
                permission.setType(PermissionEnum.DEPT_PERMISSION.getCode());
                permission.setModelPermissionId(String.valueOf(idWorker.nextId()));
                permission.setModelId(id);
            }
            permissionsList.addAll(deptList);
        }

        if (ObjectUtil.isNotNull(deptList) && userList.size() > 0) {
            for (WfModelPermission permission : userList) {
                permission.setType(PermissionEnum.USER_PERMISSION.getCode());
                permission.setModelPermissionId(String.valueOf(idWorker.nextId()));
                permission.setModelId(id);
            }
            permissionsList.addAll(userList);
        }

        if (permissionsList.size() == 0) {
            WfModelPermission permission = new WfModelPermission();
            permission.setType(PermissionEnum.USER_PERMISSION.getCode());
            permission.setModelPermissionId(String.valueOf(idWorker.nextId()));
            permission.setModelId(id);
            permission.setPermissionId(SecurityUtils.getUserId());
            permission.setName(SecurityUtils.getLoginUser().getUser().getNickName());
            permissionsList.add(permission);
        }
        wfModelPermissionService.insertWfModelPermissionList(permissionsList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateModel(WfModelBo modelBo) {
        // 根据模型Key查询模型信息
        Model model = repositoryService.getModel(modelBo.getModelId());
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        model.setCategory(modelBo.getCategory());
        WfMetaInfoDto metaInfoDto = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
        String metaInfo = buildMetaInfo(metaInfoDto,
                modelBo.getDescription(),
                modelBo.getIcon(),
                modelBo.getFormType(),
                modelBo.getFormId(),
                modelBo.getFormName(),
                modelBo.getFormCustomCreatePath(),
                modelBo.getFormCustomViewPath(),
                modelBo.getMenuId(),
                modelBo.getMenuName(),
                modelBo.getProcessConfig(),
                modelBo.getModelTemplateId()
        );
        model.setMetaInfo(metaInfo);

        // 保存流程模型
        repositoryService.saveModel(model);
        checkModel(modelBo, model.getId());

        wfModelPermissionService.deleteWfModelPermissionByModelId(modelBo.getModelId());
        insertWfModelPermissionList(modelBo.getModelId(), modelBo);


        WfModelAssociationTemplate wfModelAssociationTemplate = new WfModelAssociationTemplate();
        wfModelAssociationTemplate.setModelId(model.getId());
        wfModelAssociationTemplate.setModelTemplateId(modelBo.getModelTemplateId());
        wfModelAssociationTemplateService.deleteWfModelAssociationTemplate(wfModelAssociationTemplate);
        if (ProcessConfigConstants.MODEL_TEMPLATE.equals(modelBo.getProcessConfig())) {
            wfModelAssociationTemplateService.insertWfModelAssociationTemplate(wfModelAssociationTemplate);
        }
    }

    /**
     * 校验模型
     *
     * @param modelBo
     * @param modelId
     */
    private void checkModel(WfModelBo modelBo, String modelId) {
        if (ObjectUtil.isNull(modelBo.getBpmnXml())) {
            throw new RuntimeException("请选设计流程模型！");
        }
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(modelBo.getBpmnXml());
        if (ObjectUtil.isEmpty(bpmnModel)) {
            throw new RuntimeException("获取模型设计失败！");
        }

        // 获取开始节点
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new RuntimeException("开始节点不存在，请检查流程设计是否有误！");
        }
        if (FormType.PROCESS.getType().equals(modelBo.getFormType())) {
            // 获取开始节点配置的表单Key
            if (StrUtil.isBlank(startEvent.getFormKey())) {
                throw new RuntimeException("请配置流程表单");
            }
        }

        //查看开始节点的后一个任务节点出口
        List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
        if (Objects.isNull(outgoingFlows)) {
            throw new RuntimeException("导入失败，流程配置错误！");
        }

        //遍历返回下一个节点信息
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

        // 保存 BPMN XML
        Process process = bpmnModel.getProcesses().get(0);
        if (StringUtils.isEmpty(modelBo.getModelId())) {
            process.setName(modelBo.getModelName());
            process.setId("process_" + idWorker.nextId());
        } else {
            process.setName(modelBo.getModelName());
        }
        repositoryService.addModelEditorSource(modelId, ModelUtils.getBpmnXml(bpmnModel));
    }

    /**
     * 保存流程模型信息
     *
     * @param modelBo 流程模型对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveModel(WfModelBo modelBo) {
        // 查询模型信息
        Model model = repositoryService.getModel(modelBo.getModelId());
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(modelBo.getBpmnXml());
        if (ObjectUtil.isEmpty(bpmnModel)) {
            throw new RuntimeException("获取模型设计失败！");
        }
//        String processName = bpmnModel.getMainProcess().getName();
        // 获取开始节点
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new RuntimeException("开始节点不存在，请检查流程设计是否有误！");
        }
        // 获取开始节点配置的表单Key
        if (StrUtil.isBlank(startEvent.getFormKey())) {
            throw new RuntimeException("请配置流程表单");
        }
        //查看开始节点的后一个任务节点出口
        List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
        if (Objects.isNull(outgoingFlows)) {
            throw new RuntimeException("导入失败，流程配置错误！");
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
        Model newModel;
        if (Boolean.TRUE.equals(modelBo.getNewVersion())) {
            newModel = repositoryService.newModel();
            newModel.setName(model.getName());
            newModel.setKey(model.getKey());
            newModel.setCategory(model.getCategory());
            newModel.setMetaInfo(model.getMetaInfo());
            newModel.setVersion(model.getVersion() + 1);
        } else {
            newModel = model;
            // 设置流程名称
            newModel.setName(model.getName());
        }
        // 保存流程模型
        repositoryService.saveModel(newModel);
        // 保存 BPMN XML
        byte[] bpmnXmlBytes = StringUtils.getBytes(modelBo.getBpmnXml(), StandardCharsets.UTF_8);
        repositoryService.addModelEditorSource(newModel.getId(), bpmnXmlBytes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void latestModel(String modelId) {
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        Integer latestVersion = repositoryService.createModelQuery().modelKey(model.getKey()).latestVersion().singleResult().getVersion();
        if (model.getVersion().equals(latestVersion)) {
            throw new RuntimeException("当前版本已是最新版！");
        }
        // 获取 BPMN XML
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        Model newModel = repositoryService.newModel();
        newModel.setName(model.getName());
        newModel.setKey(model.getKey());
        newModel.setCategory(model.getCategory());
        newModel.setMetaInfo(model.getMetaInfo());
        newModel.setVersion(latestVersion + 1);
        // 保存流程模型
        repositoryService.saveModel(newModel);
        // 保存 BPMN XML
        repositoryService.addModelEditorSource(newModel.getId(), bpmnBytes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Collection<String> ids) {
        for (String id : ids) {
            Model model = repositoryService.getModel(id);
            if (ObjectUtil.isNull(model)) {
                throw new RuntimeException("流程模型不存在！");
            }
            repositoryService.deleteModel(id);
            // 禁用流程定义
            updateProcessDefinitionSuspended(model.getDeploymentId());

            // 删除流程模型权限
            wfModelPermissionService.deleteWfModelPermissionByModelId(id);

            // 删除流程部署
            WfModelProcdef wfModelProcdef = new WfModelProcdef();
            wfModelProcdef.setModelId(id);
            List<WfModelProcdef> wfModelProcdefs = wfModelProcdefService.selectWfModelProcdefList(wfModelProcdef);
            for (WfModelProcdef modelProcdef : wfModelProcdefs) {
                wfDeployService.deleteByIds(Collections.singletonList(modelProcdef.getProcdefId()));
            }

            // 删除流程模型关联
            wfModelProcdefService.deleteWfModelProcdefByModelId(id);

            WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
            WfModelAssociationTemplate wfModelAssociationTemplate = new WfModelAssociationTemplate();
            wfModelAssociationTemplate.setModelId(model.getId());
            if (metaInfo != null) {
                wfModelAssociationTemplate.setModelTemplateId(metaInfo.getModelTemplateId());
            }
            wfModelAssociationTemplateService.deleteWfModelAssociationTemplate(wfModelAssociationTemplate);
        }
    }

    /**
     * 挂起 deploymentId 对应的流程定义
     * <p>
     * 注意：这里一个 deploymentId 只关联一个流程定义
     *
     * @param deploymentId 流程发布Id
     */
    private void updateProcessDefinitionSuspended(String deploymentId) {
        if (StrUtil.isEmpty(deploymentId)) {
            return;
        }
        ProcessDefinition oldDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        if (oldDefinition == null) {
            return;
        }
        // 挂起
        SuspensionState.SUSPENDED.getStateCode();
        SuspensionState.SUSPENDED.getStateCode();
        // suspendProcessInstances = false，进行中的任务，不进行挂起。
        // 原因：只要新的流程不允许发起即可，老流程继续可以执行。
        repositoryService.suspendProcessDefinitionById(oldDefinition.getId(), false, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deployModel(String modelId) {
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        // 获取流程图
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        if (ArrayUtil.isEmpty(bpmnBytes)) {
            throw new RuntimeException("请先设计流程图！");
        }
        String bpmnXml = StringUtils.toEncodedString(bpmnBytes, StandardCharsets.UTF_8);
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXml);
        String processName = model.getName() + ProcessConstants.SUFFIX;

        WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(model.getCategory())
                .addBytes(processName, bpmnBytes)
                .deploy();
        String deploymentId = deployment.getId();
        WfIcon wfIcon = new WfIcon();
        wfIcon.setDeploymentId(deploymentId);
        if (metaInfo != null) {
            wfIcon.setIcon(metaInfo.getIcon());
        }
        wfIconService.insertWfIcon(wfIcon);

        WfModelProcdef wfModelProcdef = new WfModelProcdef();
        wfModelProcdef.setModelId(modelId);
        wfModelProcdef.setProcdefId(deploymentId);
        if (metaInfo != null) {
            wfModelProcdef.setFormType(metaInfo.getFormType());
            wfModelProcdef.setFormCreatePath(metaInfo.getFormCustomCreatePath());
            wfModelProcdef.setFormViewPath(metaInfo.getFormCustomViewPath());
        }
        wfModelProcdefMapper.insertWfModelProcdef(wfModelProcdef);

        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        // 修改流程定义的分类，便于搜索流程
        repositoryService.setProcessDefinitionCategory(procDef.getId(), model.getCategory());

        if (metaInfo != null && FormType.PROCESS.getType().equals(metaInfo.getFormType())) {
            // 保存部署表单
            return deployFormService.saveInternalDeployForm(deployment.getId(), bpmnModel);
        }

        return true;
    }

    /**
     * 查询流程模型列表
     *
     * @param modelBo
     * @return
     */
    @Override
    public List<WfModelVo> selectList(WfModelBo modelBo) {
        List<WfModelVo> list = new ArrayList<>();
        List<ActReModel> actReModelList;
        if (SecurityUtils.hasRole("admin")) {
            actReModelList = wfModelMapper.selectList(modelBo);
        } else {
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            List<Long> userIdList = userServiceApi.listUserIdByDeptId(sysUser.getDeptId());
            List<Long> deptIdList;

            deptIdList = deptServiceApi.selectBranchDeptId(sysUser.getDeptId());
            deptIdList.add(sysUser.getDeptId());

            actReModelList = wfModelMapper.selectListVo(modelBo, userIdList, deptIdList);
        }

        for (ActReModel actReModel : actReModelList) {
            JSONObject.parseObject(actReModel.getMetaInfo(), WfMetaInfoDto.class);
            WfModelVo modelVo = new WfModelVo();
            modelVo.setModelId(actReModel.getId());
            modelVo.setModelName(actReModel.getName());
            modelVo.setModelKey(actReModel.getKey());
            modelVo.setCategory(actReModel.getCategory());
            modelVo.setCreateTime(actReModel.getCreateTime());
            modelVo.setVersion(actReModel.getVersion());
            WfMetaInfoDto metaInfo = JsonUtils.parseObject(actReModel.getMetaInfo(), WfMetaInfoDto.class);
            if (metaInfo != null) {
                modelVo.setDescription(metaInfo.getDescription());
                modelVo.setFormType(metaInfo.getFormType());
                modelVo.setFormId(metaInfo.getFormId());
                modelVo.setFormName(metaInfo.getFormName());
                modelVo.setFormCustomCreatePath(metaInfo.getFormCustomCreatePath());
                modelVo.setFormCustomViewPath(metaInfo.getFormCustomViewPath());
                modelVo.setIcon(metaInfo.getIcon());
                modelVo.setMenuId(metaInfo.getMenuId());
                modelVo.setMenuName(metaInfo.getMenuName());
                modelVo.setProcessConfig(metaInfo.getProcessConfig());
                modelVo.setModelTemplateId(metaInfo.getModelTemplateId());
            }
            list.add(modelVo);
        }
        return list;
    }

    /**
     * 根据菜单id查询流程模型列表
     *
     * @param menuId 菜单id
     * @return
     */
    @Override
    public List<WfModelVo> getModelByMenuId(String menuId) {
        List<WfModelVo> wfModelVoList = selectList(new WfModelBo());
        return wfModelVoList.stream().filter(modelVo -> menuId.equals(modelVo.getMenuId())).collect(Collectors.toList());
    }

    /**
     * 构建模型扩展信息
     *
     * @return
     */
    private String buildMetaInfo(WfMetaInfoDto metaInfo,
                                 String description,
                                 String icon,
                                 String formType,
                                 String formId,
                                 String formName,
                                 String formCustomCreatePath,
                                 String formCustomViewPath,
                                 String menuId,
                                 String menuName,
                                 String processConfig,
                                 String modelTemplateId
    ) {
        if (metaInfo == null) {
            metaInfo = new WfMetaInfoDto();
        }
        // 只有非空，才进行设置，避免更新时的覆盖
        if (StrUtil.isNotEmpty(description)) {
            metaInfo.setDescription(description);
        }
        if (Objects.nonNull(formType)) {
            metaInfo.setFormType(formType);
            metaInfo.setFormId(formId);
            metaInfo.setFormName(formName);
            metaInfo.setFormCustomCreatePath(formCustomCreatePath);
            metaInfo.setFormCustomViewPath(formCustomViewPath);
            metaInfo.setIcon(icon);
            metaInfo.setMenuId(menuId);
            metaInfo.setMenuName(menuName);
            metaInfo.setProcessConfig(processConfig);
            metaInfo.setModelTemplateId(modelTemplateId);
        }
        return JsonUtils.toJsonString(metaInfo);
    }
}
