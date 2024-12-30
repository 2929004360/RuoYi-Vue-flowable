package com.ruoyi.flowable.domain;

import com.ruoyi.flowable.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 流程模型对象 ACT_RE_MODEL
 *
 * @author fengcheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActReModel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 数据的唯一标识
     */
    private String id;

    /**
     * 数据版本号
     */
    private Integer rev;

    /**
     * 数据名称
     */
    private String name;

    /**
     * 数据键
     */
    private String key;

    /**
     * 数据类别
     */
    private String category;

    /**
     * 数据创建时间
     */
    private Date createTime;

    /**
     * 数据最后更新时间
     */
    private Date lastUpdateTime;

    /**
     * 数据版本号
     */
    private Integer version;

    /**
     * 数据元信息
     */
    private String metaInfo;

    /**
     * 部署ID
     */
    private String deploymentId;

    /**
     * 编辑器值ID
     */
    private String editorSourceValueId;

    /**
     * 辑器额外值ID
     */
    private String editorSourceExtraValueId;

    /**
     * 租户ID
     */
    private String tenantId;
}
