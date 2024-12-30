<template>
  <div style="padding: 0 20px">
    <el-checkbox-group v-model="buttonList" @change="buttonListChange">
      <el-checkbox label="cc">抄送人</el-checkbox>
      <el-checkbox label="approval">审批人</el-checkbox>
      <el-checkbox label="adopt">通过</el-checkbox>
      <el-checkbox label="delegate">委派</el-checkbox>
      <el-checkbox label="addSign">加签</el-checkbox>
      <el-checkbox label="jump">跳转</el-checkbox>
      <el-checkbox label="transfer">转办</el-checkbox>
      <el-checkbox label="return">退回</el-checkbox>
      <el-checkbox label="reject">驳回</el-checkbox>
      <el-checkbox label="refuse">拒绝</el-checkbox>
    </el-checkbox-group>
  </div>
</template>

<script>

let userTaskForm = {
  buttonList: ''
}

export default {
  name: "ElementButton",
  props: {
    id: String,
    type: String
  },
  data() {
    return {
      buttonList: [],
    }
  },
  watch: {
    id: {
      immediate: true,
      handler() {
        this.bpmnElement = window.bpmnInstances.bpmnElement;
        this.$nextTick(() => this.resetTaskForm());
      }
    },
  },
  methods: {
    resetTaskForm() {
      // this.buttonList = ['cc', 'approval', 'adopt', 'delegate', 'refuse', 'addSign', 'jump', 'transfer', 'return', 'reject']
      this.buttonList = []
      const businessObject = this.bpmnElement?.businessObject;
      if (!businessObject) {
        return;
      }
      if (businessObject['buttonList']) {
        this.buttonList = businessObject['buttonList'].split(',');
      }
    },
    buttonListChange() {
      userTaskForm.buttonList = this.buttonList.join() || null
      this.updateElementTask()
    },
    /**
     * 更新节点数据
     */
    updateElementTask() {
      const taskAttr = Object.create(null);
      for (let key in userTaskForm) {
        taskAttr[key] = userTaskForm[key];
      }
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, taskAttr);
    },
  }
}
</script>

<style scoped>

</style>
