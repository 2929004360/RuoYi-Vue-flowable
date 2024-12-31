<h4 align="center">基于RuoYi-Vue+Flowable的工作流管理平台</h4>


## 平台简介

基于RuoYi-Vue + Flowable一套全部开源的快速开发平台，毫无保留给个人及企业免费使用。

* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。
* 特别鸣谢：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)

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
19.  流程分类：流程的分类
20.  流程菜单：菜单关联流程
21.  表单配置：在线表单设计
22.  模型模板：实现流程模板，进行统一管理
23.  流程模型：在线流程设计
24.  部署管理：流程的版本的切换等
25.  发起流程：发起指定流程
26.  我的流程：查看自己发起的流程
27.  代办任务：获取审批的任务
28.  代签任务：任务的接收
29.  已办任务：获取自己审批过的流程
30.  抄送我的：流程的抄送

## 在线体验

- admin/admin123  

演示地址：http://ry.orangepromax.asia/index

## 联系方式

付费版咨询、技术咨询、项目定制开发等其它支持可扫码添加微信进行沟通交流。

<table>
    <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725db2703e9.jpg" style="zoom:25%;"/></td>
    </tr>
</table>

## 请作者喝杯咖啡 ~ (*^▽^*)

<table>
    <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725e951d13a.jpg" style="zoom:25%;"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725e8a6e27b.jpg" style="zoom:25%;"/></td>
    </tr>
</table>



## 演示图

<table>
    <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725bc908c48.png"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c037a705.png"/></td>
    </tr>
     <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c3fbf3cd.png"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c40021c8.png"/></td>
    </tr>
    <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c43e6f81.png"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c4212b1e.png"/></td>
    </tr>
     <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c43a29ff.png"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c48efd3c.png"/></td>
    </tr>
     <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c483f6e1.png"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c48e9ec1.png"/></td>
    </tr>
      <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c4c1f22a.png"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725c4bd4451.png"/></td>
    </tr>
      <tr>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725d5c73a33.png"/></td>
        <td><img src="https://www.helloimg.com/i/2024/12/30/67725d58c3312.png"/></td>
    </tr>
</table>

## 推荐

大家在使用本项目时，推荐结合贺波老师的书 [《深入Flowable流程引擎：核心原理与高阶实战》](https://gitee.com/link?target=https%3A%2F%2Fitem.jd.com%2F14804836.html)学习。这本书得到了Flowable创始人Tijs Rademakers亲笔作序推荐，对系统学习和深入掌握Flowable的用法非常有帮助。 ![img](https://foruda.gitee.com/images/1727432593738798662/46c08088_2042292.png)
