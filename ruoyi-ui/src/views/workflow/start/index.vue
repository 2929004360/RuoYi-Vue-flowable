<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>发起【{{ processName }}】流程</span>
      </div>
      <el-col :span="18" :offset="3">
        <div class="form-conf" v-if="formOpen">
          <parser :key="new Date().getTime()" :form-conf="formData" @submit="submit" ref="parser" @getData="getData"/>
        </div>
      </el-col>
    </el-card>

    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>流程图</span>
      </div>
      <process-viewer :key="`designer-${definitionId}`" :xml="xmlData" :style="{height: '700px'}"/>
    </el-card>
  </div>
</template>

<script>
import {getProcessForm, startProcess} from '@/api/workflow/process'
import Parser from '@/utils/generator/parser'
import {getBpmnXml} from "@/api/workflow/process";
import ProcessViewer from '@/components/ProcessViewer'

export default {
  name: 'WorkStart',
  components: {
    Parser,
    ProcessViewer
  },
  data() {
    return {
      definitionId: null,
      deployId: null,
      procInsId: null,
      formOpen: false,
      processName: null,
      xmlData: null,
      formData: {},
    }
  },
  created() {
    this.initData();
  },
  methods: {
    initData() {
      this.deployId = this.$route.params && this.$route.params.deployId;
      this.definitionId = this.$route.query && this.$route.query.definitionId;
      this.procInsId = this.$route.query && this.$route.query.procInsId;
      this.processName = this.$route.query && this.$route.query.processName;
      getProcessForm({
        definitionId: this.definitionId,
        deployId: this.deployId,
        procInsId: this.procInsId
      }).then(res => {
        if (res.data) {
          this.formData = res.data;
          this.formOpen = true
        }
      })

      // 发送请求，获取xml
      getBpmnXml(this.definitionId).then(response => {
        this.xmlData = response.data
      })
    },
    /** 接收子组件传的值 */
    getData(data) {
      if (data) {
        const variables = [];
        data.fields.forEach(item => {
          let variableData = {};
          variableData.label = item.__config__.label
          // 表单值为多个选项时
          if (item.__config__.defaultValue instanceof Array) {
            const array = [];
            item.__config__.defaultValue.forEach(val => {
              array.push(val)
            })
            variableData.val = array;
          } else {
            variableData.val = item.__config__.defaultValue
          }
          variables.push(variableData)
        })
        this.variables = variables;
      }
    },
    submit(data) {
      if (data && this.definitionId) {
        // 启动流程并将表单数据加入流程变量
        startProcess(this.definitionId, JSON.stringify(data.valData)).then(res => {
          this.$modal.msgSuccess(res.msg);
          this.$tab.closeOpenPage({
            path: '/process/own'
          })
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.form-conf {
  margin: 15px auto;
  width: 80%;
  padding: 15px;
}
</style>
