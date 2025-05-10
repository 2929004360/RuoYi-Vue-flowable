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
 * 待审批通知模板
 *
 * @author fengcheng
 */
@Component
public class PendingApprovalTemplate {

    /**
     * 待审批通知模板id
     */
    @Value("${wx.pendingApprovalId}")
    private String pendingApprovalId;

    @Lazy
    @Autowired
    private WxMaService wxMaService;

    /**
     * 发送待审批通知
     *
     * @param openid     接收人
     * @param deptName   申请部门
     * @param userName   申请人
     * @param riskArea   隐患区域
     * @param createTime 发起时间
     * @param schedule   审批进度
     * @return
     */
    public void sending(String openid,String deptName, String userName, String riskArea, String createTime, String schedule) throws WxErrorException {
        List<WxMaSubscribeMessage.MsgData> msgData = Arrays.asList(
                new WxMaSubscribeMessage.MsgData("thing8", deptName),
                new WxMaSubscribeMessage.MsgData("name1", userName),
                new WxMaSubscribeMessage.MsgData("thing76", riskArea),
                new WxMaSubscribeMessage.MsgData("time72", createTime),
                new WxMaSubscribeMessage.MsgData("phrase32", schedule)
        );
        WxMaSubscribeMessage message = WxMaSubscribeMessage.builder()
                .toUser(openid)
                .templateId(pendingApprovalId)
                .data(msgData)
                .build();

        wxMaService.getMsgService().sendSubscribeMsg(message);
    }
}
