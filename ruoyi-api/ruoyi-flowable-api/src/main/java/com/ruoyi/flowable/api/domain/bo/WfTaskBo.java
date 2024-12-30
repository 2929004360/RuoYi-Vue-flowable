package com.ruoyi.flowable.api.domain.bo;

import java.util.List;
import java.util.Map;

/**
 * 流程任务业务对象
 *
 * @author fengcheng
 * @createTime 2022/3/10 00:12
 */
public class WfTaskBo {
    /**
     * 自定义业务用数据Id
     */
    private String dataId;
    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 任务父级编号
     */
    private String parentTaskId;

    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 任务意见
     */
    private String comment;
    /**
     * 流程实例Id
     */
    private String procInsId;
    /**
     * 节点
     */
    private String targetKey;
    /**
     * 流程变量信息
     */
    private Map<String, Object> variables;
    /**
     * 审批人
     */
    private String assignee;
    /**
     * 候选人
     */
    private List<String> candidateUsers;
    /**
     * 审批组
     */
    private List<String> candidateGroups;
    /**
     * 抄送用户Id
     */
    private String copyUserIds;
    /**
     * 下一节点审批人
     */
    private String nextUserIds;
    /**
     * 下一节点接收人
     */
    private String nextApproval;
    /**
     * 自定义业务主键
     */
    private String businessKey;
    /**
     * 流程类型
     */
    private String category;
    /**
     * 加签用户
     */
    private String addSignUsers;
    /**
     * 加签类型
     */
    private String addSignType; //前加签0，后加签1，多实例加签2
    /**
     * 目标活动节点id，当跳转或退回或加签减签时使用
     */
    private String targetActId;
    /**
     * 目标活动节点名称，当跳转或退回或加签减签时使用
     */
    private String targetActName;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<String> getCandidateUsers() {
        return candidateUsers;
    }

    public void setCandidateUsers(List<String> candidateUsers) {
        this.candidateUsers = candidateUsers;
    }

    public List<String> getCandidateGroups() {
        return candidateGroups;
    }

    public void setCandidateGroups(List<String> candidateGroups) {
        this.candidateGroups = candidateGroups;
    }

    public String getCopyUserIds() {
        return copyUserIds;
    }

    public void setCopyUserIds(String copyUserIds) {
        this.copyUserIds = copyUserIds;
    }

    public String getNextUserIds() {
        return nextUserIds;
    }

    public void setNextUserIds(String nextUserIds) {
        this.nextUserIds = nextUserIds;
    }

    public String getNextApproval() {
        return nextApproval;
    }

    public void setNextApproval(String nextApproval) {
        this.nextApproval = nextApproval;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddSignUsers() {
        return addSignUsers;
    }

    public void setAddSignUsers(String addSignUsers) {
        this.addSignUsers = addSignUsers;
    }

    public String getAddSignType() {
        return addSignType;
    }

    public void setAddSignType(String addSignType) {
        this.addSignType = addSignType;
    }

    public String getTargetActId() {
        return targetActId;
    }

    public void setTargetActId(String targetActId) {
        this.targetActId = targetActId;
    }

    public String getTargetActName() {
        return targetActName;
    }

    public void setTargetActName(String targetActName) {
        this.targetActName = targetActName;
    }

    @Override
    public String toString() {
        return "WfTaskBo{" +
                "dataId='" + dataId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", parentTaskId='" + parentTaskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", userId='" + userId + '\'' +
                ", comment='" + comment + '\'' +
                ", procInsId='" + procInsId + '\'' +
                ", targetKey='" + targetKey + '\'' +
                ", variables=" + variables +
                ", assignee='" + assignee + '\'' +
                ", candidateUsers=" + candidateUsers +
                ", candidateGroups=" + candidateGroups +
                ", copyUserIds='" + copyUserIds + '\'' +
                ", nextUserIds='" + nextUserIds + '\'' +
                ", nextApproval='" + nextApproval + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", category='" + category + '\'' +
                ", addSignUsers='" + addSignUsers + '\'' +
                ", addSignType='" + addSignType + '\'' +
                ", targetActId='" + targetActId + '\'' +
                ", targetActName='" + targetActName + '\'' +
                '}';
    }
}
