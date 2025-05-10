package com.ruoyi.common.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录
 *
 * @author fengcheng
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WechatLogin {

    /**
     * code
     */
    @NotBlank(message = "code不能为空")
    private String code;

    /**
     * 加密串
     */
    @NotBlank(message = "加密串不能为空")
    private String encryptedData;

    /**
     * 偏移串
     */
    @NotBlank(message = "偏移串不能为空")
    private String iv;
}
