<template>
  <div>
    <el-dialog title="选择流程" :visible.sync="open" append-to-body width="800px">
      <el-table v-loading="loading" :data="startList">
        <el-table-column align="center" label="编号" type="index" width="55"/>
        <el-table-column label="流程图标" align="center" prop="icon">
          <template slot-scope="scope">
            <image-preview :height="50" v-if="scope.row.icon" :src="scope.row.icon" :width="50"/>
          </template>
        </el-table-column>
        <el-table-column label="流程名称" align="center">
          <template slot-scope="scope">
            <el-link type="primary" :underline="false" @click="handleProcessView(scope.row)">
              {{ scope.row.processName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="流程版本" align="center">
          <template slot-scope="scope">
            <el-tag size="medium">v{{ scope.row.version }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.suspended">激活</el-tag>
            <el-tag type="warning" v-else>挂起</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="部署时间" align="center" prop="deploymentTime" width="180"/>
        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button
              icon="el-icon-check"
              size="mini"
              type="text"
              @click="select(scope.row)"
            >选择
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 流程图 -->
    <el-dialog :title="processView.title" :visible.sync="processView.open" width="70%" append-to-body>
      <process-viewer :key="`designer-${processView.index}`" :xml="processView.xmlData" :style="{height: '700px'}"/>
    </el-dialog>
  </div>
</template>

<script>
import {getStartList} from "@/api/workflow/process";
import ProcessViewer from '@/components/ProcessViewer'
import {getBpmnXml} from '@/api/workflow/deploy'

export default {
  name: "SelectFlow",
  components: {
    ProcessViewer
  },
  data() {
    return {
      startList: [],
      open: false,
      loading: false,

      processView: {
        title: '',
        open: false,
        index: undefined,
        xmlData: "",
      },

      query:{}
    }
  },
  methods: {
    getFlow(menuId, query) {
      if (!menuId) {
        this.$message.error('menuId不能为空！')
        return
      }
      this.loading = true
      this.query = query

      getStartList(menuId).then((res) => {
        if (res.data.length === 0) {
          this.$modal.msgError("请先配置流程")
        } else if (res.data.length === 1) {
          let data = res.data[0]
          this.$router.push({
            path: data.formCreatePath,
            query: {
              definitionId: data.definitionId,
              processName: data.processName,
              ...query
            }
          })
        } else {
          this.startList = res.data
          this.open = true
        }

        this.loading = false
      })
    },
    /** 查看流程图 */
    handleProcessView(row) {
      let definitionId = row.definitionId;
      this.processView.title = "流程图";
      this.processView.index = definitionId;
      // 发送请求，获取xml
      getBpmnXml(definitionId).then(response => {
        this.processView.xmlData = response.data;
      })
      this.processView.open = true;
    },
    select(data) {
      this.$router.push({
        path: data.formCreatePath,
        query: {
          definitionId: data.definitionId,
          processName: data.processName,
          ...this.query
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
