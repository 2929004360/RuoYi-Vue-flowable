
<template>
  <!-- append-to-body="true" 是为了不被前面对话框遮挡 by nbacheng -->
  <el-drawer
    :append-to-body="true"
    size="550px"
    class="drawer"
    :visible.sync="visible"
    :show-close="false"
    style="text-align:left;"
    @close="cancel"
    v-if="properties"
  >
    <!-- 标题 -->
    <header slot="title" class="header"  v-if="value && value.type=='start' && properties.title">{{properties.title}}</header>
    <header slot="title" class="header" v-else>
      <span @click="titleInputVisible = true" v-show="!titleInputVisible" style="cursor:pointer;">
        {{properties.title}}
        <i class="el-icon-edit"></i>
      </span>
      <el-input
        size="mini"
        v-model="properties.title"
        v-show="titleInputVisible"
        v-clickoutside="_ => titleInputVisible=false"
        style="z-index:9;max-width: 200px;"
      ></el-input>
      <el-select
        v-if="isConditionNode()"
        v-model="properties.priority"
        size="mini"
        class="priority-select" >
        <el-option v-for="item in priorityLength" :key="item" :value="item-1" :label="'优先级'+item"></el-option>
      </el-select>
    </header>

    <!-- 条件  -->
    <section class="condition-pane" v-if="value && isConditionNode()">

      <!-- 字符串条件 -->
      <el-input placeholder="请输入条件表达式" v-model="pconditions">
          <template slot="prepend">条件表达式:</template>
        </el-input>

    </section>

    <!-- 延时处理 -->
    <section  v-if="value && isDelayNode()" style="padding-left: 1rem;">
      <div>
        <div style="margin-bottom: 20px">
          <p class="item-desc">延时方式</p>
          <el-radio-group v-model="value.properties.type" size="small">
            <el-radio-button label="FIXED">固定时长</el-radio-button>
            <el-radio-button label="AUTO">自动计算</el-radio-button>
          </el-radio-group>
        </div>
        <div v-if="value.properties.type === 'FIXED'">
          <el-input style="width: 180px;" placeholder="时间单位" size="small" type="number" v-model="value.properties.time">
            <el-select style="width: 75px;" v-model="value.properties.unit" slot="append" placeholder="请选择">
              <el-option label="天" value="D"></el-option>
              <el-option label="小时" value="H"></el-option>
              <el-option label="分钟" value="M"></el-option>
            </el-select>
          </el-input>
          <span class="item-desc"> 后进入下一步</span>
        </div>
        <div class="item-desc" v-else>
          <el-time-picker value-format="HH:mm:ss" style="width: 150px;" size="small" v-model="value.properties.dateTime" placeholder="任意时间点"></el-time-picker>
          <span class="item-desc"> 后进入下一步</span>
        </div>
      </div>
    </section>

    <!-- 审批人 -->
    <section class="approver-pane" style="height:100%;" v-if="value && (isApproverNode() || isStartNode())">
      <el-tabs v-model="activeName"  class="pane-tab">
        <el-tab-pane :label="'设置' + (value.type === 'approver' ? '审批人' : '发起人')" name="config">
          <!-- 开始节点 -->
          <el-row style="padding: 10px;"  v-if="value.type === 'start'">
            <el-col :span="4" style="font-size: 12px;">发起人</el-col>
            <el-col :span="18" style="padding-left: 12px;">
              <!--<fc-org-select ref="start-org" :tabList="['dep&user']" v-model="initiator" /> -->
            </el-col>
          </el-row>

          <div v-else-if="value.type === 'approver'">
            <div style="padding: 12px;">
              <el-radio-group v-model="approverForm.assigneeType" style="line-height: 32px;" @change="resetOrgColl">
                <el-radio v-for="item in assigneeTypeOptions" :label="item.value" :key="item.value" class="radio-item">{{item.label}}</el-radio>
              </el-radio-group>
            </div>
            <div style="border-bottom: 1px solid #e5e5e5;padding-bottom: 1rem;">
              <div v-if="approverForm.assigneeType === 'myself'"  class="option-box" style="color: #a5a5a5;">发起人自己将作为审批人处理审批单</div>
              <div v-else-if="approverForm.assigneeType === 'optional'"  class="option-box">
                <p>可选多人</p>
                <el-switch v-model="approverForm.optionalMultiUser" active-color="#13ce66"> </el-switch>
                <p>选择范围</p>
                <el-select v-model="approverForm.optionalRange" size="mini">
                  <el-option v-for="(item, index) in rangeOptions" :key="index" :label="item.label" :value="item.value"
                    :disabled="item.disabled"></el-option>
                </el-select>
              </div>
              <div v-else-if="approverForm.assigneeType === 'director'">
                <div style="font-size: 14px;padding-left: 1rem;">发起人的
                  <el-select v-model="directorLevel" size="small">
                    <el-option v-for="item in 5" :key="item" :label="item === 1 ? '直接主管': `第${item}级主管`" :value="item"
                    ></el-option>
                  </el-select>
                  <br>
                  <el-checkbox v-model="useDirectorProxy" style="margin-top: 1rem;">找不到主管时，由上级主管代审批</el-checkbox>
                </div>
              </div>
              <div v-else-if="approverForm.assigneeType === 'role'">
                <el-select v-model="roleIds" multiple size="mini" placeholder="请选择 角色" @change="changeSelectRoles">
                  <el-option
                    v-for="item in roleOptions"
                    :key="item.roleId"
                    :label="item.roleName"
                    :value="`ROLE${item.roleId}`"
                    :disabled="item.status === 1">
                  </el-option>
                </el-select>
              </div>
              <div v-else class="option-box">
                <div class="element-drawer__button">
                  <el-button size="mini" type="primary" icon="el-icon-plus" @click="onSelectUsers()">添加用户</el-button>
                </div>
                <el-tag v-for="userText in selectedUser.text" :key="userText" effect="plain">
                  {{userText}}
                </el-tag>
              </div>
            </div>
            <div class="option-box" style="border-bottom: 1px solid #e5e5e5;"
              v-if="orgCollection[approverForm.assigneeType]
              && orgCollection[approverForm.assigneeType].length > 1
              || ['optional'].includes(approverForm.assigneeType)
              || (['role'].includes(approverForm.assigneeType) && orgCollection[approverForm.assigneeType].length>=1)">
              <p>多人审批时采用的审批方式</p>
              <el-radio v-model="approverForm.counterSign" :label="true" class="radio-item">会签（须所有审批人同意）</el-radio>
              <br>
              <el-radio  v-model="approverForm.counterSign"  :label="false" class="radio-item">或签（一名审批人同意或拒绝即可）</el-radio>
            </div>
          </div>

        </el-tab-pane>
        <el-tab-pane label="表单选择" name="formSelect">
           <el-form size="mini" label-width="90px" @submit.native.prevent>
             <el-form-item label="表单" prop="formKey">
               <el-select v-model="approverForm.formKey" placeholder="请选择表单" @change="updateFormKey" clearable>
                 <el-option v-for="item in formOptions" :key="item.formId" :label="item.formName" :value="`key_${item.formId}`" />
               </el-select>
             </el-form-item>
             <el-form-item prop="localScope">
               <span slot="label">
                 <el-tooltip content="若为节点表单，则表单信息仅在此节点可用，默认为全局表单，表单信息在整个流程实例中可用" placement="top-start">
                   <i class="header-icon el-icon-info"></i>
                 </el-tooltip>
                 <span>节点表单</span>
               </span>
               <el-switch :disabled="value.type === 'start'" v-model="localScope" active-text="是" inactive-text="否" @change="updateFormScope()" />
             </el-form-item>
           </el-form>
        </el-tab-pane>
      </el-tabs>
    </section>

    <section  v-if="value && isCopyNode()" style="padding-left: 1rem;">
      <p>抄送人</p>
      <!--<fc-org-select ref="copy-org" v-model="properties.members" buttonType="button" title="抄送人" /> -->
      <br>
      <el-checkbox v-model="properties.userOptional">允许发起人自选抄送人</el-checkbox>
    </section>
    <div class="actions">
      <el-button size="small" @click="cancel">取消</el-button>
      <el-button size="small" type="primary" @click="confirm">确定</el-button>
    </div>
    <!-- 候选用户弹窗 -->
    <el-dialog title="候选用户" :visible.sync="userOpen" width="60%" append-to-body>
      <el-row type="flex" :gutter="20">
        <!--部门数据-->
        <el-col :span="7">
          <el-card shadow="never" style="height: 100%">
            <div slot="header">
              <span>部门列表</span>
            </div>
            <div class="head-container">
              <el-input
                v-model="deptName"
                placeholder="请输入部门名称"
                clearable
                size="small"
                prefix-icon="el-icon-search"
                style="margin-bottom: 20px"
              />
              <el-tree
                :data="deptOptions"
                :props="deptProps"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                ref="tree"
                default-expand-all
                @node-click="handleNodeClick"
              />
            </div>
          </el-card>
        </el-col>
        <el-col :span="17">
          <el-table ref="multipleTable" height="600" :data="userTableList" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="用户名" align="center" prop="nickName" />
            <el-table-column label="部门" align="center" prop="dept.deptName" />
          </el-table>
          <pagination
            :total="userTotal"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getUserList"
          />
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleTaskUserComplete">确 定</el-button>
        <el-button @click="userOpen = false">取 消</el-button>
      </div>
    </el-dialog>

  </el-drawer>
