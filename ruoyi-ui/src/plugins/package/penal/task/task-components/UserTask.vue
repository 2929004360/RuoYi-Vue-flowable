<template>
  <div>
    <el-row>
      <el-link type="warning" :underline="false">注意：审批人设置只能有一种类型</el-link>
      <h4><b>审批人设置</b></h4>
      <el-radio-group v-model="$globalStore.dataType" @change="changeDataType">
        <el-radio label="USERS">指定用户</el-radio>
        <el-radio label="ROLES">角色</el-radio>
        <el-radio label="DEPTS">部门</el-radio>
<!--        <el-radio label="INITIATOR">发起人</el-radio>-->
      </el-radio-group>
    </el-row>

    <el-row>
      <div v-if="$globalStore.dataType === 'USERS'">
        <el-tag v-for="(tag,i)  in selectedUser.text" closable :key="i" effect="plain"
                @close="handleUserClose(i)">
          {{ tag }}
        </el-tag>
        <div class="element-drawer__button">
          <el-button size="mini" type="primary" icon="el-icon-plus" @click="selectUserClick()">添加用户</el-button>
        </div>
      </div>
      <div v-if="$globalStore.dataType === 'ROLES'">
        <el-tag v-for="(tag,i)  in selectedRole.text" closable :key="i" effect="plain"
                @close="handleRoleClose(i)">
          {{ tag }}
        </el-tag>
        <div class="element-drawer__button">
          <el-button size="mini" type="primary" icon="el-icon-plus" @click="selectRoleClick()">添加角色</el-button>
        </div>
      </div>
      <div v-if="$globalStore.dataType === 'DEPTS'">
        <!--        @change="checkedDeptChange"-->
        <el-tag v-for="(tag,i)  in selectedDept.text" closable :key="i" effect="plain"
                @close="handleDeptClose(i)">
          {{ tag }}
        </el-tag>
        <div class="element-drawer__button">
          <el-button size="mini" type="primary" icon="el-icon-plus" @click="selectDeptClick()">添加部门</el-button>
        </div>
      </div>
    </el-row>

    <el-row>
      <div v-show="showMultiFlog">
        <el-divider/>
        <h4><b>多实例审批方式</b></h4>
        <el-row>
          <el-radio-group v-model="multiLoopType" @change="changeMultiLoopType()">
            <el-row>
              <el-radio label="Null">无</el-radio>
            </el-row>
            <el-row>
              <el-radio label="SequentialMultiInstance">会签（需所有审批人同意）</el-radio>
            </el-row>
            <el-row>
              <el-radio label="ParallelMultiInstance">或签（一名审批人同意即可）</el-radio>
            </el-row>
          </el-radio-group>
        </el-row>
        <el-row v-if="multiLoopType !== 'Null'">
          <el-tooltip content="开启后，实例需按顺序轮流审批" placement="top-start" @click.stop.prevent>
            <i class="header-icon el-icon-info"></i>
          </el-tooltip>
          <span class="custom-label">顺序审批：</span>
          <el-switch v-model="isSequential" @change="changeMultiLoopType()"/>
        </el-row>
      </div>
    </el-row>

    <!--选择用户数据-->
    <SelectUser ref="SelectUser" v-model="selectUserList" :allowCancel="true" :isWhole="false"
                selectType="checkbox" @determine="determineUser"></SelectUser>

    <!--选择部门数据-->
    <SelectDept ref="SelectDept" v-model="selectDeptList" selectType="checkbox" @determine="determineDept"
                :allowCancel="true" :isWhole="false"></SelectDept>

    <!--选择角色数据-->
    <SelectRole ref="SelectRole" v-model="selectRoleList" selectType="checkbox" @determine="determineRole"
                :allowCancel="true" :isWhole="false"></SelectRole>
  </div>
</template>

<script>
import TreeSelect from "@/components/TreeSelect";

const userTaskForm = {
  dataType: '',
  assignee: '',
  candidateUsers: '',
  candidateGroups: '',
  text: '',
  // dueDate: '',
  // followUpDate: '',
  // priority: ''
}

