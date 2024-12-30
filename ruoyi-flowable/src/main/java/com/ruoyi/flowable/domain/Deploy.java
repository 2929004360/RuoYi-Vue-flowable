package com.ruoyi.flowable.domain;

import com.ruoyi.flowable.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部署对象 deploy
 *
 * @author fengcheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Deploy extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 数据的唯一标识
     */
    private String id;

    /**
     * 数据的版本
     */
    private Integer rev;

    /**
     * 数据所属的类别
     */
    private String category;

    /**
     * 数据名称
     */
    private String name;

    /**
     * 数据的键
     */
    private String key;

    /**
     * 数据版本
     */
    private Integer version;

    /**
     * 部署数据的唯一标识
     */
    private String deploymentId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 模板资源名称
     */
    private String dgrmResourceName;

    /**
     * 数据描述
     */
    private String description;

    /**
     * 是否具有启动表单键
     */
    private Integer hasStartFormKey;

    /**
     * 是否有图形表示
     */
    private Integer hasGraphicalNotation;

    /**
     * 挂起状态
     */
    private Integer suspensionState;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 引擎版本
     */
    private String engineVersion;

    /**
     * 继承自
     */
    private String derivedFrom;

    /**
     * 根继承自
     */
    private String derivedFromRoot;

    /**
     * 继承版本
     */
    private Integer derivedVersion;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 表单创建路径
     */
    private String formCreatePath;
}
