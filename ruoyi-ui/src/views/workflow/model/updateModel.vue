<template>
  <div class="app-container">
    <div class="insert_model_tabs">
      <div style="flex: 1;">
        <el-button icon="el-icon-back" round size="mini" @click="goBack">返回</el-button>
      </div>
      <div style="flex: 8">
        <el-tabs v-model="activeName">
          <el-tab-pane label="基础配置" name="basics"></el-tab-pane>
          <el-tab-pane v-if="form.processConfig === '2'" label="流程配置" name="process"></el-tab-pane>
        </el-tabs>
      </div>
      <div style="flex: 1;text-align: right;display: flex;justify-content: flex-end;">
        <el-button type="primary" icon="el-icon-check" round size="mini" @click="save">保存</el-button>
        <!--        <el-button type="primary" icon="el-icon-check" round size="mini">部署</el-button>-->
      </div>
    </div>

    <div v-show="activeName === 'basics'">
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模型标识" prop="modelKey">
              <el-input v-model="form.modelKey" clearable placeholder="请输入模型标识"
                        maxlength="50" show-word-limit
                        :disabled="form.modelId !== undefined"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="form.modelName" clearable placeholder="请输入模型名称"
                        maxlength="50" show-word-limit
                        :disabled="form.modelId !== undefined"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="流程分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择流程分类" filterable clearable
                         style="width:100%">
                <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName"
                           :value="item.code"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入内容" maxlength="200"
                        show-word-limit/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="注意">
              <el-link :underline="false" type="warning">
                表单类型为【流程表单】，流程配置中开始节点必须选择表单
                <br/>
                表单类型为【业务表单】，流程配置中开始节点不用选择表单
              </el-link>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="表单类型" prop="formType">
              <el-radio-group v-model="form.formType" @change="formTypeChange">
                <el-radio
                  v-for="dict in dict.type.workflow_form_type"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="12" v-if="form.formType === '1'">
            <el-form-item label="应用菜单" prop="menuId">
              <el-cascader filterable style="width: 100%;" ref="cascaderRef" v-model="form.menuId"
                           :options="menuOptions"
                           clearable
                           @change="menuChange" placeholder="请选择应用菜单"
              ></el-cascader>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="流程表单" prop="formId" v-if="form.formType === '0'">
              <el-select v-model="form.formId"
                         @change="formIdChange"
                         placeholder="请选择流程表单"
                         filterable
                         clearable
                         style="width:100%">
                <el-option v-for="item in listForm" :key="item.formId" :label="item.formName"
                           :value="item.formId"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="流程配置" prop="processConfig">
              <el-radio-group v-model="form.processConfig" @change="processConfigChange">
                <el-radio
                  v-for="dict in dict.type.workflow_process_config"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="12" v-if="form.processConfig === '1'">
            <el-form-item label="模型模板" prop="modelTemplateId">
              <el-select v-model="form.modelTemplateId"
                         placeholder="请选择模型模板"
                         filterable
                         clearable
                         @change="modelTemplateChange"
                         style="width:100%">
                <el-option v-for="item in WfModelTemplateList" :key="item.modelTemplateId" :label="item.name"
                           :value="item.modelTemplateId"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="流程图标" prop="icon">
              <image-upload v-model="form.icon" :insertProgram="{width:600,height:600}" :limit="1"
                            :scale="{w:1,h:1}"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="注意">
              <el-link :underline="false" type="warning">使用人和使用部门默认为或者权限,不选择默认自己权限</el-link>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="使用人" prop="selectUserList">
              <el-button type="primary" @click="selectUserClick()">选人员</el-button>
              <div v-if="form.selectUserList && form.selectUserList.length > 0"
                   style="margin-top: 10px">
                <el-tag
                  v-for="(tag,i) in form.selectUserList"
                  :key="i"
                  :disable-transitions="false"
                  closable
                  style="margin-right: 10px"
                  @close="handleClose('user',i)"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="使用部门" prop="selectDeptList">
              <el-button type="primary" @click="selectDeptClick()">选部门</el-button>
              <div v-if="form.selectDeptList && form.selectDeptList.length > 0"
                   style="margin-top: 10px">
                <el-tag
                  v-for="(tag,i) in form.selectDeptList"
                  :key="i"
                  :disable-transitions="false"
                  closable
                  style="margin-right: 10px"
                  @close="handleClose('dept',i)"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div v-show="activeName === 'process'">
      <process-designer
        v-if="form.processConfig === '2'"
        ref="modelDesigner"
        v-model="form.bpmnXml"
        :isSave="false"
      />
    </div>

    <!--选择用户数据-->
    <SelectUser ref="SelectUser" v-model="selectUserList" :allowCancel="true" :isWhole="false"
                selectType="checkbox" @determine="determineUser"></SelectUser>

    <!--选择部门数据-->
    <SelectDept ref="SelectDept" v-model="selectDeptList" :isWhole="false" selectType="checkbox"
                @determine="determineDept"></SelectDept>
  </div>
