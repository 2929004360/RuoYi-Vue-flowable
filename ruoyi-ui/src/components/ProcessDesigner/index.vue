<template>
  <div class="process-design" :style="'display: flex; height:' + height">
    <bpmn-process-designer
      :options="{
        taskResizingEnabled: true,
        eventResizingEnabled: true,
        minimap: {
          open: true
        }
      }"
      v-model="bpmnXml"
      v-bind="controlForm"
      :isSave="isSave"
      keyboard
      :simulation="true"
      ref="processDesigner"
      :events="[
        'element.click',
        'connection.added',
        'connection.removed',
        'connection.changed'
      ]"
      @element-click="elementClick"
      @init-finished="initModeler"
      @event="handlerEvent"
      @save="onSaveProcess"
    />
    <bmpn-process-penal :bpmn-modeler="modeler" :controlForm="controlForm"
                        :prefix="controlForm.prefix"
                        class="process-panel"/>

  </div>
</template>

<script>
import Vue from 'vue';
import '@/plugins/package/theme/index.scss';
import {BmpnProcessPenal, BpmnProcessDesigner} from '@/plugins/package/index';
// 自定义元素选中时的弹出菜单（修改 默认任务 为 用户任务）
import CustomContentPadProvider from '@/plugins/package/designer/plugins/content-pad';
// 自定义左侧菜单（修改 默认任务 为 用户任务）
import CustomPaletteProvider from '@/plugins/package/designer/plugins/palette';
import {vuePlugin} from '@/plugins/package/highlight';
import 'highlight.js/styles/atom-one-dark-reasonable.css';

Vue.use(vuePlugin);

export default {
  name: 'ProcessDesigner',
  props: {
    isSave: {
      type: Boolean,
      required: true
    },
    value: {
      type: String,
      required: true
    },
  },
  components: {
    BpmnProcessDesigner,
    BmpnProcessPenal
  },
  computed: {
    bpmnXml: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit("input", val);
      }
    }
  },
  data() {
    return {
      height: document.documentElement.clientHeight - 94.5 + "px;",
      xmlString: this.bpmnXml,
      modeler: null,
      controlForm: {
        processId: undefined,
        processName: undefined,
        simulation: false,
        labelEditing: false,
        labelVisible: false,
        prefix: 'flowable',
        headerButtonSize: 'small',
        additionalModel: [
          CustomContentPadProvider,
          CustomPaletteProvider,
        ]
      }
    }
  },
  methods: {
    elementClick(element) {
      this.element = element;
    },
    initModeler(modeler) {
      setTimeout(() => {

        this.modeler = modeler;
      }, 10);
    },
    handlerEvent(eventName, element) {
    },
    onSaveProcess(saveData) {
      this.$emit('save', saveData);
    }
  }
}
</script>

<style lang="scss">
body {
  overflow: auto !important;
  margin: 0;
  box-sizing: border-box;
}

body,
body * {
  /* 滚动条 */
  &::-webkit-scrollbar-track-piece {
    background-color: #fff; /*滚动条的背景颜色*/
    -webkit-border-radius: 0; /*滚动条的圆角宽度*/
  }

  &::-webkit-scrollbar {
    width: 10px; /*滚动条的宽度*/
    height: 8px; /*滚动条的高度*/
  }

  &::-webkit-scrollbar-thumb:vertical {
    /*垂直滚动条的样式*/
    height: 50px;
    background-color: rgba(153, 153, 153, 0.5);
    -webkit-border-radius: 4px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
  }

  &::-webkit-scrollbar-thumb {
    /*滚动条的hover样式*/
    background-color: rgba(159, 159, 159, 0.3);
    -webkit-border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb:hover {
    /*滚动条的hover样式*/
    background-color: rgba(159, 159, 159, 0.5);
    -webkit-border-radius: 4px;
  }
}
</style>
