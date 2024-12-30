<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="流程名称" prop="processName">
        <el-input
          v-model="queryParams.processName"
          placeholder="请输入流程名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-card v-loading="loading"
             v-for="(item,index) in processList"
             :key="index"
             v-if="processList.length > 0 && item.processList.length > 0"
             style="margin: 10px 0">
      <div slot="header" class="clearfix">
        <span>{{ item.categoryName }}</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="(item2,index2) in item.processList" :key="index2">
          <el-card shadow="never" style="cursor:pointer">
            <div v-if="checkPermi(['workflow:process:start'])" @click.stop="handleStart(item2)">
              <el-row style="display: flex;align-items: center">
                <el-col :span="12">
                  <ImagePreview :isPreviewSrcList="false" style="width: 100px; height: 100px;border-radius: 50%" :src="item2.icon"/>
                </el-col>
                <el-col :span="12">
                  <div @click.stop="handleProcessView(item2)">流程名称：
                    <el-button style="zoom: 0.9;padding-bottom: 4px;padding-top: 4px" type="text">{{
                        item2.processName
                      }}/v{{ item2.version }}
                    </el-button>
                  </div>
                  <div>
                    流程状态:
                    <el-tag size="mini" type="success" v-if="item2.suspended">激活</el-tag>
                    <el-tag size="mini" type="warning" v-else>挂起</el-tag>
                  </div>
                  <div style="padding: 2px 0">部署时间：<span style="font-weight: 700">{{ item2.deploymentTime }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>

            <div v-else>
              <el-row>
                <el-col :span="12">
                  <ImagePreview :isPreviewSrcList="false" style="width: 100px; height: 100px;border-radius: 50%" :src="item2.icon"/>
                </el-col>
                <el-col :span="12">
                  <div @click.stop="handleProcessView(item2)">流程名称：
                    <el-button style="zoom: 0.9;padding-bottom: 4px;padding-top: 4px" type="text">{{
                        item2.processName
                      }}/v{{ item2.version }}
                    </el-button>
                  </div>
                  <div>
                    流程状态:
                    <el-tag size="mini" type="success" v-if="item2.suspended">激活</el-tag>
                    <el-tag size="mini" type="warning" v-else>挂起</el-tag>
                  </div>
                  <div style="padding: 2px 0">部署时间：<span style="font-weight: 700">{{ item2.deploymentTime }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-empty v-if="processList.length === 0" description="没有可发起流程"></el-empty>

    <!-- 流程图 -->
    <el-dialog :title="processView.title" :visible.sync="processView.open" width="70%" append-to-body>
      <process-viewer :key="`designer-${processView.index}`" :xml="processView.xmlData" :style="{height: '700px'}"/>
    </el-dialog>
  </div>
</template>

<script>
import {getBpmnXml, listProcess} from "@/api/workflow/process";
import {listAllCategory} from '@/api/workflow/category'
import ProcessViewer from '@/components/ProcessViewer'
import {checkPermi, checkRole} from "@/utils/permission";

export default {
  name: 'Create',
  components: {
    ProcessViewer
  },
  data() {
    return {
      processRow: {},

      // 遮罩层
      loading: true,
      // 查询参数
      queryParams: {
        processKey: undefined,
        processName: undefined,
        category: undefined
      },
      // 显示搜索条件
      showSearch: true,
      filterCategoryText: '',

      categoryProps: {
        label: 'categoryName',
        value: 'code'
      },
      // 流程定义表格数据
      processList: [],
      processView: {
        title: '',
        open: false,
        index: undefined,
        xmlData: "",
      }
    }
  },
  created() {
    this.getList();
  },
  methods: {
    checkPermi,
    checkRole,
    /** 查询流程定义列表 */
    getList() {
      this.loading = true;
      listAllCategory().then(response => {
        listProcess(this.queryParams).then(res => {
          this.processList = res.data;

          if (this.processList.length > 0) {
            this.processList = response.data.map((item) => {
              let data = {
                categoryName: item.categoryName,
                processList: []
              }
              res.data.forEach((i) => {
                if (item.code === i.category) {
                  data.processList.push(i)
                }
              })

              return data
            })
          }
          this.loading = false
        })
      })
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 重置按钮操作
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
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
    handleStart(row) {
      if (row.formType === '0') {
        this.$router.push({
          path: '/workflow/process/start/' + row.deploymentId,
          query: {
            definitionId: row.definitionId,
            processName: row.processName,
          }
        })
      } else {
        this.$router.push({
          path: row.formCreatePath,
          query: {
            definitionId: row.definitionId,
            processName: row.processName,
          }
        })
      }
    },
  }
}
</script>

<style scoped>

</style>
