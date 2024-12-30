package com.ruoyi.flowable.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.flowable.domain.WfModelPermission;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 流程模型视图对象
 *
 * @author fengcheng
 * @createTime 2022/6/21 9:16
 */
@Data
public class WfModelVo {
    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 菜单id
     */
    private String menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 模型Key
     */
    private String modelKey;
    /**
     * 分类编码
     */
    private String category;
    /**
     * 版本
     */
    private Integer version;
    /**
     * 表单类型
     */
    private String formType;

    /**
     * 表单主键
     */
    private String formId;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 流程配置
     */
    private String processConfig;

    /**
     * 模板id
     */
    private String modelTemplateId;

    /**
     * 表单查看路由
     */
    private String formCustomViewPath;

    /**
     * 表单提交路由
     */
    private String formCustomCreatePath;

    /**
     * 流程图标
     */
    private String icon;

    /**
     * 模型描述
     */
    private String description;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 流程xml
     */
    private String bpmnXml;
    /**
     * 表单内容
     */
    private String content;

    /**
     * 使用人
     */
    private List<WfModelPermission> selectUserList;

    /**
     * 使用部门
     */
    private List<WfModelPermission> selectDeptList;
}
