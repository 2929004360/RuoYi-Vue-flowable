<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="应用菜单" prop="menuId">
        <el-cascader filterable v-model="queryParams.menuId"
                     :options="menuOptions"
                     clearable
                     placeholder="请选择应用菜单"
        ></el-cascader>
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
          v-hasPermi="['workflow:wfFlowMenu:add']"
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
          v-hasPermi="['workflow:wfFlowMenu:edit']"
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
          v-hasPermi="['workflow:wfFlowMenu:remove']"
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
          v-hasPermi="['workflow:wfFlowMenu:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      @row-click="handleRowClick"
      ref="table"
      v-loading="loading"
      :data="wfFlowMenuList"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column align="center" label="编号" type="index" width="55"/>
      <el-table-column label="菜单名称" align="center" prop="name"/>
      <el-table-column label="表单提交路由" align="center" prop="create"/>
      <el-table-column label="表单查看路由" align="center" prop="view"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['workflow:wfFlowMenu:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workflow:wfFlowMenu:remove']"
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

    <!-- 添加或修改流程菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="应用菜单" prop="menuId">
          <el-cascader filterable style="width: 100%;" ref="cascaderRef" v-model="form.menuId"
                       :options="menuOptions"
                       clearable
                       :disabled="form.flowMenuId !== null"
                       @change="menuChange" placeholder="请选择应用菜单"
          ></el-cascader>
        </el-form-item>
        <el-form-item label="表单提交路由" prop="create">
          <el-input v-model="form.create" show-word-limit :maxlength="200" placeholder="请输入表单提交路由"/>
        </el-form-item>
        <el-form-item label="表单查看路由" prop="view">
          <el-input v-model="form.view" show-word-limit :maxlength="200" placeholder="请输入表单查看路由"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {addWfFlowMenu, delWfFlowMenu, getWfFlowMenu, listWfFlowMenu, updateWfFlowMenu} from "@/api/workflow/flowMenu";
import {listDirectoryMenu} from "@/api/system/menu";

export default {
  name: "FlowMenu",
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
      // 流程菜单表格数据
      wfFlowMenuList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 表单查看路由时间范围
      daterangeCreateTime: [],
      // 表单查看路由时间范围
      daterangeUpdateTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        menuId: null,
        name: null,
        create: null,
        view: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        menuId: [
          {required: true, message: "菜单ID不能为空", trigger: "blur"}
        ],
        create: [
          {required: true, message: "表单提交路由不能为空", trigger: "blur"}
        ],
        view: [
          {required: true, message: "表单查看路由不能为空", trigger: "blur"}
        ],
      },

      menuOptions: [],
    };
  },
  created() {
    this.getList();
    this.listDirectoryMenuFun();
  },
  methods: {
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
      let nodesObj = this.$refs['cascaderRef'].getCheckedNodes()
      if (nodesObj.length > 0) {
        this.form.name = nodesObj[0].pathLabels.join('/')
      }
    },
    /** 表格数据点击 */
    handleRowClick(row) {
      if (row) {
        this.$refs.table.toggleRowSelection(row);
      } else {
        this.$refs.table.clearSelection();
      }
    },
    /** 查询流程菜单列表 */
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

      if(this.queryParams.menuId){
        this.queryParams.menuId = this.queryParams.menuId.join(',')
      }

      listWfFlowMenu(this.queryParams).then(response => {
        this.wfFlowMenuList = response.rows;
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
        flowMenuId: null,
        menuId: null,
        name: null,
        create: null,
        view: null,
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
      this.ids = selection.map(item => item.flowMenuId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.listDirectoryMenuFun();
      this.open = true;
      this.title = "添加流程菜单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.listDirectoryMenuFun();
      const flowMenuId = row.flowMenuId || this.ids
      getWfFlowMenu(flowMenuId).then(response => {
        this.form = response.data;
        if (this.form.menuId && typeof (this.form.menuId) === 'string') {
          this.form.menuId = this.form.menuId.split(',').map(Number)
        }
        this.open = true;
        this.title = "修改流程菜单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (Array.isArray(this.form.menuId)) {
            this.form.menuId = this.form.menuId.join(',')
          }
          if (this.form.flowMenuId != null) {
            updateWfFlowMenu(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addWfFlowMenu(this.form).then(response => {
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
      const flowMenuIds = row.flowMenuId || this.ids;
      this.$modal.confirm('是否确认删除流程菜单编号为"' + flowMenuIds + '"的数据项？').then(function () {
        return delWfFlowMenu(flowMenuIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/flowable/wfFlowMenu/export', {
        ...this.queryParams
      }, `wfFlowMenu_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
