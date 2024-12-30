<template>
  <div>
    <el-dialog :title="`选择用户（${selectType === 'radio'?'单选':'多选'}）`" :visible.sync="selectUserOpen"
               append-to-body
               width="850px" @close="close"
    >
      <el-link v-if="text" type="primary" :underline="false">{{text}}</el-link>
      <div class="search">
        <el-input v-model="queryParams.nickName" placeholder="请输入用户名称" style="margin: 10px 0"></el-input>
        <el-button icon="el-icon-search" style="margin-left: 20px" type="primary"
                   @click="selectHandleNodeClick"
        ></el-button>
      </div>

      <el-row :gutter="10">
        <el-col :span="8">
          <div class="grid-content">
            <el-input
              v-model="deptName"
              clearable
              placeholder="请输入部门名称"
              prefix-icon="el-icon-search"
              size="small"
              style="margin: 10px 0"
            />
            <el-tree
              ref="tree"
              :data="deptOptions"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :props="defaultProps"
              default-expand-all
              highlight-current
              node-key="id"
              style="margin-top: 10px"
              @node-click="handleNodeClick"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="grid-content">
            <div class="head">
              <div>备选</div>
              <div>
                <el-button type="danger" @click="selectAll()">全选</el-button>
              </div>
            </div>
            <div style="margin-top: 20px">
              <div v-for="(item,index) in userList" :key="index" class="user" @click="selectPersonnel(item,index)">
                <i class="el-icon-s-custom user-icon"></i>
                <i class="user-name">{{ item.nickName }}</i>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="grid-content">
            <div class="head">
              <div>已选</div>
              <div>
                <el-button type="danger" @click="invert()">反选</el-button>
              </div>
            </div>
            <div style="margin-top: 20px">
              <div v-for="(item,index) in value" :key="index" class="user" style="justify-content: space-between;"
                   @click="deselect(item,index)"
              >
                <div style="display: flex;align-items: center">
                  <i class="el-icon-s-custom user-icon"></i>
                  <i class="user-name">{{ item.nickName }}</i>
                  <el-link style="margin-left: 10px" v-if="!allowCancel && item.userId === userData.userId"
                           :underline="false" type="primary"
                  >自己
                  </el-link>
                </div>
                <div style="margin-right: 10px">
                  <i class="el-icon-check"></i>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <div slot="footer" class="dialog-footer">
        <el-button type="success" v-if="isWhole" @click="wholeSelectUser">全部人员</el-button>
        <el-button type="primary" @click="determineSelectUser">确 定</el-button>
        <el-button @click="close">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {deptTreeSelect, listUserList} from '@/api/system/user'