export default {
  name: "UserTask",
  props: {
    id: String,
    type: String
  },
  components: {TreeSelect},
  data() {
    return {
      loading: false,

      // 显示多实例审批方式
      showMultiFlog: false,
      // 顺序审批
      isSequential: false,
      // 多实例类型
      multiLoopType: 'Null',

      // 选择用户
      selectedUser: {
        ids: [],
        text: []
      },
      selectUserList: [],
      selectedUserDate: [],

      // 选择角色
      selectedRole: {
        ids: [],
        text: []
      },
      selectRoleList: [],
      selectedRoleDate: [],

      // 选择部门
      selectedDept: {
        ids: [],
        text: []
      },
      selectDeptList: [],
      selectedDeptDate: []
    };
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
  beforeDestroy() {
    this.bpmnElement = null;
  },
  methods: {
    resetTaskForm() {
      const bpmnElementObj = this.bpmnElement?.businessObject;
      if (!bpmnElementObj) {
        return;
      }
      this.clearOptionsData()
      this.$globalStore.dataType = bpmnElementObj['dataType'];
      if (this.$globalStore.dataType === 'USERS') {
        let userIdData = bpmnElementObj['candidateUsers'] || bpmnElementObj['assignee'];
        let userText = bpmnElementObj['text'] || [];
        if (userIdData && userIdData.toString().length > 0 && userText && userText.length > 0) {
          this.selectedUser.ids = userIdData?.toString().split(',');
          this.selectedUser.text = userText?.split(',');

          this.selectedUserDate = this.selectedUser.ids.map((userId, index) => ({
            userId: parseInt(userId),
            nickName: this.selectedUser.text[index]
          }));
        }
        console.log(this.selectedUser.ids)
        console.log(this.selectedUser.text)
        if (this.selectedUser.ids.length > 1) {
          this.showMultiFlog = true;
        }
      } else if (this.$globalStore.dataType === 'ROLES') {
        let roleIdData = bpmnElementObj['candidateGroups'] || [];
        let roleText = bpmnElementObj['text'] || [];
        if (roleIdData && roleIdData.length > 0 && roleText && roleText.length > 0) {
          this.selectedRole.ids = roleIdData?.toString().split(',');
          this.selectedRole.text = roleText?.split(',');

          this.selectedRoleDate = this.selectedRole.ids.map((roleId, index) => ({
            roleId: roleId,
            roleName: this.selectedRole.text[index]
          }));
          console.log(this.selectedRoleDate)
        }
        this.showMultiFlog = true;
      } else if (this.$globalStore.dataType === 'DEPTS') {
        let deptIdData = bpmnElementObj['candidateGroups'] || [];
        let deptText = bpmnElementObj['text'] || [];
        if (deptIdData && deptIdData.length > 0 && deptText && deptText.length > 0) {
          this.selectedDept.ids = deptIdData?.toString().split(',');
          this.selectedDept.text = deptText?.split(',');

          this.selectedDeptDate = this.selectedDept.ids.map((deptId, index) => ({
            deptId: deptId,
            deptName: this.selectedDept.text[index]
          }));
          console.log(this.selectedDeptDate)
        }
        this.showMultiFlog = true;
      }
      this.getElementLoop(bpmnElementObj);
    },
    /**
     * 清空选项数据
     */
    clearOptionsData() {
      this.selectedUser.ids = [];
      this.selectedUser.text = [];
      this.selectedRole.ids = [];
      this.selectedRole.text = [];
      this.selectedDept.ids = [];
      this.selectedDept.text = [];
      this.selectedRoleDate = [];
      this.selectedUserDate = [];
      this.selectedDeptDate = [];
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

    handleUserClose(index) {
      this.selectedUser.text.splice(index, 1)
      this.selectedUser.ids.splice(index, 1)
      this.selectedUserDate.splice(index, 1)
      userTaskForm.dataType = 'USERS';
      userTaskForm.candidateUsers = this.selectedUserDate.map(k => k.userId).join() || null;
      userTaskForm.text = this.selectedUserDate.map(k => k.nickName).join() || null;
      this.updateElementTask()
    },
    selectUserClick() {
      this.$refs.SelectUser.selectUser()
      if (this.selectedUserDate) {
        this.selectUserList = this.selectedUserDate.map((item) => {
          return {
            userId: item.userId,
            nickName: item.nickName,
          }
        })
      }
    },
    // 选择人员回调
    determineUser(e) {
      this.selectedUserDate = e.map((item) => {
        return {
          userId: item.userId,
          nickName: item.nickName,
        }
      })
      userTaskForm.dataType = 'USERS';
      this.selectedUser.text = this.selectedUserDate.map(k => k.nickName) || [];
      this.selectedUser.ids = this.selectedUserDate.map(k => k.userId) || [];
      userTaskForm.candidateUsers = this.selectedUserDate.map(k => k.userId).join() || null;
      userTaskForm.text = this.selectedUserDate.map(k => k.nickName).join() || null;
      userTaskForm.assignee = null;
      this.updateElementTask()

      console.log(this.selectedUser)
    },

    // 选择角色回调
    determineRole(val) {
      this.selectedRoleDate = val.map((item) => {
        return {
          roleId: `ROLE${item.roleId}`,
          roleName: item.roleName,
        }
      })

      let groups = null;
      let text = null;
      if (this.selectedRoleDate && this.selectedRoleDate.length > 0) {
        userTaskForm.dataType = 'ROLES';
        groups = this.selectedRoleDate.map(k => k.roleId).join() || null;
        text = this.selectedRoleDate.map(k => k.roleName).join() || null;
      } else {
        userTaskForm.dataType = null;
        this.multiLoopType = 'Null';
      }
      userTaskForm.candidateGroups = groups
      userTaskForm.text = text;
      this.selectedRole.text = this.selectedRoleDate.map(k => k.roleName) || [];
      this.selectedRole.ids = this.selectedRoleDate.map(k => k.roleId) || [];
      this.updateElementTask();
      this.changeMultiLoopType();

      console.log(this.selectedRole)
    },
    selectRoleClick() {
      this.$refs.SelectRole.selectRole()
      if (this.selectedRoleDate) {
        this.selectRoleList = this.selectedRoleDate.map((item) => {
          return {
            roleId: parseInt(item.roleId.replace('ROLE', '')),
            roleName: item.roleName,
          }
        })
      }
    },
    handleRoleClose(index) {
      this.selectedRole.text.splice(index, 1)
      this.selectedRole.ids.splice(index, 1)
      this.selectedRoleDate.splice(index, 1);
      userTaskForm.candidateGroups = this.selectedRoleDate.map(k => k.roleId).join() || null;
      userTaskForm.text = this.selectedRoleDate.map(k => k.roleName).join() || null;
      userTaskForm.dataType = 'ROLES';
      this.updateElementTask()
    },

    // 选择部门回调
    determineDept(e) {
      this.selectedDeptDate = e.map((item) => {
        return {
          deptId: `DEPT${item.deptId}`,
          deptName: item.deptName,
        }
      })

      let groups = null;
      let text = null;
      if (this.selectedDeptDate && this.selectedDeptDate.length > 0) {
        userTaskForm.dataType = 'DEPTS';
        groups = this.selectedDeptDate.map(i => i.deptId).join() || null;
        text = this.selectedDeptDate.map(k => k.deptName).join() || null;
      } else {
        userTaskForm.dataType = null;
        this.multiLoopType = 'Null';
      }
      userTaskForm.candidateGroups = groups;
      userTaskForm.text = text;
      this.selectedDept.text = this.selectedDeptDate.map(k => k.deptName) || [];
      this.selectedDept.ids = this.selectedDeptDate.map(k => k.deptId) || [];
      this.updateElementTask();

      console.log(this.selectedDept)
    },
    // 选择部门
    selectDeptClick() {
      this.$refs.SelectDept.selectDept()
      if (this.selectedDeptDate) {
        this.selectDeptList = this.selectedDeptDate.map((item) => {
          return {
            deptId: parseInt(item.deptId.replace('DEPT', '')),
            deptName: item.deptName,
          }
        })
      }
    },
    handleDeptClose(index) {
      this.selectedDept.text.splice(index, 1)
      this.selectedDept.ids.splice(index, 1)
      this.selectedDeptDate.splice(index, 1);
      userTaskForm.candidateGroups = this.selectedDeptDate.map(i => i.deptId).join() || null;
      userTaskForm.text = this.selectedDeptDate.map(k => k.deptName).join() || null;
      userTaskForm.dataType = 'DEPTS';
      this.updateElementTask();
    },

    changeDataType(val) {
      this.showMultiFlog = val === 'ROLES' || val === 'DEPTS' || val === 'USERS';
      this.multiLoopType = 'Null';
      this.changeMultiLoopType();
      // 清空 userTaskForm 所有属性值
      Object.keys(userTaskForm).forEach(key => userTaskForm[key] = null);
      userTaskForm.dataType = val;
      if (val === 'USERS') {
        if (this.selectedUser && this.selectedUser.ids && this.selectedUser.ids.length > 0) {
          if (this.selectedUser.ids.length === 1) {
            userTaskForm.assignee = this.selectedUser.ids[0];
          } else {
            userTaskForm.candidateUsers = this.selectedUser.ids.join()
          }
          userTaskForm.text = this.selectedUser.text?.join() || null
        }
      } else if (val === 'ROLES') {
        if (this.selectedRoleDate && this.selectedRoleDate.length > 0) {
          userTaskForm.candidateGroups = this.selectedRoleDate.map(i => i.roleId).join() || null;
          userTaskForm.text = this.selectedRoleDate.map(k => k.roleName).join() || null;
        }
      } else if (val === 'DEPTS') {
        if (this.selectedDeptDate && this.selectedDeptDate.length > 0) {
          userTaskForm.candidateGroups = this.selectedDeptDate.map(i => i.deptId).join() || null;
          userTaskForm.text = this.selectedDeptDate.map(k => k.deptName) || null;
        }
      } else if (val === 'INITIATOR') {
        userTaskForm.assignee = "${initiator}";
        userTaskForm.text = "流程发起人";
      }
      this.updateElementTask();
    },
    getElementLoop(businessObject) {
      if (!businessObject.loopCharacteristics) {
        this.multiLoopType = "Null";
        return;
      }
      this.isSequential = businessObject.loopCharacteristics.isSequential;
      if (businessObject.loopCharacteristics.completionCondition) {
        if (businessObject.loopCharacteristics.completionCondition.body === "${nrOfCompletedInstances >= nrOfInstances}") {
          this.multiLoopType = "SequentialMultiInstance";
        } else {
          this.multiLoopType = "ParallelMultiInstance";

        }
      }
    },
    changeMultiLoopType() {
      // 取消多实例配置
      if (this.multiLoopType === "Null") {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {loopCharacteristics: null, assignee: null});
        return;
      }
      this.multiLoopInstance = window.bpmnInstances.moddle.create("bpmn:MultiInstanceLoopCharacteristics", {isSequential: this.isSequential});
      // 更新多实例配置
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
        loopCharacteristics: this.multiLoopInstance,
        assignee: '${assignee}'
      });
      // 完成条件
      let completionCondition = null;
      // 会签
      if (this.multiLoopType === "SequentialMultiInstance") {
        completionCondition = window.bpmnInstances.moddle.create("bpmn:FormalExpression", {body: "${nrOfCompletedInstances >= nrOfInstances}"});
      }
      // 或签
      if (this.multiLoopType === "ParallelMultiInstance") {
        completionCondition = window.bpmnInstances.moddle.create("bpmn:FormalExpression", {body: "${nrOfCompletedInstances > 0}"});
      }
      // 更新模块属性信息
      window.bpmnInstances.modeling.updateModdleProperties(this.bpmnElement, this.multiLoopInstance, {
        collection: '${multiInstanceHandler.getUserIds(execution)}',
        elementVariable: 'assignee',
        completionCondition
      });
    },
  }
};
</script>

<style scoped lang="scss">
.el-row .el-radio-group {
  margin-bottom: 15px;

  .el-radio {
    line-height: 28px;
  }
}

.el-tag {
  margin-bottom: 10px;

  + .el-tag {
    margin-left: 10px;
  }
}

.custom-label {
  padding-left: 5px;
  font-weight: 500;
  font-size: 14px;
  color: #606266;
}

</style>
