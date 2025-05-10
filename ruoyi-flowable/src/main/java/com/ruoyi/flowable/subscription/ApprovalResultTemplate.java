package com.ruoyi.flowable.subscription;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 审批结果通知模板
 *
 * @author fengcheng
 */
@Component
public class ApprovalResultTemplate {

    /**
     * 审批结果通知模板id
     */
    @Value("${wx.approvalResultId}")
    private String approvalResultId;

    @Lazy
    @Autowired
    private WxMaService wxMaService;

    /**
     * 发送审批结果通知
     *
     * @param openid               接收人
     * @param approver             审批人
     * @param approvalResult       审批结果
     * @param approvalInstructions 审批意见
     * @param approvalTime         审批时间
     * @param remark               备注
     * @throws WxErrorException
     */
    public void sending(String openid, String approver, String approvalResult, String approvalInstructions, String approvalTime, String remark) throws WxErrorException {
        List<WxMaSubscribeMessage.MsgData> msgData = Arrays.asList(
                new WxMaSubscribeMessage.MsgData("name2", approver),
                new WxMaSubscribeMessage.MsgData("phrase1", approvalResult),
                new WxMaSubscribeMessage.MsgData("thing11", approvalInstructions),
                new WxMaSubscribeMessage.MsgData("time26", approvalTime),
                new WxMaSubscribeMessage.MsgData("thing20", remark)
        );
        WxMaSubscribeMessage message = WxMaSubscribeMessage.builder()
                .toUser(openid)
                .templateId(approvalResultId)
                .data(msgData)
                .build();

        wxMaService.getMsgService().sendSubscribeMsg(message);
    }
}