export default {
  name: 'index',
  props: {
    // 选择人员数据
    value: {
      type: Array
    },
    // 选择类型 radio=单选,checkbox多选
    selectType: {
      type: String,
      default: 'radio'
    },
    // 允许取消本人(true 允许 必传userData, false 不允许)
    allowCancel: {
      type: Boolean,
      default: true
    },
    // 用户数据
    userData: {
      type: Object
    },
    // 是否显示全部按钮
    isWhole: {
      type: Boolean,
      default: false
    },
    // 显示文本
    text: {
      type: String,
      default: ''
    }
  },
  computed: {
    dataValue: {
      get: function () {
        return this.value
      },
      set: function (newValue) {
        this.value = newValue
      }
    }
  },
  data() {
    return {
      // 部门名称
      deptName: undefined,
      // 部门树选项
      deptOptions: undefined,
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 遮罩层
      loading: true,
      // 日期范围
      dateRange: [],
      // 用户表格数据
      userList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        userName: undefined,
        nickName: undefined,
        phonenumber: undefined,
        status: undefined,
        deptId: undefined
      },
      selectUserOpen: false
    }
  },
  methods: {
    // 全部人员按钮
    wholeSelectUser() {
      if (this.selectType === 'radio') {
        return this.$message.error('单选模式不能选择全部人员！')
      } else {
        while (this.value.length) {
          this.value.shift()
        }
        this.value.push({
          nickName: '全部人员',
          isWhole: '1'
        })
        this.$emit('determine', this.value)
        this.$message.success('选择成功')
        this.selectUserOpen = false
      }
    },
    // 确定按钮
    determineSelectUser() {
      if (this.value.length <= 0) {
        return this.$message.error('请选择用户！')
      }

      if (this.selectType === 'radio') {
        this.$emit('determine', this.value[0])
      } else {
        this.$emit('determine', this.value)
      }
      this.$message.success('选择成功')
      this.selectUserOpen = false
    },
    // 关闭选择用户
    close() {
      this.selectUserOpen = false
      this.userList = []
      this.$emit('input', [])
    },
    // 打开选择用户
    selectUser() {
      this.selectUserOpen = true
      this.userList = []
      this.getDeptTree()
      this.queryParams.nickName = undefined
      this.queryParams.deptId = undefined
    },
    // 反选
    invert() {
      if (this.value.length > 0) {
        if (!this.allowCancel) {
          for (let i = 0; i < this.value.length; i++) {
            if (this.userData.userId === this.value[i].userId) {
              return this.$message.error('不允许取消自己')
            }
          }
        }
        this.value.forEach((item) => {
          this.userList.push(item)
        })
        this.$emit('input', [])
      }
    },
    // 全选
    selectAll() {
      if (this.selectType === 'radio') {
        return this.$message.error('单选模式不能多选')
      }
      this.userList.forEach((item) => {
        this.value.push(item)
      })
      this.userList = []
    },
    // 取消选择
    deselect(item, index) {
      if (!this.allowCancel) {
        if (this.userData.userId === item.userId) {
          return this.$message.error('不允许取消自己')
        }
      }
      this.userList.push(item)
      this.value.splice(index, 1)
    },
    // 选择人员
    selectPersonnel(item, index) {
      if (this.selectType === 'radio') {
        if (this.value.length >= 1) {
          return this.$message.error('单选模式只能选择一个用户')
        }
        this.value.push(item)
        this.userList.splice(index, 1)
      } else if (this.selectType === 'checkbox') {
        this.value.push(item)
        this.userList.splice(index, 1)
      }
    },
    /** 查询用户列表 */
    getList() {
      this.loading = true
      listUserList(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.userList = response.data.map((item) => {
            return {
              userId: item.userId,
              nickName: item.nickName,
              phone: item.phonenumber,
              email: item.email,
              avatar: item.avatar,
              avatarBase64: item.avatarBase64,
              deptId: item.deptId,
              deptName: item.dept.deptName
            }
          })
          this.userList = (this.userList.filter(i => !this.value.map(i => i.userId).includes(i.userId)))
          this.total = response.total
          this.loading = false
        }
      )
    },
    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data
      })
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    // 节点单击事件
    handleNodeClick(data) {
      for (let i = 0; i < this.value.length; i++) {
        if (!this.value[i].userId && this.value[i].nickName === '全部人员') {
          return
        }
      }

      this.queryParams.deptId = data.id
      this.queryParams.nickName = undefined
      this.handleQuery()
    },
    // 搜索用户
    selectHandleNodeClick() {
      if (!this.queryParams.nickName) {
        return this.$message.error('请输入用户昵称')
      }
      this.handleQuery()
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    }
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val)
    }
  }
}
</script>

<style lang="scss" scoped>
.search {
  display: flex;
  align-items: center;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 10px 0;
}

.user {
  cursor: pointer;
  margin: 6px 0;
  padding: 6px 0;
  display: flex;
  align-items: center;

  .user-name {
    margin-left: 10px;
    /*font-weight: bold;*/
  }

  .user-icon {
    margin-left: 10px;
    /*font-size: 24px;*/
    color: var(--current-color);
  }

  &:hover {
    background: #dddddd;
  }
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

.bg-purple-dark {
  background: #99a9bf;
}

.bg-purple {
  background: #d3dce6;
}

.bg-purple-light {
  background: #e5e9f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}
</style>