</template>
<script>
import { listUser, deptTreeSelect } from "@/api/system/user"
import { listRole } from "@/api/system/role"
import { listForm } from "@/api/workflow/form";
import Clickoutside from "element-ui/src/utils/clickoutside"
import { NodeUtils } from "../FlowCard/util.js"
import RowWrapper from './RowWrapper'
import NumInput from "./NumInput"
const rangeType = {
  'lt': '<',
  'lte':'≤',
  'gt':'>',
  'gte':'≥',
  'eq': '=',
}
const defaultApproverForm = {
  approvers:[], // 审批人集合
  assigneeType: "user", // 指定审批人
  formKey: "",  //挂载表单
  formOperates:[], // 表单操作权限集合
  counterSign: true, //是否为会签
  // 审批类型为自选 出现 optionalMultiUser optionalRange
  optionalMultiUser: false,
  optionalRange: 'ALL', // USER<最多十个> / ALL / ROLE
}
export default {
  components: {
    "num-input": NumInput,
    "row-wrapper": RowWrapper
  },
  props: [/*当前节点数据*/"value", /*整个节点数据*/"processData"],
  data() {
    return {
      fcOrgTabList:['dep', 'role', 'user', 'position'],
      visible: false,  // 控制面板显隐
      globalFormOperate: null,  // 统一设置节点表单权限
      titleInputVisible: false, // 是否显示标题输入框  startNode 不显示
      activeName: "config", // or formAuth  Tab面板key
      showingPCons: "{填写条件表达式}", // 用户选择了得条件  被选中的才会被展示在面板上编辑
      pconditions: "{填写条件表达式}", // 作为流程图条件
      dialogVisible: false, // 控制流程条件选项Dialog显隐
      // 当前节点数据
      properties: {},
      // 发起人  start节点和condition节点需要
      initiator:{
        'dep&user': []
      },
      priorityLength: 0, // 当为条件节点时  显示节点优先级选项的数据
      orgCollection:{
        dep: [],
        role: [],
        user: [],
        position: []
      },
      useDirectorProxy: true, // 找不到主管时 上级主管代理审批
      directorLevel: 1,  // 审批主管级别
      startForm:{
        formOperates: [],
        formKey: "",
      },
      approverForm: JSON.parse(JSON.stringify(defaultApproverForm)),

      optionalOptions: [
        {
          label: '自选一个人',
          value: false
        }, {
          label: '自选多个人',
          value: true
        }],
        rangeOptions: [
          {
          label: '全公司',
          value: 'ALL'
        }, {
          label: '指定成员',
          value: 'USER'
        }, {
          label: '角色',
          value: 'ROLE'
      }],

      assigneeTypeOptions:[
        {
          label:'指定成员',
          value: 'user'
        },
        {
          label:'主管',
          value: 'director'
        },
        {
          label:'角色',
          value: 'role'
        },
        {
          label:'发起人',
          value: 'myself'
        }],
      selectedUser: {
        ids: [],
        text: []
      },
      userOpen: false,
      deptName: undefined,
      deptOptions: [],
      deptProps: {
        children: "children",
        label: "label"
      },
      deptTempOptions: [],
      userTableList: [],
      userTotal: 0,
      selectedUserDate: [],
      roleOptions: [],
      roleIds: [],
      deptTreeData: [],
      deptIds: [],
      // 查询参数
      queryParams: {
        deptId: undefined
      },
      formOptions: [],
      formKey: "",
      localScope: false,
    };
  },
  computed: {

  },
  directives: {
    Clickoutside
  },
  watch: {
    visible(val) {
      if (!val) {
        this.approverForm = JSON.parse(JSON.stringify(defaultApproverForm)) // 重置数据为默认状态
        return
      }
      this.processData.properties.formOperates =
        this.initFormOperates(this.processData)
        .map(t=>({formId: t.formId, formOperate: t.formOperate}))
      this.isStartNode() && this.initStartNodeData()
      this.isApproverNode() && this.initApproverNodeData()
      this.isConditionNode() && this.initConditionNodeData()
    },

    value(newVal) {
      if (newVal && newVal.properties) {
        this.visible = true;
        this.properties = JSON.parse(JSON.stringify(newVal.properties));
        if (this.properties) {
          NodeUtils.isConditionNode(newVal) && this.getPriorityLength();
        }
      }
    }
  },
  created() {
    /** 查询流程分类列表 */
    this.getFormList();
  },
  methods: {
    onSelectUsers() {
      this.selectedUserDate = []
      this.$refs.multipleTable?.clearSelection();
      this.getDeptOptions();
      this.userOpen = true;
    },
    /**
     * 清空选项数据
     */
    clearOptionsData() {
      this.selectedUser.ids = [];
      this.selectedUser.text = [];
      this.roleIds = [];
      this.deptIds = [];
    },
    /** 查询用户列表 */
    getUserList() {
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.userTableList = response.rows;
        this.userTotal = response.total;
      });
    },
    /**
     * 查询部门下拉树结构
     */
    getDeptOptions() {
      return new Promise((resolve, reject) => {
        if (!this.deptOptions || this.deptOptions.length <= 0) {
          deptTreeSelect().then(response => {
            this.deptTempOptions = response.data;
            this.deptOptions = response.data;
            resolve()
          })
        } else {
          reject()
        }
      });
    },
    /**
     * 查询部门下拉树结构（含部门前缀）
     */
    getDeptTreeData() {
      function refactorTree(data) {
        return data.map(node => {
          let treeData = { id: `DEPT${node.id}`, label: node.label, parentId: node.parentId, weight: node.weight };
          if (node.children && node.children.length > 0) {
            treeData.children = refactorTree(node.children);
          }
          return treeData;
        });
      }
      return new Promise((resolve, reject) => {
        if (!this.deptTreeData || this.deptTreeData.length <= 0) {
          this.getDeptOptions().then(() => {
            this.deptTreeData = refactorTree(this.deptOptions);
            resolve()
          }).catch(() => {
            reject()
          })
        } else {
          resolve()
        }
      })
    },
    /**
     * 查询部门下拉树结构
     */
    getRoleOptions() {
      if (!this.roleOptions || this.roleOptions.length <= 0) {
        listRole().then(response => this.roleOptions = response.rows);
      }
    },
    /** 查询用户列表 */
    getUserList() {
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.userTableList = response.rows;
        this.userTotal = response.total;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.getUserList();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.selectedUserDate = selection;
    },
    handleTaskUserComplete() {
      if (!this.selectedUserDate || this.selectedUserDate.length <= 0) {
        this.$modal.msgError('请选择用户');
        return;
      }
      console.log("handleTaskUserComplete this.selectedUserDate",this.selectedUserDate);
      this.orgCollection.user = this.selectedUserDate;
      this.approverForm.text = this.selectedUserDate.map(k => k.nickName).join() || null;
      this.selectedUser.text = this.selectedUserDate.map(k => k.nickName) || [];
      this.userOpen = false;
    },
    changeSelectRoles(val) {
      let groups = null;
      let text = null;
      let textArr = null;
      if (val && val.length > 0) {
        groups = val.join() || null;
        textArr = this.roleOptions.filter(k => val.indexOf(`ROLE${k.roleId}`) >= 0);
        text = textArr?.map(k => k.roleName).join() || null;
      }
      this.approverForm.text = text;
      this.orgCollection.role = textArr;
    },
    /** 查询表单列表 */
    getFormList() {
      listForm().then(response => this.formOptions = response.rows)
    },
    updateFormKey(formKey) {
      console.log("updateFormKey formKey",formKey)
      this.startForm.formKey = formKey
    },
    updateFormScope() {

    },
    getFormOperates(){
      let res = []
      this.isApproverNode() && (res = this.approverForm.formOperates) && (res = this.approverForm.formKey)
      this.isStartNode() && (res = this.startForm.formOperates) && (res = this.startForm.formKey)
      return res
    },
    resetOrgColl(){
      for(let key in this.orgCollection){
        this.$set(this.orgCollection, key, [])
      }
      this.getRoleOptions();
    },
    onOrgChange(data){ },
    timeTangeLabel(item){
      const index = ['fc-time-duration','fc-date-duration'].findIndex(t => t === item.tag)
      if(index > -1){
        return '时长' + ['(小时)','(天)'][index]
      }else{
        return item.label
      }
    },
    getAssignTypeLabel(){
      const res = this.assigneeTypeOptions.find(t => t.value === this.approverForm.assigneeType)
      return res ? res.label : ''
    },
    changeAllFormOperate(val){
      const target = this.isStartNode() ? this.startForm : this.approverForm
      target.formOperates.forEach(t => t.formOperate = val)
    },
    // 是否可以显示当前条件组件
    couldShowIt(item, ...tag) {
      return tag.includes(item.tag);
    },

    initFormOperates(target){
      const formOperates = target.properties && target.properties.formOperates || []
      // 自定义组件不加入权限控制
      const res = []
      const defaultType = this.isApproverNode() ? 1 : 2 // 操作权限 0 隐藏 1 只读 2 可编辑
      const getPermissionById = id => {
        const permission = formOperates.find(t => t.formId === id)
        return permission !== undefined ? permission.formOperate : defaultType
      }
      const format = (list, parentName = '') => list.map(t => {
        const data = {
            formId: t.formId,
            required: t.required,
            label: parentName ? [parentName, t.label].join('.') : t.label,
            formOperate: getPermissionById(t.formId)
        }
        res.push(data)
        Array.isArray(t.children) && format(t.children, t.label)
      })
      //const formItems = this.$store.state.formItemList.filter(t => t.cmpType !== 'custom')
      //format(formItems)
      return res
    },

    initCopyNode () {
      this.properties = this.value.properties
    },

    initStartNodeData(){
      console.log("initStartNodeData this.value",this.value)
      this.initInitiator()
      this.startForm.formOperates = this.initFormOperates(this.value)
      this.startForm.formKey = this.value.properties.formKey
      this.approverForm.formKey = this.value.properties.formKey
    },

    copyNodeConfirm () {
      //this.$emit("confirm", this.properties, this.getOrgSelectLabel('copy') || '发起人自选');
      this.visible = false;
    },

    /**
     * 条件节点确认保存得回调
     */
    conditionNodeComfirm() {
      this.properties.conditions = this.pconditions
      // 发起人虽然是条件 但是这里把发起人放到外部单独判断
      this.properties.initiator = this.initiator['dep&user']
      //this.initiator['dep&user'] && (nodeContent = `[发起人: ${this.getOrgSelectLabel('condition')}]` + '\n' + nodeContent)
      this.$emit("confirm", this.properties, this.pconditions || '请设置条件表达式');
      this.visible = false;
    },

    getOrgSelectLabel (type) {
      return this.$refs[type + '-org']['selectedLabels']
    },
    /**
     * 开始节点确认保存
     */
    startNodeComfirm() {
      console.log("startNodeComfirm this.startForm",this.startForm)
      this.properties.initiator = this.initiator['dep&user']
      this.properties.formKey = this.startForm.formKey
      const formOperates = this.startForm.formOperates.map(t=>({formId: t.formId, formOperate: t.formOperate}))
      Object.assign(this.properties,this.startForm, {formOperates})
      //this.properties = Object.assign({}, this.properties, formOperates);
      //this.$emit("confirm", this.properties);
      this.$emit("confirm", this.properties, '所有人');
      console.log("startNodeComfirm this.properties",this.properties)
      this.visible = false;
    },
    /**
     * 审批节点确认保存
     */
    approverNodeComfirm() {
      console.log("approverNodeComfirm this.approverForm",this.approverForm)
      const assigneeType = this.approverForm.assigneeType
      let content = ''
      if (['optional','myself'].includes(assigneeType)) {
        content = this.assigneeTypeOptions.find(t => t.value === assigneeType).label
      } else if('director' === assigneeType){
        content = this.directorLevel === 1 ? '直接主管' : `第${this.directorLevel}级主管`
      } else{
        //content = this.getOrgSelectLabel('approver')
        content = this.approverForm.text
      }
      const formOperates = this.approverForm.formOperates.map(t=>({formId: t.formId, formOperate: t.formOperate}))
      this.approverForm.approvers = this.orgCollection[assigneeType]
      Object.assign(this.properties, this.approverForm, {formOperates})
      this.$emit("confirm", this.properties, content || '请设置审批人')
      this.visible = false
    },
    /**
     *  延时节点确认保存
     */
    delayNodeComfirm() {
      console.log("delayNodeComfirm this.value",this.value)
      let content = ''
      if(this.value.properties.type === "FIXED") {
          content = "等待" + this.value.properties.time + "分钟"
      } else if (this.value.properties.type === "AUTO") {
          content = "到当天" +  this.value.properties.dateTime
      } else {

      }
      const formOperates = this.startForm.formOperates.map(t=>({formId: t.formId, formOperate: t.formOperate}))
      Object.assign(this.properties, this.value.properties, {formOperates})
      this.$emit("confirm", this.properties, content || '请设置延时时间');
      this.visible = false;
    },
    // 确认修改
    confirm() {
      this.isCopyNode() && this.copyNodeConfirm()
      this.isStartNode() && this.startNodeComfirm()
      this.isApproverNode() && this.approverNodeComfirm()
      this.isConditionNode() && this.conditionNodeComfirm()
      this.isDelayNode() && this.delayNodeComfirm()
    },
    // 关闭抽屉
    cancel() {
      setTimeout(()=>{
        this.$emit("cancel");
        this.visible = false;
      }, 0)
    },
    // 配合getPriorityLength 获取前一个节点条件数组长度 用于设置优先级
    getPrevData() {
      return NodeUtils.getPreviousNode(this.value.prevId, this.processData);
    },
    // 用于获取节点优先级范围
    getPriorityLength() {
      this.priorityLength = this.getPrevData().conditionNodes.length;
    },
    // 判断是否是条件节点
    isConditionNode() {
      return this.value ? NodeUtils.isConditionNode(this.value) : false;
    },
    /** 判断是否是延时节点*/
    isDelayNode(){
      return this.value ? NodeUtils.isDelayNode(this.value) : false;
    },
    /** 判断是否是审批节点*/
    isApproverNode(){
      return this.value ? NodeUtils.isApproverNode(this.value) : false;
    },

    isStartNode(){
      return this.value ? NodeUtils.isStartNode(this.value) : false;
    },

    isCopyNode () {
      return this.value ? NodeUtils.isCopyNode(this.value) : false
    },

    initInitiator(){
      const initiator = this.value.properties && this.value.properties.initiator
      this.initiator['dep&user'] = Array.isArray(initiator) ? initiator : []
    },
        /**
     * 初始化审批节点所需数据
     */
    initApproverNodeData() {
      for (const key in this.approverForm) {
        if (this.value.properties.hasOwnProperty(key)) {
          this.approverForm[key] = this.value.properties[key];
        }
      }
      console.log("initApproverNodeData this.approverForm",this.approverForm)
      const approvers = this.approverForm.approvers
      this.resetOrgColl()
      if(this.approverForm.assigneeType === 'user'){
        this.orgCollection[this.approverForm.assigneeType] = approvers
        this.selectedUser.text = approvers.map(k => k.nickName) || [];
        this.approverForm.text = approvers.map(k => k.nickName).join() || null;
      }
      else if(this.approverForm.assigneeType === 'role'){
        this.orgCollection[this.approverForm.assigneeType] = approvers
        this.approverForm.text = approvers.map(k => k.roleName).join() || null;
      }
      this.approverForm.formOperates = this.initFormOperates(this.value)
    },
    /**
     * 初始化条件节点数据
     */
    initConditionNodeData(){
       this.pconditions = this.value.content
    },
  },
};
</script>
<style lang="stylus">
.condition-dialog {
  .el-dialog__header{
    border-bottom : 1px solid #eee;
  }
}

