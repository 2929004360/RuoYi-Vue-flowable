<template>
  <div>
    <el-dialog :title="`选择角色（${selectType === 'radio'?'单选':'多选'}）`" :visible.sync="selectRoleOpen"
               append-to-body
               width="850px"
               @close="close"
    >
      <div class="search">
        <el-input v-model="roleName" placeholder="请输入角色名称" style="margin: 10px 0"></el-input>
        <el-button icon="el-icon-search" style="margin-left: 20px" type="primary"
                   @click="selectHandleNodeClick"
        ></el-button>
      </div>

      <el-row :gutter="10">
        <el-col :span="12">
          <div class="grid-content">
            <div class="head">
              <div>备选</div>
              <div>
                <el-button type="danger" @click="selectAll()">全选</el-button>
              </div>
            </div>
            <div style="margin-top: 20px">
              <div v-for="(item,index) in roleList" :key="index" class="user" @click="selectRoleClick(item,index)">
                <svg-icon class="user-icon" icon-class="post" class-name="custom-class"/>
                <i class="user-name">{{ item.roleName }}</i>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
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
                  <svg-icon class="user-icon" icon-class="post" class-name="custom-class"/>
                  <i class="user-name">{{ item.roleName }}</i>
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
        <el-button type="success" v-if="isWhole" @click="wholeSelectRole">全部角色</el-button>
        <el-button type="primary" @click="determineSelectRole">确 定</el-button>
        <el-button @click="close">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listRoleVo} from '@/api/system/role'

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
    // 是否显示全部按钮
    isWhole: {
      type: Boolean,
      default: false
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
      selectRoleOpen: false,
      roleList: [],
      roleName: undefined,
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  methods: {
    // 全部角色按钮
    wholeSelectRole() {
      if (this.selectType === 'radio') {
        return this.$message.error('单选模式不能选择全部角色！')
      } else {
        while (this.value.length) {
          this.value.shift()
        }
        this.value.push({
          roleName: '全部角色',
          isWhole: '1'
        })
        this.$emit('determine', this.value)
        this.$message.success('选择成功')
        this.selectRoleOpen = false
      }
    },
    // 确定按钮
    determineSelectRole() {
      if (this.value.length <= 0) {
        return this.$message.error('请选择角色！')
      }

      if (this.selectType === 'radio') {
        this.$emit('determine', this.value[0])
      } else {
        this.$emit('determine', this.value)
      }
      this.$message.success('选择成功')
      this.selectRoleOpen = false
    },
    // 关闭选择部门
    close() {
      this.selectRoleOpen = false
      this.roleList = []
      this.$emit('input', [])
    },
    // 搜索用户
    selectHandleNodeClick() {
      if (!this.roleName) {
        return this.$message.error('请输入角色名称')
      }
      this.listRoleVoFun(this.roleName)
    },
    // 打开选择角色
    selectRole() {
      this.selectRoleOpen = true
      this.roleList = []
      this.roleName = undefined
      this.listRoleVoFun(this.roleName)
    },
    // 选择角色
    selectRoleClick(item, index) {
      if (this.selectType === 'radio') {
        if (this.value.length >= 1) {
          return this.$message.error('单选模式只能选择一个角色')
        }
        this.value.push(item)
        this.roleList.splice(index, 1)
      } else if (this.selectType === 'checkbox') {
        this.value.push(item)
        this.roleList.splice(index, 1)
      }
    },
    // 取消选择
    deselect(item, index) {
      this.roleList.push(item)
      this.value.splice(index, 1)
    },
    // 反选
    invert() {
      if (this.value.length > 0) {
        this.value.forEach((item) => {
          this.roleList.push(item)
        })
        this.$emit('input', [])
      }
    },
    // 全选
    selectAll() {
      if (this.selectType === 'radio') {
        return this.$message.error('单选模式不能多选')
      }
      this.roleList.forEach((item) => {
        this.value.push(item)
      })
      this.roleList = []
    },
    handleNodeClick(data) {
      for (let i = 0; i < this.value.length; i++) {
        if (!this.value[i].roleId && this.value[i].roleName === '全部角色') {
          return
        }
      }

      this.listRoleVoFun(this.roleName)
    },
    listRoleVoFun(roleName) {
      listRoleVo({roleName}).then((res) => {
        this.roleList = res.data
        this.roleList = (this.roleList.filter(i => !this.value.map(i => i.roleId).includes(i.roleId)))
      })
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    }
  },
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
