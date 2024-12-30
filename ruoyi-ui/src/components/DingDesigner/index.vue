<template>
  <div class="page">
    <section class="page__content" v-if="designerData">
      <Process
        ref="processDesign"
        :conf="designerData"
        @startNodeChange="onStartChange"/>
    </section>
    <div class="publish">
      <el-button size="mini" type="primary" @click="previewJson"><i class="el-icon-view"></i>预览JSON</el-button>
      <el-button size="mini" type="primary" @click="previewXml"><i class="el-icon-view"></i>预览XML</el-button>
      <el-button size="mini" type="primary" @click="previewBpmn"><i class="el-icon-view"></i>bpmn预览</el-button>
      <el-button size="mini" type="primary" @click="save"><i class="el-icon-s-claim"></i>保存</el-button>
      <!--      <el-button size="mini" type="primary" @click="publish"><i class="el-icon-s-promotion"></i>发布</el-button>-->
    </div>
    <el-dialog title="预览" width="60%" :visible.sync="previewModelVisible" append-to-body destroy-on-close>
      <highlightjs :language="previewType" :code="previewResult" style="height: 70vh"/>
    </el-dialog>
    <!-- Bpmn流程图 -->
    <el-dialog :title="processView.title" :visible.sync="processView.open" width="70%" append-to-body>
      <process-viewer :key="`designer-${processView.title}`" :xml="processView.xmlData" :style="{height: '700px'}"/>
    </el-dialog>

    <!--<el-dialog :title="designerData.title" :visible.sync="designerOpen" append-to-body fullscreen>
      <process-designer
        :key="designerOpen"
        style="border:1px solid rgba(0, 0, 0, 0.1);"
        ref="modelDesigner"
        v-loading="designerData.loading"
        :bpmnXml="designerData.bpmnXml"
        :designerForm="designerData.form"
        @save="onSaveDesigner"
      />
    </el-dialog>-->
  </div>
</template>

<script>
// @ is an alias to /src
import Vue from 'vue';
import {dingdingToBpmn} from '@/api/workflow/process'
import Process from "./components/Process";
import ProcessDesigner from '@/components/ProcessDesigner';
import ProcessViewer from '@/components/ProcessViewer';
import {vuePlugin} from '@/plugins/package/highlight';
import 'highlight.js/styles/atom-one-dark-reasonable.css';
// 引入json转换与高亮
Vue.use(vuePlugin);
//目前不加这个提示没有加载组件，具体我不明
Vue.component('ProcessViewer', ProcessViewer);

const beforeUnload = function (e) {
  var confirmationMessage = '离开网站可能会丢失您编辑得内容';
  (e || window.event).returnValue = confirmationMessage;     // Gecko and Trident
  return confirmationMessage;                                // Gecko and WebKit
}

