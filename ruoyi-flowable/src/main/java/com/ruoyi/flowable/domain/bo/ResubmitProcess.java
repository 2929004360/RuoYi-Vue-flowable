package com.ruoyi.flowable.domain.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 重新发起流程
 *
 * @author fengcheng
 */
@Data
public class ResubmitProcess implements Serializable {

    /**
     * 流程id
     */
    @NotBlank(message = "流程id不能为空")
    private String procInsId;

    /**
     * 业务id
     */
    @NotBlank(message = "业务id不能为空")
    private String businessId;
}
