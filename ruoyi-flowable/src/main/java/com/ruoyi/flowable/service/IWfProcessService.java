package com.ruoyi.flowable.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.flowable.core.FormConf;
import com.ruoyi.flowable.core.domain.ProcessQuery;
import com.ruoyi.flowable.domain.bo.DdToBpmn;
import com.ruoyi.flowable.domain.bo.ResubmitProcess;
import com.ruoyi.flowable.domain.vo.WfDefinitionVo;
import com.ruoyi.flowable.domain.vo.WfDetailVo;
import com.ruoyi.flowable.domain.vo.WfTaskVo;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

/**
 * 流程服务接口
 *
 * @author fengcheng
 * @createTime 2022/3/24 18:57
 */
public interface IWfProcessService {

    /**
     * 查询可发起流程列表
     *
     * @param processQuery 查询参数
     * @return
     */
    List<WfDefinitionVo> selectPageStartProcessList(ProcessQuery processQuery);

    /**
     * 查询可发起流程列表
     */
    List<WfDefinitionVo> selectStartProcessList(ProcessQuery processQuery);

    /**
     * 查询我的流程列表
     *
     * @param pageQuery 分页参数
     */
    TableDataInfo<WfTaskVo> selectPageOwnProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * 查询我的流程列表
     */
    List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery);

    /**
     * 查询代办任务列表
     *
     * @param pageQuery 分页参数
     */
    TableDataInfo<WfTaskVo> selectPageTodoProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * 查询代办任务列表
     */
    List<WfTaskVo> selectTodoProcessList(ProcessQuery processQuery);

    /**
     * 查询待签任务列表
     *
     * @param pageQuery 分页参数
     */
    TableDataInfo<WfTaskVo> selectPageClaimProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * 查询待签任务列表
     */
    List<WfTaskVo> selectClaimProcessList(ProcessQuery processQuery);

    /**
     * 查询已办任务列表
     *
     * @param pageQuery 分页参数
     */
    TableDataInfo<WfTaskVo> selectPageFinishedProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * 查询已办任务列表
     */
    List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery);

    /**
     * 查询流程部署关联表单信息
     *
     * @param definitionId 流程定义ID
     * @param deployId     部署ID
     */
    FormConf selectFormContent(String definitionId, String deployId, String procInsId);

    /**
     * 启动流程实例
     *
     * @param procDefId 流程定义ID
     * @param variables 扩展参数
     * @return
     */
    ProcessInstance startProcessByDefId(String procDefId, Map<String, Object> variables);

    /**
     * 通过DefinitionKey启动流程
     *
     * @param procDefKey 流程定义Key
     * @param variables  扩展参数
     */
    void startProcessByDefKey(String procDefKey, Map<String, Object> variables);

    /**
     * 删除流程实例
     */
    void deleteProcessByIds(String[] instanceIds);


    /**
     * 读取xml文件
     *
     * @param processDefId 流程定义ID
     */
    String queryBpmnXmlById(String processDefId);


    /**
     * 查询流程任务详情信息
     *
     * @param procInsId 流程实例ID
     * @param taskId    任务ID
     * @param formType  表单类型
     */
    WfDetailVo queryProcessDetail(String procInsId, String taskId, String formType);

    /**
     * 根据钉钉流程json转flowable的bpmn的xml格式
     *
     * @param ddToBpmn
     * @return
     */
    R<String> dingdingToBpmn(DdToBpmn ddToBpmn);

    /**
     * 根据菜单id获取可发起列表
     *
     * @param menuId
     * @return
     */
    List<WfDefinitionVo> getStartList(String menuId);

    /**
     * 重新发起流程实例
     *
     * @param resubmitProcess 重新发起
     */
    void resubmitProcess(ResubmitProcess resubmitProcess);

    /**
     * 查询流程是否结束
     *
     * @param procInsId
     * @param
     */
    boolean processIsCompleted(String procInsId);
}
