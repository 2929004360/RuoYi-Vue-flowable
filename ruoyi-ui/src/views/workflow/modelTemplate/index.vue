<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入模板名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['workflow:wfModelTemplate:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['workflow:wfModelTemplate:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['workflow:wfModelTemplate:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['workflow:wfModelTemplate:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      @row-click="handleRowClick"
      ref="table"
      v-loading="loading"
      :data="wfModelTemplateList"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="编号" align="center" type="index" width="55"/>
      <el-table-column label="部门名称" align="center" prop="deptName"/>
      <el-table-column label="模板名称" align="center" prop="name"/>
      <el-table-column align="center" label="使用类型" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.flow_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="表单类型" prop="formType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.workflow_form_type" :value="scope.row.formType"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="mini"
            icon="el-icon-view"
            @click="handleProcessView(scope.row)"
            v-hasPermi="['workflow:wfModelTemplate:view']"
          >流程图
          </el-button>
          <el-button
            type="text"
            size="mini"
            icon="el-icon-brush"
            @click="handleDesigner(scope.row)"
            v-hasPermi="['workflow:wfModelTemplate:edit']"
          >模型设计
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['workflow:wfModelTemplate:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workflow:wfModelTemplate:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改模型模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="应用部门" prop="deptId">
          <treeselect v-model="form.deptId" :options="deptOptions1" :show-count="true" placeholder="请选择应用部门"
                      @select="deptIdTreeselectChange"
          />
        </el-form-item>
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模板名称" show-word-limit :maxlength="50"/>
        </el-form-item>
        <el-form-item label="使用类型" prop="type">
          <el-select style="width: 100%;" v-model="form.type" placeholder="请选择使用类型">
            <el-option
              v-for="dict in dict.type.flow_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="注意">
          <el-link :underline="false" type="warning">
            表单类型为【流程表单】，流程配置中开始节点必须选择表单
            <br/>
            表单类型为【业务表单】，流程配置中开始节点不用选择表单
          </el-link>
        </el-form-item>
        <el-form-item label="表单类型" prop="formType">
          <el-select style="width: 100%;" v-model="form.formType" placeholder="请选择表单类型">
            <el-option
              v-for="dict in dict.type.workflow_form_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <el-dialog :title="designerData.title" :visible.sync="designerOpen" append-to-body fullscreen>
      <process-designer
        :key="designerOpen"
        ref="modelDesigner"
        v-loading="designerData.loading"
        v-model="designerData.bpmnXml"
        @save="onSaveDesigner"
        :isSave="true"
      />
    </el-dialog>

    <!-- 流程图 -->
    <el-dialog :title="processView.title" :visible.sync="processView.open" width="70%" append-to-body>
      <process-viewer :key="`designer-${processView.index}`" :xml="processView.xmlData" :style="{height: '700px'}"/>
    </el-dialog>
  </div>
</template>

<script>
import {
  addWfModelTemplate,
  delWfModelTemplate,
  editBpmnXml,
  getWfModelTemplate,
  listWfModelTemplate,
  updateWfModelTemplate,
} from "@/api/workflow/wfModelTemplate";
import {deptTreeSelect} from "@/api/system/user";
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import ProcessDesigner from '@/components/ProcessDesigner';
import ProcessViewer from '@/components/ProcessViewer'

export default {
  name: "WfModelTemplate",
  dicts: ['flow_type','workflow_form_type'],
  components: {
    Treeselect,
    ProcessDesigner,
    ProcessViewer,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 模型模板表格数据
      wfModelTemplateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 流程xml时间范围
      daterangeCreateTime: [],
      // 流程xml时间范围
      daterangeUpdateTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deptId: null,
        name: null,
        deptName: null,
        type: null,
        createTime: null,
        updateTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {required: true, message: '请输入模板名称', trigger: 'blur'}
        ],
        deptId: [
          {required: true, message: '请选择部门', trigger: 'change'}
        ],
      },

      // 部门树选项
      deptOptions1: [],

      designerData: {
        loading: false,
        bpmnXml: '',
        modelTemplateId: null,
        title: null,
      },

      designerOpen: false,

      processView: {
        title: '',
        open: false,
        index: undefined,
        xmlData: "",
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查看流程图 */
    handleProcessView(row) {
      let modelTemplateId = row.modelTemplateId;
      this.processView.title = "流程图";
      this.processView.index = modelTemplateId;
      getWfModelTemplate(modelTemplateId).then(response => {
        this.processView.xmlData = response.data.bpmnXml || ''
      });
      this.processView.open = true;
    },
    onSaveDesigner(bpmnXml) {
      this.bpmnXml = bpmnXml;
      let dataBody = {
        modelTemplateId: this.designerData.modelTemplateId,
        bpmnXml: this.bpmnXml
      }
      this.$confirm("是否将此模型保存？", "提示", {
        distinguishCancelAndClose: true,
        confirmButtonText: '是',
        cancelButtonText: '否'
      }).then(() => {
        editBpmnXml(dataBody).then(response => {
          this.$modal.msgSuccess("保存成功");
          this.designerOpen = false;
        });
      }).catch(action => {

      })
    },
    /** 设计按钮操作 */
    handleDesigner(row) {
      this.designerData.loading = true;
      this.designerData.title = "流程模型 - " + row.name;
      this.designerData.modelTemplateId = row.modelTemplateId;
      const modelTemplateId = row.modelTemplateId
      getWfModelTemplate(modelTemplateId).then(response => {
        this.designerData.bpmnXml = response.data.bpmnXml || ''
        this.designerData.loading = false;
        this.designerOpen = true;
      });
    },

    /** 查询部门下拉树结构 */
    getDeptTree1() {
      deptTreeSelect().then(response => {
        this.deptOptions1 = response.data
      })
    },
    //部门选择
    deptIdTreeselectChange(e) {
      this.form.deptName = e.label
    },
    /** 表格数据点击 */
    handleRowClick(row) {
      if (row) {
        this.$refs.table.toggleRowSelection(row);
      } else {
        this.$refs.table.clearSelection();
      }
    },
    /** 查询模型模板列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params["beginCreateTime"] = this.daterangeCreateTime[0];
        this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
      }
      if (null != this.daterangeUpdateTime && '' != this.daterangeUpdateTime) {
        this.queryParams.params["beginUpdateTime"] = this.daterangeUpdateTime[0];
        this.queryParams.params["endUpdateTime"] = this.daterangeUpdateTime[1];
      }
      listWfModelTemplate(this.queryParams).then(response => {
        this.wfModelTemplateList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        modelTemplateId: null,
        deptId: null,
        name: null,
        deptName: null,
        type: '1',
        formType: '0',
        bpmnXml: null,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangeCreateTime = [];
      this.daterangeUpdateTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.modelTemplateId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.getDeptTree1();
      this.open = true;
      this.title = "添加模型模板";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getDeptTree1();
      const modelTemplateId = row.modelTemplateId || this.ids
      getWfModelTemplate(modelTemplateId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改模型模板";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.modelTemplateId != null) {
            updateWfModelTemplate(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addWfModelTemplate(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const modelTemplateIds = row.modelTemplateId || this.ids;
      this.$modal.confirm('是否确认删除模型模板编号为"' + modelTemplateIds + '"的数据项？').then(function () {
        return delWfModelTemplate(modelTemplateIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/flowable/wfModelTemplate/export', {
        ...this.queryParams
      }, `wfModelTemplate_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
