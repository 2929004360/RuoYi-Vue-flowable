<template>
  <div class="app-container">
    <el-collapse v-model="activeNames">
      <el-collapse-item title="请假详情" name="1">
        <Details style="padding: 0 !important;"
                 :detailsData="detailsData"
                 :isReturn="true"
                 returnUrl="/work/leave"
                 :row="detailRow"/>
      </el-collapse-item>

      <el-collapse-item title="流转记录" v-if="taskForm.procInsId" name="2">
        <el-card class="box-card" shadow="never">
          <el-col :span="20" :offset="2">
            <div class="block">
              <el-timeline>
                <el-timeline-item v-for="(item,index) in historyProcNodeList" :key="index" :icon="setIcon(item.endTime)"
                                  :color="setColor(item.endTime)">
                  <p style="font-weight: 700">{{ item.activityName }}</p>
                  <el-card v-if="item.activityType === 'startEvent'" class="box-card" shadow="hover">
                    【{{ item.assigneeName }}】 在 {{ item.createTime }} 发起流程
                  </el-card>
                  <el-card v-if="item.activityType === 'userTask'" class="box-card" shadow="hover">
                    <el-descriptions :column="5" :labelStyle="{'font-weight': 'bold'}">
                      <el-descriptions-item label="实际办理">{{ item.assigneeName || '-' }}</el-descriptions-item>
                      <el-descriptions-item label="候选办理">{{ item.candidate || '-' }}</el-descriptions-item>
                      <el-descriptions-item label="接收时间">{{ item.createTime || '-' }}</el-descriptions-item>
                      <el-descriptions-item label="办结时间">{{ item.endTime || '-' }}</el-descriptions-item>
                      <el-descriptions-item label="耗时">{{ item.duration || '-' }}</el-descriptions-item>
                    </el-descriptions>
                    <div v-if="item.commentList && item.commentList.length > 0">
                      <div v-for="(comment, index) in item.commentList" :key="index">
                        <el-divider content-position="left">
                          <el-tag :type="approveTypeTag(comment.type)" size="mini">{{
                              commentType(comment.type)
                            }}
                          </el-tag>
                          <el-tag type="info" effect="plain" size="mini">
                            {{ $moment(comment.time).format('YYYY-MM-DD HH:mm:ss') }}
                          </el-tag>
                        </el-divider>
                        <span>{{ comment.fullMessage }}</span>
                      </div>
                    </div>
                  </el-card>
                  <el-card v-if="item.activityType === 'endEvent'" class="box-card" shadow="hover">
                    {{ item.createTime }} 结束流程
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-col>
        </el-card>
      </el-collapse-item>

      <el-collapse-item title="历史流转记录" v-if="taskForm.procInsId && historyApproveProcNodeList.length > 0"
                        name="6">
        <el-card class="box-card" shadow="never">
          <el-col :span="20" :offset="2">
            <el-collapse>
              <el-collapse-item class="block" v-for="(it,i) in historyApproveProcNodeList" :key="i"
                                :title="`第【${i + 1}】次发起流程的记录`">
                <el-timeline>
                  <el-timeline-item v-for="(item,index) in it.data" :key="index" :icon="setIcon(item.endTime)"
                                    :color="setColor(item.endTime)">
                    <p style="font-weight: 700">{{ item.activityName }}</p>
                    <el-card v-if="item.activityType === 'startEvent'" class="box-card" shadow="hover">
                      【{{ item.assigneeName }}】 在 {{ item.createTime }} 发起流程
                    </el-card>
                    <el-card v-if="item.activityType === 'userTask'" class="box-card" shadow="hover">
                      <el-descriptions :column="5" :labelStyle="{'font-weight': 'bold'}">
                        <el-descriptions-item label="实际办理">{{ item.assigneeName || '-' }}</el-descriptions-item>
                        <el-descriptions-item label="候选办理">{{ item.candidate || '-' }}</el-descriptions-item>
                        <el-descriptions-item label="接收时间">{{ item.createTime || '-' }}</el-descriptions-item>
                        <el-descriptions-item label="办结时间">{{ item.endTime || '-' }}</el-descriptions-item>
                        <el-descriptions-item label="耗时">{{ item.duration || '-' }}</el-descriptions-item>
                      </el-descriptions>
                      <div v-if="item.commentList && item.commentList.length > 0">
                        <div v-for="(comment, index) in item.commentList" :key="index">
                          <el-divider content-position="left">
                            <el-tag :type="approveTypeTag(comment.type)" size="mini">{{
                                commentType(comment.type)
                              }}
                            </el-tag>
                            <el-tag type="info" effect="plain" size="mini">
                              {{ $moment(comment.time).format('YYYY-MM-DD HH:mm:ss') }}
                            </el-tag>
                          </el-divider>
                          <span>{{ comment.fullMessage }}</span>
                        </div>
                      </div>
                    </el-card>
                    <el-card v-if="item.activityType === 'endEvent'" class="box-card" shadow="hover">
                      {{ item.createTime }} 结束流程
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
              </el-collapse-item>
            </el-collapse>
          </el-col>
        </el-card>
      </el-collapse-item>

      <el-collapse-item title="流程跟踪" v-if="taskForm.procInsId" name="3">
        <process-viewer :key="`designer-${loadIndex}`" :style="'height:' + height" :xml="xmlData"
                        :finishedInfo="finishedInfo" :allCommentList="historyProcNodeList"
        />
      </el-collapse-item>

      <el-collapse-item title="流程图" v-if="!taskForm.procInsId && definitionId && xmlData" name="4">
        <process-viewer :key="`designer-${definitionId}`" :xml="xmlData" :style="{height: '700px'}"/>
      </el-collapse-item>

      <el-collapse-item title="任务办理" v-if="processed === true" name="5">
        <div v-if="taskFormOpen">
          <div class="clearfix">
            <span>填写表单</span>
          </div>
          <el-col :span="20" :offset="2">
            <parser :form-conf="taskFormData" ref="taskFormParser"/>
          </el-col>
        </div>
        <div>
          <div class="clearfix">
            <span>审批流程</span>
          </div>
          <el-row>
            <el-col :span="20" :offset="2">
              <el-form ref="taskForm" :model="taskForm" :rules="rules" label-width="120px">
                <el-form-item label="流程名称" prop="procDefName">
                  {{ procDefName }}
                </el-form-item>
                <el-form-item label="流程发起人">
                <span v-if="userData.user && userData.user.dept">
                   {{ userData.user.dept.deptName }}/{{ userData.user.nickName }}
                </span>
                </el-form-item>
                <el-form-item label="审批意见" prop="comment">
                  <el-input type="textarea" :rows="5" v-model="taskForm.comment" placeholder="请输入审批意见"/>
                </el-form-item>
                <el-form-item label="抄送人" prop="copyUserIds" v-if="buttonList.includes('cc')">
                  <el-tag
                    :key="index"
                    v-for="(item, index) in copyUser"
                    closable
                    :disable-transitions="false"
                    @close="handleClose('copy', index)">
                    {{ item.nickName }}
                  </el-tag>
                  <el-button class="button-new-tag" type="primary" icon="el-icon-plus" size="mini" circle
                             @click="onSelectCopyUsers"/>
                </el-form-item>
                <el-form-item label="指定审批人" prop="copyUserIds" v-if="buttonList.includes('approval')">
                  <el-tag
                    :key="index"
                    v-for="(item, index) in nextUser"
                    closable
                    :disable-transitions="false"
                    @close="handleClose('next', index)">
                    {{ item.nickName }}
                  </el-tag>
                  <el-button class="button-new-tag" type="primary" icon="el-icon-plus" size="mini" circle
                             @click="onSelectNextUsers"/>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
          <el-row :gutter="10" type="flex" justify="center">
            <el-col :span="1.5" v-if="buttonList.includes('adopt')">
              <el-button icon="el-icon-circle-check" type="success" @click="handleComplete">通过</el-button>
            </el-col>
            <el-col :span="1.5" v-if="buttonList.includes('delegate')">
              <el-button icon="el-icon-chat-line-square" type="primary" @click="handleDelegate">委派</el-button>
            </el-col>
            <el-col :span="1.5" v-if="buttonList.includes('addSign')">
              <el-button icon="el-icon-plus" type="primary" @click="handleAddSign">加签</el-button>
            </el-col>
            <el-col :span="1.5" v-if="buttonList.includes('jump')">
              <el-button icon="el-icon-circle-check" type="success" @click="handleJump">跳转</el-button>
            </el-col>
            <el-col :span="1.5" v-if="buttonList.includes('transfer')">
              <el-button icon="el-icon-thumb" type="success" @click="handleTransfer">转办</el-button>
            </el-col>
            <el-col :span="1.5" v-if="buttonList.includes('return')">
              <el-button icon="el-icon-refresh-left" type="warning" @click="handleReturn">退回</el-button>
            </el-col>
            <el-col :span="1.5" v-if="buttonList.includes('reject')">
              <el-button icon="el-icon-circle-close" type="danger" @click="handleReject">驳回</el-button>
            </el-col>
            <el-col :span="1.5" v-if="buttonList.includes('refuse')">
              <el-button icon="el-icon-circle-close" type="danger" @click="handleRefuse">拒绝</el-button>
            </el-col>
          </el-row>
        </div>
      </el-collapse-item>
    </el-collapse>


    <!--退回流程-->
    <el-dialog :title="returnTitle" :visible.sync="returnOpen" width="40%" append-to-body>
      <el-link type="primary" :underline="false">
        <span>退回：将一个任务或流程实例中的某个任务从当前处理者手中取回，并重新分配给之前的处理者或其他指定参与者处理，通常用于纠正错误、重新评估或修改任务处理</span>
      </el-link>
      <el-form style="margin-top: 10px" ref="taskForm" :model="taskForm" label-width="80px">
        <el-form-item v-if="returnTaskList && returnTaskList.length > 0" label="退回节点" prop="targetKey">
          <el-radio-group v-model="taskForm.targetKey">
            <el-radio-button
              v-for="item in returnTaskList"
              :key="item.id"
              :label="item.id"
            >{{ item.name }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <div v-else>
          无退回节点
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="returnOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitReturn">确 定</el-button>
      </span>
    </el-dialog>

    <!--加签流程-->
    <el-dialog :title="addSignTitle" @cancel="addSignOpen = false" :visible.sync="addSignOpen" width="40%"
               append-to-body>
      <el-link type="primary" :underline="false">
        <span>向前加签：任务在 A 这里，A 这个时候需要 B 核对一下，等 B 核对之后又回到 A 这里，这时 A 才能继续自己的任务</span>
      </el-link>
      <el-link style="margin-top: 10px" type="primary" :underline="false">
        <span>向后加签：任务在 A 这里，A 这个时候需要 B 处理这个事情，处理完毕之后就不用管了，继续后面的审批环节</span>
      </el-link>
      <el-link style="margin-top: 10px" type="primary" :underline="false">
        <span>多实例加签：将任务动态分配给多个参与者处理，每个参与者独立完成其分配的任务实例，适用于需要多人协作的场</span>
      </el-link>
      <el-form style="margin-top: 10px" ref="addSignForm" :model="addSignForm" label-width="160px">
        <el-form-item label="加签类型" prop="addSignType"
                      :rules="[{ required: true, message: '请选择加签类型', trigger: 'blur' }]">
          <el-radio-group v-model="addSignForm.addSignType" @change="changeAddSignType">
            <el-radio :label="0">前加签</el-radio>
            <el-radio :label="1">后加签</el-radio>
            <el-radio :label="2">多实例加签</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="加签人员" prop="copyUserIds"
                      :rules="[{ required: true, message: '请选择用户', trigger: 'blur' }]">
          <el-tag
            :key="index"
            v-for="(item, index) in addSignUser"
            closable
            :disable-transitions="false"
            @close="handleClose('addSign', index)">
            {{ item.nickName }}
          </el-tag>
          <el-button class="button-new-tag" type="primary" icon="el-icon-plus" size="mini" circle
                     @click="onSelectAddSignUsers"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addSignOpen = false">取 消</el-button>
        <el-button type="primary" @click="addSignComplete()">确 定</el-button>
      </span>
    </el-dialog>

    <!--跳转流程-->
    <el-dialog :title="jumpTitle" @cancel="jumpOpen = false" :visible.sync="jumpOpen" width="40%" append-to-body>
      <el-form ref="jumpForm" :model="jumpForm" label-width="80px">
        <el-form-item label="跳转节点" prop="jumpType"
                      :rules="[{ required: true, message: '请选择跳转节点', trigger: 'blur' }]">
          <el-radio-group v-model="jumpForm.targetKey">
            <el-radio-button
              v-for="item in jumpNodeData"
              :key="item.id"
              :label="item.id"
            >{{ item.name }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="jumpOpen = false">取 消</el-button>
        <el-button type="primary" @click="jumpComplete(true)">确 定</el-button>
      </span>
    </el-dialog>

    <SelectUser ref="SelectUser" v-model="selectUserList" :allowCancel="true" :isWhole="false"
                :selectType="selectType" @determine="determineUser" :text="selectUserText"></SelectUser>
  </div>
</template>

<script>
import {detailProcess, getBpmnXml} from '@/api/workflow/process'
import Parser from '@/utils/generator/parser'
import {
  addSignTask,
  complete,
  delegate,
  jumpTask,
  multiInstanceAddSignTask,
  refuseTask,
  rejectTask,
  returnList,
  returnTask,
  transfer,
  userTaskList,
} from '@/api/workflow/task'
import ProcessViewer from '@/components/ProcessViewer'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import Treeselect from '@riophae/vue-treeselect'
import {getInfoUser} from "@/api/system/user";
import {getLeave} from "@/api/work/leave";

export default {
  name: "ViewLeave",
  dicts: ['work_leave_category', 'common_schedule', 'common_whether'],
  components: {
    ProcessViewer,
    Parser,
    Treeselect
  },
  props: {
    procInsId: {
      type: String,
    },
    formType: {
      type: String,
    },
    id: {
      type: String,
    },
    dId: {
      type: String,
    }
  },
  computed: {
    commentType() {
      return val => {
        switch (val) {
          case '1':
            return '通过'
          case '2':
            return '退回'
          case '3':
            return '驳回'
          case '4':
            return '委派'
          case '5':
            return '转办'
          case '6':
            return '终止'
          case '7':
            return '撤回'
          case '8':
            return '拒绝'
          case '9':
            return '跳过'
          case '10':
            return '前加签'
          case '11':
            return '后加签'
          case '12':
            return '多实例加签'
          case '13':
            return '跳转'
          case '14':
            return '收回'
        }
      }
    },
    approveTypeTag() {
      return val => {
        switch (val) {
          case '1':
            return 'success'
          case '2':
            return 'warning'
          case '3':
            return 'danger'
          case '4':
            return 'primary'
          case '5':
            return 'success'
          case '6':
            return 'danger'
          case '7':
            return 'info'
        }
      }
    }
  },
  data() {
    return {
      activeNames: ['1', '2', '4', '5'],

      height: document.documentElement.clientHeight - 205 + 'px;',
      // 模型xml数据
      loadIndex: 0,
      xmlData: undefined,
      finishedInfo: {
        finishedSequenceFlowSet: [],
        finishedTaskSet: [],
        unfinishedTaskSet: [],
        rejectedTaskSet: []
      },
      historyProcNodeList: [],
      historyApproveProcNodeList: [],

      // 遮罩层
      loading: true,
      taskForm: {
        comment: "", // 意见内容
        procInsId: "", // 流程实例编号
        taskId: "",// 流程任务编号
        copyUserIds: "", // 抄送人Id
        vars: "",
        targetKey: "",
        formType: "",
        parentTaskId: "",
      },
      rules: {
        comment: [{required: true, message: '请输入审批意见', trigger: 'blur'}],
      },

      variables: [], // 流程变量数据
      taskFormOpen: false,
      buttonList: [], // 显示按钮组
      taskFormData: {}, // 流程变量数据
      returnTaskList: [],  // 回退列表数据
      processed: false,
      procDefName: null,
      returnTitle: null,
      returnOpen: false,
      rejectOpen: false,
      rejectTitle: null,
      nextUser: [],
      userMultipleSelection: [],
      userDialogTitle: '',
      userOpen: false,

      addSignUser: [],
      addSignOpen: false, //前加签
      addSignTitle: null,  //加签标题
      addSignForm: {
        multiple: true,
        comment: "", // 意见内容
        commentFileDto: { //意见里的附件
          type: '',
          fileurl: ''
        },
        procInsId: "", // 流程实例编号
        taskId: "", // 流程任务编号
        vars: "",
        targetKey: "",
        addSignUsers: "", //委托加签人员
        addSignType: 0, //加签类型
      },

      jumpOpen: false, //跳转
      jumpTitle: null,  //跳转标题
      jumpForm: {
        multiple: true,
        comment: "", // 意见内容
        commentFileDto: { //意见里的附件
          type: '',
          fileurl: ''
        },
        procInsId: "", // 流程实例编号
        taskId: "", // 流程任务编号
        vars: "",
        targetKey: "",
        jumpNode: "", //跳转节点
      },
      jumpNodeData: [],

      selectUserList: [],
      copyUser: [],

      userData: {
        type: '',
        user: {},
      },

      selectType: 'checkbox',

      detailsData: [
        {
          label: '请假编号',
          prop: 'leaveId',
          value: '',
          isShow: true
        }, {
          label: '申请人',
          prop: 'userName',
          value: '',
          isShow: true
        }, {
          label: '请假类别',
          prop: 'category',
          value: '',
          type: 'dictTag',
          dictionary: 'work_leave_category',
          isShow: true
        }, {
          label: '请假天数',
          prop: 'holiday',
          value: '',
          isShow: true
        }, {
          label: '职位',
          prop: 'position',
          value: '',
          isShow: true
        }, {
          label: '开始时间',
          prop: 'startTime',
          isShow: true,
          type: 'dateType'
        }, {
          label: '结束时间',
          prop: 'endTime',
          isShow: true,
          type: 'dateType'
        },{
          label: '请假内容',
          prop: 'content',
          value: '',
          isShow: true
        }, {
          label: '是否补假',
          prop: 'isRepair',
          value: '',
          isShow: true,
          type: 'dictTag',
          dictionary: 'common_whether'
        }, {
          label: '审批进度',
          prop: 'schedule',
          type: 'dictTag',
          isShow: true,
          value: '',
          dictionary: 'common_schedule'
        },
        {
          label: '备注',
          prop: 'remark',
          value: '',
          isShow: true
        }, {
          label: '创建时间',
          prop: 'createTime',
          isShow: true,
          type: 'dateType'
        }, {
          label: '更新时间',
          prop: 'updateTime',
          isShow: true,
          type: 'dateType'
        }
      ],
      detailRow: {},

      businessId: null,
      definitionId: null,

      selectUserText: ''
    };
  },
  created() {
    this.initData();
  },
  methods: {
    getBusinessProcess(data) {
      this.detailsData.forEach(item => {
        item.value = data[item.prop]

        const map = new Map(Object.entries(this.dict.type))
        if (item.dictionary) {
          item.dictionaryList = map.get(item.dictionary)
        }
      })

      this.detailRow = data
    },
    // 选择人员回调
    determineUser(e) {
      let type = this.userData.type

      if (type === 'copy' || type === 'next' || type === 'addSign') {
        let userIds = e.map(k => k.userId);
        if (type === 'copy') {
          this.taskForm.copyUserIds = userIds instanceof Array ? userIds.join(',') : userIds;

          this.copyUser = e.map((item) => {
            return {
              userId: item.userId,
              nickName: item.nickName,
            }
          })
        } else if (type === 'next') {
          this.nextUser = e.map((item) => {
            return {
              userId: item.userId,
              nickName: item.nickName,
            }
          })
          this.taskForm.nextUserIds = userIds instanceof Array ? userIds.join(',') : userIds;
        } else if (type === 'addSign') {
          this.addSignUser = e.map((item) => {
            return {
              userId: item.userId,
              nickName: item.nickName,
            }
          })
          this.addSignForm.addSignUsers = userIds instanceof Array ? userIds.join(',') : userIds;
        }
      } else {
        if (!this.taskForm.comment) {
          this.$modal.msgError("请输入审批意见");
          return;
        }
        this.taskForm.userId = e.userId;
        if (type === 'delegate') {
          delegate(this.taskForm).then(res => {
            this.$modal.msgSuccess("任务委派成功");
            this.goBack();
          });
        }
        if (type === 'transfer') {
          transfer(this.taskForm).then(res => {
            this.$modal.msgSuccess("任务转办成功");
            this.goBack();
          });
        }
      }
    },
    onSelectCopyUsers() {
      this.selectType = 'checkbox'
      this.$refs.SelectUser.selectUser()
      if (this.copyUser) {
        this.selectUserList = this.copyUser.map((item) => {
          return {
            userId: item.userId,
            nickName: item.nickName,
          }
        })
      }

      this.userData.type = 'copy';
    },
    onSelectNextUsers() {
      this.selectType = 'checkbox'
      this.$refs.SelectUser.selectUser()
      if (this.nextUser) {
        this.selectUserList = this.nextUser.map((item) => {
          return {
            userId: item.userId,
            nickName: item.nickName,
          }
        })
      }

      this.userData.type = 'next';
    },
    onSelectAddSignUsers() {
      this.selectType = 'checkbox'
      this.$refs.SelectUser.selectUser()
      if (this.addSignUser) {
        this.selectUserList = this.addSignUser.map((item) => {
          return {
            userId: item.userId,
            nickName: item.nickName,
          }
        })
      }

      this.userData.type = 'addSign';
    },
    // 关闭标签
    handleClose(type, index) {
      if (type === 'copy') {
        // 设置抄送人ID
        if (this.copyUser && this.copyUser.length > 0) {
          this.copyUser.splice(index, 1)
          let userIds = this.copyUser.map(k => k.userId);
          this.taskForm.copyUserIds = userIds instanceof Array ? userIds.join(',') : userIds;
        } else {
          this.taskForm.copyUserIds = '';
        }
      } else if (type === 'next') {
        // 设置审批人ID
        if (this.nextUser && this.nextUser.length > 0) {
          this.nextUser.splice(index, 1)
          let userIds = this.nextUser.map(k => k.userId);
          this.taskForm.nextUserIds = userIds instanceof Array ? userIds.join(',') : userIds;
        } else {
          this.taskForm.nextUserIds = '';
        }
      } else if (type === 'addSign') {
        // 设置加签人ID
        if (this.addSignUser && this.addSignUser.length > 0) {
          this.addSignUser.splice(index, 1)
          let userIds = this.addSignUser.map(k => k.userId);
          this.addSignForm.addSignUsers = userIds instanceof Array ? userIds.join(',') : userIds;
        } else {
          this.addSignForm.addSignUsers = '';
        }
      }
    },
    /** 通过任务 */
    handleComplete() {
      // 校验表单
      const taskFormRef = this.$refs.taskFormParser;
      const isExistTaskForm = taskFormRef !== undefined;

      // 若无任务表单，则 taskFormPromise 为 true，即不需要校验
      const taskFormPromise = !isExistTaskForm ? true : new Promise((resolve, reject) => {
        taskFormRef.$refs[taskFormRef.formConfCopy.formRef].validate(valid => {
          valid ? resolve() : reject()
        })
      });
      const approvalPromise = new Promise((resolve, reject) => {
        this.$refs['taskForm'].validate(valid => {
          valid ? resolve() : reject()
        })
      });


      Promise.all([taskFormPromise, approvalPromise]).then(() => {
        if (isExistTaskForm) {
          this.taskForm.variables = taskFormRef[taskFormRef.formConfCopy.formModel]
        }
        this.$confirm('确定审批通过吗?', '温馨提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          complete(this.taskForm).then(response => {
            this.$modal.msgSuccess("审批成功");
            this.goBack();
          });
        }).catch(() => {

        });
      })
    },

    initData() {
      if (this.formType) {
        this.taskForm.formType = this.formType
      } else {
        this.taskForm.formType = this.$route.query && this.$route.query.formType;
      }

      if (this.procInsId) {
        this.taskForm.procInsId = this.procInsId
      } else {
        this.taskForm.procInsId = this.$route.query && this.$route.query.procInsId;
      }

      if (this.id) {
        this.businessId = this.id
      } else {
        this.businessId = this.$route.query && this.$route.query.businessId
      }

      if (this.dId) {
        this.definitionId = this.dId
      } else {
        this.definitionId = this.$route.query && this.$route.query.definitionId
      }

      this.taskForm.taskId = this.$route.query && this.$route.query.taskId;
      this.taskForm.parentTaskId = this.$route.query && this.$route.query.parentTaskId;
      this.processed = this.$route.query && eval(this.$route.query.processed || false);
      this.procDefName = this.$route.query && this.$route.query.procDefName

      if (this.taskForm.procInsId) {
        // 流程任务重获取变量表单
        this.getProcessDetails(this.taskForm.procInsId, this.taskForm.taskId, this.taskForm.formType);
        this.loadIndex = this.taskForm.procInsId;
      } else if (this.businessId) {
        getLeave(this.businessId).then(response => {
          let data = response.data
          this.detailsData.forEach(item => {
            item.value = data[item.prop]

            const map = new Map(Object.entries(this.dict.type))
            if (item.dictionary) {
              item.dictionaryList = map.get(item.dictionary)
            }
          })

          this.detailRow = data
        })

        if (this.definitionId) {
          // 发送请求，获取xml
          getBpmnXml(this.definitionId).then(response => {
            this.xmlData = response.data
          })
        }
      } else {
        this.$modal.msgError("参数错误");
      }
    },
    setIcon(val) {
      if (val) {
        return "el-icon-check";
      } else {
        return "el-icon-time";
      }
    },
    setColor(val) {
      if (val) {
        return "#2bc418";
      } else {
        return "#b3bdbb";
      }
    },
    /** 流程变量赋值 */
    handleCheckChange(val) {
      if (val instanceof Array) {
        this.taskForm.values = {
          "approval": val.join(',')
        }
      } else {
        this.taskForm.values = {
          "approval": val
        }
      }
    },
    getProcessDetails(procInsId, taskId, formType) {
      const params = {procInsId: procInsId, taskId: taskId, formType: formType};
      detailProcess(params).then(res => {
        const data = res.data;
        this.getBusinessProcess(data.businessProcess)
        this.xmlData = data.bpmnXml;
        this.taskFormOpen = data.existTaskForm;
        this.buttonList = data.buttonList;
        if (this.taskFormOpen) {
          this.taskFormData = data.taskFormData;
        }
        this.historyProcNodeList = data.historyProcNodeList;
        this.historyProcNodeList.forEach((item) => {
          if (item.activityType === 'startEvent') {
            getInfoUser(item.assigneeId).then((res) => {
              this.userData.user = res.data
            })
          }
        })

        if (data.historyApproveProcNodeList && data.historyApproveProcNodeList.length > 0) {
          this.historyApproveProcNodeList = data.historyApproveProcNodeList;
          this.historyApproveProcNodeList.forEach((item) => {
            item.data = JSON.parse(item.data)
          })
        }
        this.finishedInfo = data.flowViewer;
      })
    },
    /** 委派任务 */
    handleDelegate() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          this.selectUserText = '委派：是将任务节点分给其他人处理，等其他人处理好之后，委派任务还会 自动回到委派人的任务中'
          this.$refs.SelectUser.selectUser()
          this.userData.type = 'delegate';
          this.selectType = 'radio'
        }
      })
    },
    /**
     * 加签按钮
     */
    handleAddSign() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          this.addSignOpen = true;
          this.addSignTitle = "前加签流程";
        }
      })
    },
    /** 加签任务 */
    addSignComplete() {
      if (this.addSignUser.length === 0) {
        this.$message.error("请选择加签人员");
        return
      }
      // 流程信息
      this.addSignForm.taskId = this.$route.query && this.$route.query.taskId;
      this.addSignForm.procInsId = this.$route.query && this.$route.query.procInsId;

      //对formdesigner后续加签审批的时候需要用到
      this.addSignForm.comment = this.taskForm.comment;

      if (this.addSignForm.addSignType === 2) {
        multiInstanceAddSignTask(this.addSignForm).then(response => {
          this.$message.success("多实例加签成功");
          this.addSignOpen = false;
          this.goBack();
        });
      } else {
        addSignTask(this.addSignForm).then(response => {
          this.$message.success("加签成功");
          this.addSignOpen = false;
          this.goBack();
        });
      }
    },
    changeAddSignType(val) {
      this.addSignForm.addSignType = val;
      if (this.addSignForm.addSignType === 0) {
        this.addSignTitle = "前加签流程";
      }
      if (this.addSignForm.addSignType === 1) {
        this.addSignTitle = "后加签流程";
      }
      if (this.addSignForm.addSignType === 2) {
        this.addSignTitle = "多实例加签流程";
      }
    },
    /** 跳转任务 */
    handleJump() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          this.jumpOpen = true;
          this.jumpTitle = "跳转流程";
          userTaskList({taskId: this.taskForm.taskId}).then((res) => {
            this.jumpNodeData = res.data
          })
        }
      });
    },
    jumpComplete() {
      // 流程信息
      this.jumpForm.taskId = this.$route.query && this.$route.query.taskId;
      this.jumpForm.procInsId = this.$route.query && this.$route.query.procInsId;
      //对formdesigner后续加签审批的时候需要用到
      this.jumpForm.comment = this.taskForm.comment;
      //目标选择的节点信息
      this.jumpNodeData.forEach((item) => {
        if (this.jumpForm.targetKey === item.id) {
          this.jumpForm.targetActId = item.id;
          this.jumpForm.targetActName = item.name;
        }
      })
      console.log("this.jumpForm=", this.jumpForm);
      jumpTask(this.jumpForm).then(res => {
        this.$message.success('跳转成功')
        this.jumpOpen = false;
        this.goBack();
      });
    },
    /** 转办任务 */
    handleTransfer() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          this.selectUserText = '转办：将一个任务或流程实例中的某个任务，从一个参与者（如用户或角色）重新分配给另一个参与者进行处理，以便适应流程中的变化或处理需'
          this.$refs.SelectUser.selectUser()
          this.userData.type = 'transfer';
          this.selectType = 'radio'
        }
      })
    },
    /** 驳回任务 */
    handleReject() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          const _this = this;
          this.$modal.confirm('驳回：将一个任务或流程实例中的某个任务从当前处理者手中退回，并返回到上一步骤或指定步骤重新处理，通常用于审批流程中对结果不满意或需要重新审视的情况。驳回审批单流程，是否继续？').then(function () {
            return rejectTask(_this.taskForm);
          }).then(res => {
            this.$modal.msgSuccess("驳回成功");
            this.goBack();
          });
        }
      });
    },
    /** 拒绝任务 */
    handleRefuse() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          const _this = this;
          this.$modal.confirm('拒绝审批单流程会终止，是否继续？').then(function () {
            return refuseTask(_this.taskForm);
          }).then(res => {
            this.$modal.msgSuccess("拒绝成功");
            this.goBack();
          });
        }
      });
    },
    /** 返回页面 */
    goBack() {
      // 关闭当前标签页并返回上个页面
      this.$tab.closePage(this.$route)
      this.$router.back()
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
    /** 可退回任务列表 */
    handleReturn() {
      this.$refs['taskForm'].validate(valid => {
        if (valid) {
          this.returnTitle = "退回流程";
          returnList(this.taskForm).then(res => {
            this.returnTaskList = res.data;
            this.taskForm.values = null;
            this.returnOpen = true;
          })
        }
      });

    },
    /** 提交退回任务 */
    submitReturn() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          if (!this.taskForm.targetKey) {
            this.$modal.msgError("请选择退回节点！");
          }
          returnTask(this.taskForm).then(res => {
            this.$modal.msgSuccess("退回成功");
            this.goBack()
          });
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  width: 100%;
  margin-bottom: 20px;
}

.el-tag + .el-tag {
  margin-left: 10px;
}

.el-row {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }
}

.el-col {
  border-radius: 4px;
}

.button-new-tag {
  margin-left: 10px;
}
</style>