</style>
<style lang="stylus" scoped>
.drawer {
  >>> .el-drawer__header {
    margin-bottom: 0;
    border-bottom: 1px solid #c5c5c5;
    padding-bottom: 8px;
  }

  >>> .el-drawer__body {
    padding-bottom: 44px;
    overflow: hidden;
  }

  .pane-tab{
    height: 100%;
  }

  .pane-tab >>>  .el-tabs__item.is-top:nth-child(2) {
    padding-left: 20px;
  }

  >>> .el-tabs__item:focus{
    box-shadow: none !important;
  }

  >>> .el-tabs__header {
    margin-bottom: 0;
  }
}

.header {
  line-height: 28px;
}

.actions {
  position: absolute;
  bottom: 0;
  left: 0;
  padding: 6px 12px;
  width: 100%;
  box-sizing: border-box;
  text-align: right;
}

.radio-item {
  width: 110px;
  padding: 6px;
}

.priority-select {
  width: 118px;
  position: absolute;
  right: 26px;
}

.form-auth-table{
  font-size: 14px;
  .auth-table-header{
    background: #fafafa
    line-height: 40px;
  }
  .row{
    display: flex;
    align-items: center;
    line-height: 32px;
    padding: 8px 12px;
    border-bottom: 1px solid #efefef;
    &:hover{
      background: #f5f7fa;
    }
    .label{
      flex:1;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;

      .required{
        color: #f2725e;
      }
    }
    .radio-group{
      flex: 2;
      display: flex;
      justify-content: space-between;
    }
  }
}

.approver-pane{
  overflow-y: scroll;
  overflow-x: hidden;
  .option-box {
    font-size 14px
    padding-left 1rem
  }
}

.condition-pane{
  height 100%
  overflow scroll
}
</style>