export default {
  name: "DingDesigner",
  components: {
    Process,
    ProcessDesigner,
    ProcessViewer,
  },
  props: {
    designerData: {
      type: Object,
      required: true
    },
    name: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      mockData: null, // 可选择诸如 $route.param，Ajax获取数据等方式自行注入
      previewModelVisible: false,
      activeStep: "processDesign", // 激活的步骤面板
      previewResult: "",
      previewType: "xml",
      designerOpen: false,
      processView: {
        title: '',
        open: false,
        xmlData: "",
      },
      steps: [
        {label: "流程设计", key: "processDesign"}
      ]
    };
  },
  beforeRouteEnter(to, from, next) {
    window.addEventListener('beforeunload', beforeUnload)
    next()
  },
  beforeRouteLeave(to, from, next) {
    window.removeEventListener('beforeunload', beforeUnload)
    next()
  },
  computed: {
    translateX() {
      return `translateX(${this.steps.findIndex(t => t.key === this.activeStep) * 100}%)`
    }
  },
  mounted() {
    console.log("mounted designerData", this.designerData);
  },
  methods: {
    changeSteps(item) {
      this.activeStep = item.key;
    },
    previewJson() {
      const getCmpData = name => this.$refs[name].getData()
      // processDesign 返回的是Promise 因为要做校验
      console.log("publish getCmpData", getCmpData)
      const p3 = getCmpData('processDesign')
      console.log("publish p3", p3)
      Promise.all([p3])
        .then(res => {
          const param = {
            processData: res[0].formData
          }
          this.previewResult = JSON.stringify(param, null, 2);//json格式化
          this.previewType = "json";
          this.previewModelVisible = true;
        })
        .catch(err => {
          err.target && (this.activeStep = err.target)
          err.msg && this.$message.warning(err.msg)
        })
    },
    previewXml() {
      const getCmpData = name => this.$refs[name].getData()
      // processDesign 返回的是Promise 因为要做校验
      console.log("publish getCmpData", getCmpData)
      const p3 = getCmpData('processDesign')
      console.log("publish p3", p3)
      Promise.all([p3])
        .then(res => {
          const param = {
            processData: res[0].formData
          }
          var convert = require('xml-js');
          var json = JSON.stringify(param, null, 2);//json格式化
          // 启动流程并将表单数据加入流程变量
          dingdingToBpmn({json, name: this.name}).then(res => {
            console.log("dingdingToBpmn res", res)
            this.previewResult = res.data;
            this.previewType = "xml";
            this.previewModelVisible = true
          })
        })
        .catch(err => {
          err.target && (this.activeStep = err.target)
          err.msg && this.$message.warning(err.msg)
        })
    },
    previewBpmn() {
      const getCmpData = name => this.$refs[name].getData()
      // processDesign 返回的是Promise 因为要做校验
      console.log("publish getCmpData", getCmpData)
      const p3 = getCmpData('processDesign')
      console.log("publish p3", p3)
      Promise.all([p3])
        .then(res => {
          const param = {
            processData: res[0].formData
          }
          var convert = require('xml-js');
          var json = JSON.stringify(param, null, 2);//json格式化
          // 钉钉流程转为bpmn格式
          dingdingToBpmn({json, name: this.name}).then(reponse => {
            console.log("dingdingToBpmn reponse", reponse)
            this.processView.title = "Bpmn流程图预览";
            this.processView.xmlData = reponse.data;
          })
          this.processView.open = true;
        })
        .catch(err => {
          err.target && (this.activeStep = err.target)
          err.msg && this.$message.warning(err.msg)
        })
    },
    publish() {
      const getCmpData = name => this.$refs[name].getData()
      // processDesign 返回的是Promise 因为要做校验
      console.log("publish getCmpData", getCmpData)
      const p3 = getCmpData('processDesign')
      console.log("publish p3", p3)
      Promise.all([p3])
        .then(res => {
          const param = {
            processData: res[0].formData
          }
          this.sendToServer(param)
        })
        .catch(err => {
          err.target && (this.activeStep = err.target)
          err.msg && this.$message.warning(err.msg)
        })
    },
    /** 发布按钮操作 */
    /*publish() {
      const getCmpData = name => this.$refs[name].getData()
      // processDesign 返回的是Promise 因为要做校验
      console.log("publish getCmpData",getCmpData)
      const p3 = getCmpData('processDesign')
      console.log("publish p3",p3)
      Promise.all([p3])
      .then(res => {
        const param = {
          processData: res[0].formData
        }
        var json = JSON.stringify(param, null, 2);//json格式化
        // 启动流程并将表单数据加入流程变量
        dingdingToBpmn(json).then(res => {
          console.log("dingdingToBpmn res",res)
          if(res.code == 200) {
            this.designerData.title = "流程图";
            this.designerData.bpmnXml = res.msg;
            this.designerOpen = true;
          }
        })
      })
      .catch(err => {
        err.target && (this.activeStep = err.target)
        err.msg && this.$message.warning(err.msg)
      })
    },*/
    sendToServer(param) {
      this.$notify({
        title: '数据已整合完成',
        message: '请在预览中查看数据输出',
        position: 'bottom-right'
      });
      console.log('配置数据', param)
    },
    save() {

      const getCmpData = name => this.$refs[name].getData()
      // processDesign 返回的是Promise 因为要做校验
      console.log("publish getCmpData", getCmpData)
      const p3 = getCmpData('processDesign')
      console.log("publish p3", p3)
      Promise.all([p3])
        .then(res => {
          this.$confirm("是否将此模型保存为新版本？", "提示", {
            distinguishCancelAndClose: true,
            confirmButtonText: '是',
            cancelButtonText: '否'
          }).then(() => {
            const flowData = {
              processData: res[0].formData
            }
            this.$emit('save', flowData);
          }).catch(action => {

          })
        })
        .catch(err => {
          err.target && (this.activeStep = err.target)
          err.msg && this.$message.warning(err.msg)
        })

    },
    exit() {
      this.$confirm('离开此页面您得修改将会丢失, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message({
          type: 'success',
          message: '模拟返回!'
        });
      }).catch(() => {
      });
    },
    /**
     * 同步基础设置发起人和流程节点发起人
     */
    onInitiatorChange(val, labels) {
      const processCmp = this.$refs.processDesign
      const startNode = processCmp.data
      startNode.properties.initiator = val['dep&user']
      startNode.content = labels || '所有人'
      processCmp.forceUpdate()
    },
    /**
     * 监听 流程节点发起人改变 并同步到基础设置 发起人数据
     */
    onStartChange(node) {
      //const basicSetting = this.$refs.basicSetting
      //basicSetting.formData.initiator = { 'dep&user': node.properties.initiator }
    }
  },
};
</script>
<style lang="stylus" scoped>
.publish {
  position: absolute;
  top: 15px;
  right: 80px;
  z-index: 1000;

  i {
    margin-right: 6px;
  }

  button {
    border-radius: 15px;
  }
}

</style>