</template>

<script>
import {listCategory} from "@/api/workflow/category";
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import {listDirectoryMenu} from "@/api/system/menu";
import {queryList} from "@/api/workflow/form";
import ProcessDesigner from '@/components/ProcessDesigner';
import {getWfFlowMenuInfo} from "@/api/workflow/flowMenu";
import {listWfModelTemplateVo} from "@/api/workflow/wfModelTemplate";
import {getModel, updateModel} from "@/api/workflow/model";

export default {
  name: "UpdateModel",
  components: {Treeselect, ProcessDesigner},
  dicts: ['workflow_form_type', 'workflow_process_config'],
  data() {
    return {
      activeName: 'basics',

      categoryOptions: [],

      menuOptions: [],
      bpmnXml: undefined,
      form: {
        modelId: undefined,
        menuId: undefined,
        modelKey: undefined,
        menuName: undefined,
        modelName: undefined,
        category: undefined,
        description: undefined,
        formType: '0',
        formId: undefined,
        formName: undefined,
        icon: undefined,
        bpmnXml: undefined,
        modelTemplateId: undefined,
        processConfig: '1',
        selectUserList: [],
        selectDeptList: [],
      },
      // 流程表单
      listForm: [],

      // 表单校验
      rules: {
        modelKey: [
          {required: true, message: "模型标识不能为空", trigger: "blur"}
        ],
        modelName: [
          {required: true, message: "模型名称不能为空", trigger: "blur"}
        ],
        category: [
          {required: true, message: "请选择流程分类", trigger: "change"}
        ],
        formId: [
          {required: true, message: "请选择流程表单", trigger: "change"}
        ],
        icon: [
          {required: true, message: "请上传流程图标", trigger: "blur"}
        ],
        menuId: [
          {required: true, message: "请选择应用菜单", trigger: "change"}
        ],
        modelTemplateId: [
          {required: true, message: "请选择模型模板", trigger: "change"}
        ],
      },

      selectUserList: [],
      selectDeptList: [],

      WfModelTemplateList: []
    };
  },
  created() {
    let modelId = this.$route.query.modelId

    if (!modelId) {
      this.$message.error('modelId为空');
      return
    }
    this.getCategoryList()
    this.listDirectoryMenuFun()
    this.queryListFun()

    getModel(modelId).then((res) => {
      this.$delete(res.data, "createTime")
      this.form = res.data
      if (this.form.menuId && typeof (this.form.menuId) === 'string') {
        this.form.menuId = this.form.menuId.split(',').map(Number)
      }
      this.getListWfModelTemplateVo();
    })
  },
  methods: {
    // 表单配置选择
    processConfigChange(e) {
      this.form.bpmnXml = ''
      if (e === '2') {
        this.form.modelTemplateId = undefined
      }
    },
    /**
     * 模板选择
     *
     */
    modelTemplateChange(e) {
      this.WfModelTemplateList.forEach((item) => {
        if (e === item.modelTemplateId) {
          this.form.bpmnXml = item.bpmnXml
        }
      })
    },
    // 表单类型切换
    formTypeChange() {
      this.form.modelTemplateId = undefined
      this.getListWfModelTemplateVo()
    },
    /**
     * 获取模型模板
     */
    getListWfModelTemplateVo() {
      listWfModelTemplateVo({formType: this.form.formType}).then((res) => {
        this.WfModelTemplateList = res.data
      })
    },
    /** 查询流程分类列表 */
    getCategoryList() {
      listCategory().then(response => this.categoryOptions = response.rows)
    },
    // 获取菜单
    listDirectoryMenuFun() {
      listDirectoryMenu().then((res) => {
        this.menuOptions = res.data.map((item) => {
          let children
          if (item.menuList.length > 0) {
            children = item.menuList.map((item1) => {
              let childrenData = null
              if (item1.menuList.length > 0) {
                childrenData = item1.menuList.map((item2) => {
                  return {
                    value: item2.menuId,
                    label: item2.menuName
                  }
                })
              }
              return {
                value: item1.menuId,
                label: item1.menuName,
                children: childrenData
              }
            })
          }

          return {
            value: item.menuId,
            label: item.menuName,
            children: children
          }
        })
      })
    },
    // 选择菜单
    menuChange(data) {
      if (data && data.length > 0) {
        getWfFlowMenuInfo(data.join(",")).then((res) => {
          if (!res.data) {
            this.$message.error("该菜单下没有流程表单")
            this.form.menuId = undefined
            this.form.formCustomViewPath = undefined
            this.form.formCustomCreatePath = undefined
            this.form.menuName = undefined
            return
          }
          this.form.formCustomViewPath = res.data.view
          this.form.formCustomCreatePath = res.data.create
          let nodesObj = this.$refs['cascaderRef'].getCheckedNodes()
          if (nodesObj.length > 0) {
            this.form.menuName = nodesObj[0].pathLabels.join('/')
          }
        })
      }
    },
    // 选择人员回调
    determineUser(e) {
      this.form.selectUserList = e.map((item) => {
        return {
          permissionId: item.userId,
          name: item.nickName,
        }
      })
    },
    selectUserClick() {
      this.$refs.SelectUser.selectUser()
      if (this.form.selectUserList) {
        this.selectUserList = this.form.selectUserList.map((item) => {
          return {
            userId: item.permissionId,
            nickName: item.name,
          }
        })
      }
    },
    // 选择部门回调
    determineDept(e) {
      this.form.selectDeptList = e.map((item) => {
        return {
          permissionId: item.deptId,
          name: item.deptName,
        }
      })
    },
    // 选择部门
    selectDeptClick() {
      this.$refs.SelectDept.selectDept()
      if (this.form.selectDeptList) {
        this.selectDeptList = this.form.selectDeptList.map((item) => {
          return {
            deptId: item.permissionId,
            deptName: item.name,
          }
        })
      }
    },
    // tag标签删除按钮
    handleClose(type, index) {
      if (type === 'user') {
        this.form.selectUserList.splice(index, 1)
      }
      if (type === 'dept') {
        this.form.selectDeptList.splice(index, 1)
      }
    },
    /**
     * 返回
     */
    goBack() {
      const obj = {path: "/process/model"};
      this.$tab.closeOpenPage(obj);
    },
    /**
     * 获取流程表单
     */
    queryListFun() {
      queryList().then((res) => {
        this.listForm = res.data
      })
    },
    /**
     * 选中流程表单获取表单名称
     */
    formIdChange(e) {
      let form = this.listForm.find(item => item.formId === e)
      this.form.formName = form.formName
    },
    /**
     * 保存流程
     */
    save() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (Array.isArray(this.form.menuId)) {
            this.form.menuId = this.form.menuId.join(',')
          }
          if (this.form.formType === '0') {
            this.$delete(this.form, 'menuId');
            this.$delete(this.form, 'menuName');
          } else {
            this.$delete(this.form, 'formId');
            this.$delete(this.form, 'formName');
          }

          if (this.form.processConfig === '2') {
            this.$delete(this.form, 'modelTemplateId');
          }

          updateModel(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.goBack();
          }).catch(() => {
            if (this.form.menuId && typeof (this.form.menuId) === 'string') {
              this.form.menuId = this.form.menuId.split(',')
            }
          });
        }
      })
    },
  }
}
</script>

<style lang="scss">
.insert_model_tabs {

  /*去掉tabs底部的下划线*/
  .el-tabs__nav-wrap::after {
    position: static !important;
  }

  .el-tabs__nav-scroll {
    display: flex;
    justify-content: center;
  }
}

</style>

<style lang="scss" scoped>
.insert_model_tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
