<template>
  <div class="panel-tab__content">
    <el-form size="mini" label-width="90px" @submit.native.prevent :model="elementBaseInfo" ref="ruleForm">
      <div v-if="elementBaseInfo.$type === 'bpmn:Process'">
        <el-form-item label="流程标识" prop="id" :rules="{ required: true,message: '请输入流程标识', trigger: 'blur' }">
          <el-input
            v-model="elementBaseInfo.id"
            placeholder="请输入流程标识"
            clearable
            @change="updateBaseInfo('id')"
            disabled
          />
        </el-form-item>
        <el-form-item label="流程名称" prop="name" v-if="elementBaseInfo.$type === 'bpmn:Process'"
                      :rules="{ required: true,message: '请输入流程名称', trigger: 'blur' }">
          <el-input v-model="elementBaseInfo.name"
                    placeholder="请输入流程名称" clearable @change="updateBaseInfo('name')"/>
        </el-form-item>
        <!--流程的基础属性-->
        <template>
          <el-form-item label="版本标签">
            <el-input v-model="elementBaseInfo.versionTag" placeholder="请输入版本标签" clearable
                      @change="updateBaseInfo('versionTag')"/>
          </el-form-item>
          <el-form-item label="可执行">
            <el-switch v-model="elementBaseInfo.isExecutable" active-text="是" inactive-text="否"
                       @change="updateBaseInfo('isExecutable')"/>
          </el-form-item>
        </template>
      </div>

      <div v-else>
        <el-form-item label="ID" prop="id" :rules="{ required: true,message: '请输入ID', trigger: 'blur' }">
          <el-input
            v-model="elementBaseInfo.id"
            placeholder="请输入ID"
            :disabled="idEditDisabled"
            clearable
            @change="updateBaseInfo('id')"
          />
        </el-form-item>
        <el-form-item label="名称" prop="name" :rules="{ required: true,message: '请输入名称', trigger: 'blur' }">
          <el-input v-model="elementBaseInfo.name"
                    :disabled="idEditDisabled"
                    placeholder="请输入名称" clearable @change="updateBaseInfo('name')"/>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>
<script>

export default {
  name: "ElementBaseInfo",
  props: {
    businessObject: Object,
    type: String,
    idEditDisabled: {
      type: Boolean,
      default: true
    },
  },
  data() {
    return {
      elementBaseInfo: {},
    };
  },
  watch: {
    businessObject: {
      immediate: false,
      handler: function (val) {
        if (val) {
          this.$nextTick(() => this.resetBaseInfo());
        }
      }
    },
  },
  methods: {
    resetBaseInfo() {
      this.bpmnElement = window?.bpmnInstances?.bpmnElement;
      this.elementBaseInfo = JSON.parse(JSON.stringify(this.bpmnElement.businessObject));
      console.log(this.elementBaseInfo);
    },
    updateBaseInfo(key) {
      const attrObj = Object.create(null);
      attrObj[key] = this.elementBaseInfo[key];
      if (key === "id") {
        if (this.elementBaseInfo[key]) {
          window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
            id: this.elementBaseInfo[key],
            di: {id: `${this.elementBaseInfo[key]}_di`}
          });
        }
      } else {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, attrObj);
      }
    }
  },
  beforeDestroy() {
    this.bpmnElement = null;
  }
};
</script>
