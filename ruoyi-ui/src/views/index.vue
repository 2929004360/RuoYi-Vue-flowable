<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2>基于RuoYi-Vue+Flowable的工作流管理平台</h2>
        <p>
          我看咯蛮多工作流开源项目的，发现不合适我们公司产品，所有我直接仿照别人做咯一个，新增咯模型模板，流程菜单等功能，希望对大家有帮助，谢谢！
        </p>
        <p>
          <b>当前版本:</b> <span>v{{ version }}</span>
        </p>
        <p>
          <el-tag type="danger">&yen;免费开源</el-tag>
        </p>
        <p>
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-cloudy"
            plain
            @click="goTarget('https://gitee.com/tangwenzhaoaini/RuoYi-Vue-flowable')"
          >访问码云
          </el-button
          >
        </p>
      </el-col>

      <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <el-row>
          <el-col :span="12">
            <h2>技术选型:感谢Ruoyi-Vue</h2>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <h4>后端技术</h4>
            <ul>
              <li>SpringBoot</li>
              <li>Spring Security</li>
              <li>MyBatis-Plus</li>
              <li>Flowable</li>
              <li>JWT</li>
              <li>...</li>
            </ul>
          </el-col>
          <el-col :span="6">
            <h4>前端技术</h4>
            <ul>
              <li>Vue</li>
              <li>Vuex</li>
              <li>Element-ui</li>
              <li>Axios</li>
              <li>Sass</li>
              <li>...</li>
            </ul>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-divider/>
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>联系信息</span>
          </div>
          <div class="body">
            <p>
              <i class="el-icon-s-promotion"></i> 演示地址：
              <el-link
                href="http://www.ruoyi.vip"
                target="_blank"
              >http://ry.orangepromax.asia/index
              </el-link
              >
            </p>
            <p>
              <i class="el-icon-chat-dot-round"></i> 微信：<a
              href="javascript:;"
            >NYHHDWGZL 添加备注来意</a
            >
            </p>
            <p>
              <i class="el-icon-money"></i> QQ：<a
              href="javascript:;"
              class="QQ"
            >2929004360 添加备注来意</a
            >
            </p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>更新日志</span>
          </div>
          <el-collapse accordion v-model="activeNames">
            <el-collapse-item title="v1.0.0 - 2024-12-31" name="1">
              <ol>
                <li>RuoYi-Vue+Flowable的工作流管理平台系统正式发布</li>
              </ol>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>捐赠支持</span>
          </div>
          <div class="body">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="24" :md="24" :lg="12">
                <img
                  src="@/assets/images/wx.jpg"
                  alt="donate"
                  width="250px"
                  height="320px"
                />
              </el-col>
              <el-col :xs="24" :sm="24" :md="12" :lg="12">
                <img
                  src="@/assets/images/zfb.jpg"
                  alt="donate"
                  width="250px"
                  height="320px"
                />
              </el-col>
            </el-row>
          </div>
          <span style="display: inline-block; height: 30px; line-height: 30px"
          >你可以请作者喝杯咖啡表示鼓励</span>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "1.0.0",
      activeNames: '1'
    };
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    }
  }
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

