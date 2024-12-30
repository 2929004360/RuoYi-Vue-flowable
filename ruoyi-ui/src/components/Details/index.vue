<template>
  <div class="app-container">
    <el-descriptions :colon="false" :column="column" :title="title" border>
      <template slot="extra">
        <el-button v-if="foldUnfold" size="small" type="primary" @click="foldUnfoldClick">
          {{ foldUnfold === 'Fold' ? '收起' : '展开' }}
        </el-button>
        <el-button v-if="isPrint" size="small" type="success" @click="printClick">
          打印
        </el-button>
        <el-button v-if="isReturn" size="small" @click="returnClick">
          返回
        </el-button>
      </template>
      <el-descriptions-item v-for="(item,i) in detailsData" v-if="item.isShow" :key="i"
                            :label="item.label">
        <div v-if="item.type==='dictTag'">
          <dict-tag :effect="item.effect" :options="item.dictionaryList" :value="item.value"/>
        </div>
        <!--         el-tag 单个值-->
        <div v-else-if="item.type==='elTag'">
          <el-tag
            :effect="item.effect"
            :type="item.tagType"
          >
            {{ item.value }}
          </el-tag>
        </div>
        <!--        el-tag 多个值-->
        <div v-else-if="item.type==='multiElTag'">
          <el-tag
            v-for="(tagItem,index) in item.value"
            :key="index"
            :effect="item.effect"
            :type="item.tagType"
            style="margin: 0 4px;"
          >
            {{ tagItem }}
          </el-tag>
          <!--          <el-tag style="margin: 0 4px;"  :key="index">{{item.suraName}}</el-tag>-->

        </div>
        <div v-else-if="item.type==='imagePreview'">
          <image-preview v-if="item.value" :height="80" :src="item.value" :width="80"/>
        </div>
        <div v-else-if="item.type==='annex'">
          <!--          文件名  name-->
          <!--          文件路径   url-->
          <!--          {{ item }}-->
          <div v-for="(item1,i) in item.value" :key="i">
            {{ item1.name }}
            <el-link :underline="false" type="primary" @click="fileDownload(item1)">文件下载</el-link>
            <el-link :underline="false" style="margin-left: 10px" type="primary" @click="preView(item1)">
              文件预览
            </el-link>

          </div>
        </div>

        <div v-else-if="item.type==='signFile'">
          <!--          文件名  name-->
          <!--          文件路径   url-->
          <div v-for="(item1,i) in item.value" :key="i">
            {{ item1.name }}
            <el-link :underline="false" type="primary" @click="fileDownload(item1)">文件下载</el-link>
            <el-link :underline="false" style="margin-left: 10px" type="primary" @click="preView(item1)">
              文件预览
            </el-link>

          </div>
        </div>
        <div v-else-if="item.type === 'html'">
          <span v-html="item.value"></span>
        </div>
        <div v-else-if="item.type === 'colourView'">
          <el-tooltip effect="dark" :content="item.value" placement="top">
            <div style="width: 20px;height: 20px;display: inline-block;cursor:pointer;border-radius: 4px"
                 :style="'background:'+item.value"/>
          </el-tooltip>
        </div>
        <div v-else-if="item.type === 'dateType' && item.value">
          {{ $moment(item.value).format('YYYY-MM-DD HH:mm:ss') }}
        </div>
        <div v-else class="bold">
          {{ item.value }}
        </div>
      </el-descriptions-item>
    </el-descriptions>
  </div>

</template>
<script>
export default {
  props: {
    row: {
      type: Object
    },
    detailsData: {
      type: Array,
      default() {
        return 3
      }
    },
    column: {
      type: Number
    },
    foldUnfold: {
      type: String,
    },
    title: {
      type: String,
    },
    isReturn: {
      type: Boolean,
      default: false
    },
    returnUrl: {
      type: String,
    },
    isPrint: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {}
  },
  created() {
  },
  methods: {
    // 文件下载
    fileDownload(row) {
      // this.download('/file/download', {
      //   filePath: row.url
      // }, `${row.name}`)
    },
    preView(file) {
      // let Base64 = require('js-base64').Base64
      // window.open(this.$domPreviewUrl + encodeURIComponent(Base64.encode(file.url)))
    },
    foldUnfoldClick() {
      if (this.foldUnfold === 'Fold') {
        this.$emit('foldUnfold', 'Unfold')
      } else {
        this.$emit('foldUnfold', 'Fold')
      }
    },
    returnClick() {
      const obj = {path: this.returnUrl};
      this.$tab.closeOpenPage(obj);
    },
    printClick() {
      this.$emit('printClick')
    }
  },
  watch: {}
}
</script>
<style lang="scss" scoped>
.bold {
  font-weight: bold;
}
</style>
