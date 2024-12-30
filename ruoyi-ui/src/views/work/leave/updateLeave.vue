<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>发起【{{ processName }}】流程</span>
      </div>
      <el-col :span="24">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-row>
            <el-col :span="24">
              <el-form-item label="申请人" prop="userName">
                <el-input v-model="form.userName" show-word-limit :maxlength="100" placeholder="请输入申请人名称"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="开始时间" prop="startTime">
                <el-date-picker v-model="form.startTime"
                                :picker-options="time"
                                clearable
                                default-time="9:00:00"
                                placeholder="请选择开始时间"
                                type="datetime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="结束时间" prop="endTime">
                <el-date-picker v-model="form.endTime"
                                :picker-options="time"
                                clearable
                                default-time="18:00:00"
                                placeholder="请选择结束时间"
                                type="datetime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="24">
              <el-form-item label="请假类别" prop="category">
                <el-radio-group v-model="form.category">
                  <el-radio v-for="dict in dict.type.work_leave_category" :key="dict.value" :label="dict.value">
                    {{ dict.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="请假天数" prop="holiday">
                <el-input-number size="small" v-model="form.holiday" :step="0.5" :precision="1"
                                 :min="0.5"></el-input-number>
                /天
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位" prop="position">
                <el-input v-model="form.position" show-word-limit :maxlength="100" placeholder="请输入职位"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="是否补假" prop="isRepair">
                <el-radio v-for="dict in dict.type.common_whether" :key="dict.value" v-model="form.isRepair"
                          :label="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="24">
              <el-form-item label="请假内容" prop="content">
                <el-input v-model="form.content" show-word-limit :maxlength="255" :rows="4" placeholder="请输入请假内容"
                          type="textarea"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="24">
              <el-form-item label="备注" prop="remark">
                <el-input v-model="form.remark" show-word-limit :maxlength="255" :rows="4" placeholder="请输入备注"
                          type="textarea"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary"
                       v-if="form.schedule === 'terminated' || form.schedule === 'canceled' || form.schedule === 'unapproved'"
                       @click="submitForm(1)">提 交</el-button>
            <el-button type="primary" @click="submitForm(0)">保 存</el-button>
            <el-button @click="cancel">取 消</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-card>

    <el-collapse v-if="!disable">
      <el-collapse-item title="流程图">
        <el-card class="box-card">
          <process-viewer :key="`designer-${definitionId}`" :xml="xmlData" :style="{height: '700px'}"/>
        </el-card>
      </el-collapse-item>
    </el-collapse>

  </div>
</template>

<script>
import {getBpmnXml} from '@/api/workflow/process'
import Parser from '@/utils/generator/parser'
import ProcessViewer from '@/components/ProcessViewer'
import {getLeave, updateLeave} from "@/api/work/leave";

export default {
  name: 'UpdateLeave',
  dicts: ['work_leave_category', 'common_whether'],
  components: {
    Parser,
    ProcessViewer
  },
  data() {
    return {
      definitionId: null,
      disable: false,
      processName: null,
      xmlData: null,

      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userName: [
          {required: true, message: '请输入申请人', trigger: 'blur'}
        ],
        position: [
          {required: true, message: '职位不能为空', trigger: 'blur'}
        ],
        startTime: [
          {required: true, message: '开始时间不能为空', trigger: 'blur'}
        ],
        endTime: [
          {required: true, message: '结束时间不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '请假内容不能为空', trigger: 'blur'}
        ],
        holiday: [
          {required: true, message: '请假天数不能为空', trigger: 'blur'},
        ]
      },

      time: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: '明天',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() + 3600 * 1000 * 24)
            picker.$emit('pick', date)
          }
        }, {
          text: '后天',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() + 3600 * 1000 * 24 * 2)
            picker.$emit('pick', date)
          }
        }, {
          text: '一个星期',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() + 3600 * 1000 * 24 * 7)
            picker.$emit('pick', date)
          }
        }]
      },

      userData: {}
    }
  },
  created() {
    this.definitionId = this.$route.query && this.$route.query.definitionId;
    this.processName = this.$route.query && this.$route.query.processName;
    let businessId = this.$route.query && this.$route.query.businessId;
    if (!businessId) {
      this.$message.error('leaveId不能为空');
      return
    }

    if (!this.definitionId) {
      this.$message.error('definitionId不能为空');
      return
    }

    if (!this.definitionId) {
      this.$message.error('processName不能为空');
      return
    }

    this.reset();
    getLeave(businessId).then(response => {
      this.form = response.data
      this.initData();
    })
  },
  methods: {
    initData() {
      // 发送请求，获取xml
      getBpmnXml(this.definitionId).then(response => {
        this.xmlData = response.data
      })
    },
    // 表单重置
    reset() {
      this.form = {
        leaveId: null,
        userId: null,
        userName: null,
        category: '1',
        isRepair: '0',
        holiday: 0.5,
        startTime: null,
        endTime: null,
        position: null,
        content: null,
        remark: null,
        schedule: null,
        createTime: null,
        updateTime: null,
      }
      this.resetForm('form')
    },
    // 取消按钮
    cancel() {
      const obj = {path: "/work/leave"};
      this.$tab.closeOpenPage(obj);
    },
    /** 提交按钮 */
    submitForm(index) {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.disable) {
            return
          }
          if (index === 0) {
            this.form.schedule = 'unapproved'
          } else {
            this.form.schedule = 'running'
          }
          this.form.definitionId = this.definitionId
          this.form.processName = this.processName
          updateLeave(this.form).then(response => {
            this.$modal.msgSuccess(`${this.form.schedule === 'unapproved' ? '保存' : '提交'}成功`)
            this.cancel()
          })
        }
      })
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
