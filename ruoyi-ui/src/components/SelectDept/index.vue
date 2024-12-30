<template>
  <div>
    <el-dialog :title="`选择部门（${selectType === 'radio'?'单选':'多选'}）`" :visible.sync="selectDeptOpen"
               append-to-body
               width="850px"
               @close="close"
    >
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
              <div v-for="(item,index) in deptList" :key="index" class="user" @click="selectDeptClick(item,index)">
                <svg-icon class="user-icon" icon-class="peoples" class-name='custom-class'/>
                <i class="user-name">{{ item.deptName }}</i>
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
                   @click="deselect(item,index)">
                <div style="display: flex;align-items: center">
                  <svg-icon class="user-icon" icon-class="peoples" class-name='custom-class'/>
                  <i class="user-name">{{ item.deptName }}</i>
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
        <el-button type="success" v-if="isWhole" @click="wholeSelectDept">全部部门</el-button>
        <el-button type="primary" @click="determineSelectDept">确 定</el-button>
        <el-button @click="close">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {deptTreeSelect} from '@/api/system/user'

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
      // 部门名称
      deptName: undefined,
      // 部门树选项
      deptOptions: undefined,
      defaultProps: {
        children: 'children',
        label: 'label'
      },

      selectDeptOpen: false,
      deptList: []
    }
  },
  methods: {
    // 全部部门按钮
    wholeSelectDept() {
      if (this.selectType === 'radio') {
        return this.$message.error('单选模式不能选择全部部门！')
      } else {
        while (this.value.length) {
          this.value.shift()
        }
        this.value.push({
          deptName: '全部部门',
          isWhole: '1'
        })
        this.$emit('determine', this.value)
        this.$message.success('选择成功')
        this.selectDeptOpen = false
      }
    },
    // 确定按钮
    determineSelectDept() {
      if (this.value.length <= 0) {
        return this.$message.error('请选择部门！');
      }

      if (this.selectType === 'radio') {
        this.$emit("determine", this.value[0])
      } else {
        this.$emit("determine", this.value)
      }
      this.$message.success("选择成功");
      this.selectDeptOpen = false
    },
    // 关闭选择部门
    close() {
      this.selectDeptOpen = false
      this.deptList = []
      this.$emit('input', [])
    },
    // 打开选择部门
    selectDept() {
      this.selectDeptOpen = true
      this.deptList = []
      this.getDeptTree()
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
        if (!this.value[i].deptId && this.value[i].deptName === '全部部门') {
          return
        }
      }


      this.deptList = []
      if (data.children) {
        this.deptList = this.getAllTreeData(data.children).map((item) => {
          return {
            deptId: item.id,
            deptName: item.label
          }
        })

        this.deptList.push({
          deptId: data.id,
          deptName: data.label
        })
      } else {
        this.deptList.push({
          deptId: data.id,
          deptName: data.label
        })
      }
      this.deptList = (this.deptList.filter(i => !this.value.map(i => i.deptId).includes(i.deptId)))
    },
    getAllTreeData(tree) {
      let allData = [];

      // 递归函数，用于遍历树
      function traverse(node) {
        allData.push(node); // 将当前节点加入列表
        if (node.children) {
          node.children.forEach(child => traverse(child)); // 递归遍历子节点
        }
      }

      // 遍历根节点开始
      tree.forEach(node => traverse(node));

      return allData;
    },

    // 反选
    invert() {
      if (this.value.length > 0) {
        this.value.forEach((item) => {
          this.deptList.push(item)
        })
        this.$emit('input', [])
      }
    },
    // 取消选择
    deselect(item, index) {
      this.deptList.push(item)
      this.value.splice(index, 1);
    },
    // 全选
    selectAll() {
      if (this.selectType === 'radio') {
        return this.$message.error('单选模式不能多选');
      }
      this.deptList.forEach((item) => {
        this.value.push(item)
      })
      this.deptList = []
    },
    // 选择部门
    selectDeptClick(item, index) {
      if (this.selectType === 'radio') {
        if (this.value.length >= 1) {
          return this.$message.error('单选模式只能选择一个部门');
        }
        this.value.push(item)
        this.deptList.splice(index, 1);
      } else if (this.selectType === 'checkbox') {
        this.value.push(item)
        this.deptList.splice(index, 1);
      }
    },
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
