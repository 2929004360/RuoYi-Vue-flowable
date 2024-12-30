<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请人" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入申请人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请假类别" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择请假类别" clearable style="width: 240px">
          <el-option v-for="dict in dict.type.work_leave_category" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="审批进度" prop="schedule">
        <el-select v-model="queryParams.schedule" placeholder="请选择审批进度" clearable style="width: 240px">
          <el-option v-for="dict in dict.type.common_schedule" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
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
          v-hasPermi="['work:leave:add']"
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
          v-hasPermi="['work:leave:edit']"
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
          v-hasPermi="['work:leave:remove']"
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
          v-hasPermi="['work:leave:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="leaveList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="编号" align="center" type="index" width="55"/>
      <el-table-column label="申请人" align="center" prop="userName"/>
      <el-table-column label="请假类别" align="center" prop="category">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.work_leave_category" :value="scope.row.category"/>
        </template>
      </el-table-column>
      <el-table-column label="请假天数" align="center" prop="holiday"/>
      <el-table-column label="职位" align="center" prop="position"/>
      <el-table-column label="开始时间" align="center" prop="startTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="请假内容" align="center" prop="content"/>
      <el-table-column label="是否补假" align="center" prop="isRepair">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.common_whether" :value="scope.row.isRepair"/>
        </template>
      </el-table-column>
      <el-table-column
        label="审批进度"
        align="center" prop="schedule">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.common_schedule" :value="scope.row.schedule"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['work:leave:view']"
            icon="el-icon-reading"
            size="mini"
            type="text"
            @click="preView(scope.row)"
          >查看
          </el-button>

          <el-button
            v-if="scope.row.schedule === 'unapproved'"
            v-hasPermi="['work:leave:submit']"
            icon="el-icon-position"
            size="mini"
            type="text"
            @click="submit(scope.row)"
          >请假提交
          </el-button>

          <el-button
            v-if="scope.row.schedule === 'running'"
            v-hasPermi="['work:leave:revoke']"
            icon="el-icon-bottom"
            size="mini"
            type="text"
            @click="revoke(scope.row)"
          >请假撤销
          </el-button>

          <el-button
            v-if="scope.row.schedule === 'terminated' || scope.row.schedule === 'canceled'"
            v-hasPermi="['work:leave:resubmit']"
            icon="el-icon-position"
            size="mini"
            type="text"
            @click="resubmit(scope.row)"
          >重新提交
          </el-button>


          <el-dropdown v-hasPermi="['work:leave:edit','work:leave:remove']"
                       v-if=" scope.row.schedule === 'unapproved'
                       || scope.row.schedule === 'terminated'
                       || scope.row.schedule === 'canceled'"

          >
              <span class="el-dropdown-link">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-hasPermi="['work:leave:edit']">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleUpdate(scope.row)"
                >修改
                </el-button>
              </el-dropdown-item>
              <el-dropdown-item v-hasPermi="['work:leave:remove']">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                >删除
                </el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

    <SelectFlow ref="selectFlow"></SelectFlow>
  </div>
</template>

<script>
import {delLeave, getLeave, listLeave, reapply, revoke, submit} from "@/api/work/leave";

export default {
  name: "Leave",
  dicts: ['work_leave_category', 'common_schedule', 'common_whether'],
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
      // 请假管理表格数据
      leaveList: [],
      // 删除标志时间范围
      daterangeCreateTime: [],
      // 删除标志时间范围
      daterangeUpdateTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        userName: null,
        definitionId: null,
        processId: null,
        processName: null,
        category: null,
        holiday: null,
        position: null,
        startTime: null,
        endTime: null,
        content: null,
        isRepair: null,
        schedule: null,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 预览
    preView(row) {
      if (row.schedule === 'running' || row.schedule === 'completed' || row.schedule === 'terminated') {
        this.$tab.openPage("查询请假详情", "/work/leave/viewLeave/index", {
          procInsId: row.processId,
          formType: '1',
          businessId: row.leaveId,
        })
      } else {
        this.$tab.openPage("查询请假详情", "/work/leave/viewLeave/index", {
          formType: '1',
          businessId: row.leaveId,
          definitionId: row.definitionId,
        })
      }
    },
    // 提交审核
    submit(row) {
      this.$confirm('确定提交审核吗?', '温馨提示！', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const leaveId = row.leaveId
        submit(leaveId).then(response => {
          this.$modal.msgSuccess('提交成功')
          this.getList()
        })
      }).catch(() => {
      })
    },
    // 撤销提交
    revoke(row) {
      this.$confirm('确定撤销提交审核吗?', '温馨提示！', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const leaveId = row.leaveId
        revoke(leaveId).then(response => {
          this.$modal.msgSuccess('撤销成功')
          this.getList()
        })
      }).catch(() => {
      })
    },
    // 重新提交
    resubmit(row) {
      this.$confirm('确定重新提交审核吗?', '温馨提示！', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const leaveId = row.leaveId
        reapply(leaveId).then(response => {
          this.$modal.msgSuccess('重新提交成功')
          this.getList()
        })
      }).catch(() => {
      })
    },

    /** 查询请假管理列表 */
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
      listLeave(this.queryParams).then(response => {
        this.leaveList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
      this.ids = selection.map(item => item.leaveId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$refs.selectFlow.getFlow(this.$flowMenu.leaveFlowMenu)
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const leaveId = row.leaveId || this.ids
      getLeave(leaveId).then((response) => {
        let data = response.data
        this.$tab.openPage("修改请假", "/work/leave/updateLeave/index", {
          definitionId: data.definitionId,
          processName: data.processName,
          businessId: data.leaveId,
        })
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const leaveIds = row.leaveId || this.ids;
      this.$modal.confirm('是否确认删除请假管理编号为"' + leaveIds + '"的数据项？').then(function () {
        return delLeave(leaveIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('work/leave/export', {
        ...this.queryParams
      }, `leave_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style lang="scss" scoped>
.el-dropdown-link {
  cursor: pointer;
  color: var(--current-color);
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>
