
<p align="center">
	<img alt="logo" src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/file/2025/01/17/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20241225093539_20250117140804A020.jpg" style="width: 80px;">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">RuoYi-Vue+Flowable工作流管理</h1>
<h4 align="center">基于RuoYi-Vue+Flowable的工作流管理平台</h4>

## 平台简介
RuoYi-Vue 是一款基于 Vue.js 和 Spring Boot 构建的前后端分离的轻量级企业级管理系统。其架构清晰、模块化、功能丰富，并且支持灵活扩展，适用于各种管理和数据可视化的需求。Flowable 是一个轻量级的、开源的工作流引擎，它提供了丰富的流程引擎功能，支持 BPMN 2.0 流程标准，能够有效支持业务流程的建模、执行、监控等多种需求。

RuoYi-Vue + Flowable 工作流管理平台结合的优势：
1. 低代码化开发：通过 Flowable 提供的工作流引擎和 RuoYi-Vue 的前后端分离架构，企业可以快速搭建高效、灵活的业务流程管理系统。
2. 可视化流程设计：使用 Flowable 提供的流程设计器，用户可以直观地设计和配置复杂的业务流程，流程图能够清晰展示各个环节的状态和转移。
3. 高效工作流引擎：Flowable 支持多种业务场景的自动化流转，例如审批、任务分配、条件判断等，大大提高了企业的工作效率和协作能力。
4. 灵活的权限管理：结合 RuoYi-Vue 的角色和权限管理功能，能够为不同的用户配置不同的操作权限，有效保障流程执行的安全性。
5. 跨平台支持：基于 Vue.js 的前端应用能够实现响应式设计，支持各种终端设备（如手机、平板、PC）的访问，适应现代办公的多场景需求。
6. 系统集成能力强：RuoYi-Vue + Flowable 支持与现有的企业系统进行灵活的集成，能够实现不同系统间的数据交换和工作流的无缝对接。

## 技术栈

* 前端采用Vue2、Element-Ui、Vue3、Element-Plus。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。

# 我的开源项目

* [ruoyi-wvp](https://gitee.com/xiaochemgzi/RuoYi-Wvp)  基于ruoyi-vue的流媒体平台。
* [电子签章系统](https://gitee.com/tangwenzhaoaini/ruoyi-sign)  基于SpringBoot+Vue+Flowable前后端分离的电子签章系统。
* [RuoYi-Vue-Tenant](https://gitee.com/tangwenzhaoaini/ruo-yi-vue-tenant)  基于RuoYi-Vue的多租户管理平台。
* [RuoYi-Vue-flowable](https://gitee.com/tangwenzhaoaini/RuoYi-Vue-flowable)  基于RuoYi-Vue + flowable 的工作流管理平台。
* [RuoYi-Vue-Flowable-Tenant](https://gitee.com/tangwenzhaoaini/ruo-yi-vue-flowable-tenant)  基于RuoYi-Vue + flowable 的多租户工作流管理平台。
* [ruoyi-iot](https://gitee.com/xiaochemgzi/ruoyi-iot)  基于SpringBoot+Vue3前后端分离的Java物联网平台。
* [rtsp视频分析系统](https://gitee.com/xiaochemgzi/rtsp-video-analysis-system)  基于SpringBoot+Vue前后端分离的rtsp视频分析系统。
* [口罩分析流媒体服务器](https://gitee.com/xiaochemgzi/rtsp-ai)  基于SpringBoot+Vue前后端分离的口罩识别系统。

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存信息查询，命令统计等。
17. 在线构建器：拖动表单元素生成相应的HTML代码。
18. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。
19. 流程分类：流程的分类
20. 流程菜单：菜单关联流程
21. 表单配置：在线表单设计
22. 模型模板：实现流程模板，进行统一管理
23. 流程模型：在线流程设计
24. 部署管理：流程的版本的切换等
25. 发起流程：发起指定流程
26. 我的流程：查看自己发起的流程
27. 代办任务：获取审批的任务
28. 代签任务：任务的接收
29. 已办任务：获取自己审批过的流程
30. 抄送我的：流程的抄送

## 前端源码
### pc网页端
加qq群获取最新前端代码

<p align="center">
 <img width="400" alt="logo" src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/17/7e0678c3f21547f0a427ddca506eab17.jpg">
</p>

### 小程序端（非开源）

## 在线体验

演示地址：http://flowable.ry-wvp.xyz   <br />
账号：ry <br />
密码：123456 <br />

## 演示图
### 网页端
<table>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/6a47c1914e054143a1ffe757a2bfebf3.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/0e7da63c7aab4f629dc5536086c1b134.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/1c4bdd6748bc49c7a29c77311c86b515.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/b8e7e31b9388490abea11d435640181c.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/7b3b030e8de24896bd73c66b71c4c10f.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/3b1ebd8396984c33b1ab438c160170c6.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/b01917e6cf3f4bedb3c8232735c568f4.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/af64be8ec11a4bae8e1351662e1cdff8.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/8cc8543ba4bb4f46b65daa788263e9a3.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/f4f4854046f8452fb56b442eb9c6417d.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/6ad22d973e7f4c1d888e41dce059af75.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/07d511faa500402aafcbce2a3314f0d8.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/222fb22d1abf4d2e82ffbdb1fa0fc949.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/2dabf42a9b8c44b0a71ff409fb4e265b.png"/></td>
</tr>
<tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/a4180e06c9a744beb9ac007a81ae704f.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/9661d43dd68f47709af739d93ad3ad23.png"/></td>
</tr>
</table>

### 小程序端

<table>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/1d59a65ba61f49f991f7e02efd987e5c.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/82e37bf1a78440b2ab3b2bf1800ab5d6.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/d69a9d0a88cc4d63a50a48b285585c77.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/c558ce6f962249e0a6327b5869cd3808.png"/></td>
</tr>
 <tr>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/116afb81a0914d39bc2ceacd835dedf7.png"/></td>
    <td><img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/28/8d39d9999df5446bbe8c85357d566bfe.png"/></td>
</tr>
</table>
## 授权协议

本项目自有代码使用宽松的MIT协议，在保留版权信息的情况下可以自由应用于各自商用、非商业的项目。 但是本项目也零碎的使用了一些其他的开源代码，在商用的情况下请自行替代或剔除； 由于使用本项目而产生的商业纠纷或侵权行为一概与本项目及开发者无关，请自行承担法律风险。 在使用本项目代码时，也应该在授权协议中同时表明本项目依赖的第三方库的协议。

## 联系方式

* 微信1: chenbai0511 备注：电子签章系统
* 微信2: NYHHDWGZL  备注：电子签章系统

<p align="center">
    <img width="200" src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/17/a8663f16829b4871a94c9534a6dad894.jpg"/>
    <img width="200" src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/17/4601cea6e8cc4df18124bfc1af715dc7.png"/>
</p>

## 付费社群

<img src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/21/d96d8285328e4ecdbd459018d5ab55c5.jpg"/>

## 特别致谢

- 感谢作者[若依](https://ruoyi.vip/) 开源了这么棒快速开发框架。

## 赞赏方式

全面支持微信/支付宝赞赏,江山父老可以请作者喝杯☕️咖啡吗qwq

如果你感觉项目对你有帮助,可以扫码进行捐赠qwq

<img width="700" src="https://gdhxkj.oss-cn-guangzhou.aliyuncs.com/2025/04/17/bcfef20f53ee49e8baf2559f97e85ffb.jpg"/>
