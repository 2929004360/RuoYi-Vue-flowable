<script>
import { NodeUtils } from "./util.js";
const isCondition = data => data.type === "condition";
const isConcurrent = data => data.type === "concurrent";
const notEmptyArray = arr => Array.isArray(arr) && arr.length > 0;
const hasBranch = data => notEmptyArray(data.conditionNodes);
const hasConditionBranch = data => notEmptyArray(data.conditionNodes);
const hasConcurrentBranch = data => notEmptyArray(data.concurrentNodes);
const stopPro = ev => ev.stopPropagation();

function createNormalCard(ctx, conf, h) {
  const classList = ['flow-path-card']
  const afterTrue = (isTrue, name) => (isTrue && classList.push(name), isTrue)
  const isStartNode = afterTrue(NodeUtils.isStartNode(conf), 'start-node')
  const isApprNode = afterTrue(NodeUtils.isApproverNode(conf), 'approver')
  const isCopyNode = afterTrue(NodeUtils.isCopyNode(conf), 'copy')
  const isDelayNode = afterTrue(NodeUtils.isDelayNode(conf), 'delay')
  const isTriggerNode = afterTrue(NodeUtils.isTriggerNode(conf), 'trigger')

  return (
    <section class={classList.join(' ')} onClick={this.eventLancher.bind(ctx, "edit", conf)} >
      <header class="header">
        <div class="title-box" style="height: 100%;width:190px;">
          {isApprNode && (
            <i class="iconfont iconshenpi" style="font-size:12px;color:white;margin-right:4px;"></i>
          )}
          {isCopyNode && (
            <i class="el-icon-s-promotion" style="font-size:12px;color:white;margin-right:4px;"></i>
          )}
          {isDelayNode && (
            <i class="el-icon-s-promotion" style="font-size:12px;color:white;margin-right:4px;"></i>
          )}
          <span class="title-text">{conf.properties.title}</span>
          {!isStartNode && (
            <input vModel_trim={conf.properties.title} class="title-input" style="margin-top:3px;" onClick={stopPro} />
          )}
        </div>
        <div class="actions" style="margin-right:4px;">
          <i class="el-icon-close icon" onClick={this.eventLancher.bind(ctx, "deleteNode", conf, ctx.data)} ></i>
        </div>
      </header>
      <div class="body">
        <span class="text">{conf.content}</span>
        <div class="icon-wrapper right">
          <i class="el-icon-arrow-right icon right-arrow"></i>
        </div>
      </div>
    </section>
  );
}
// arg = ctx, data, h
/* 各个节点类型的构造函数
    modify by nbacheng 2023-11-01
*/
const createFunc = (...arg) => createNormalCard.call(arg[0], ...arg)
let nodes = {
  start: createFunc,
  approver: createFunc,
  copy: createFunc,
  delay: createFunc,
  trigger: createFunc,
  empty: _ => '',
  condition: function(ctx, conf, h) {
      // <i
      //    class="el-icon-document-copy icon"
      //    onClick={this.eventLancher.bind(ctx, "copyNode", conf, ctx.data)}
      //  ></i>
    return (
      <section
        class="flow-path-card condition"
        onClick={this.eventLancher.bind(ctx, "edit", conf)}
      >
        <header class="header">
          <div class="title-box" style="height: 20px;width:160px;">
            <span class="title-text">{conf.properties.title}</span>
            {
              <input
                vModel_trim={conf.properties.title}
                class="title-input"
                style="margin-top:1px;"
                onClick={stopPro}
              />
            }
          </div>
          <span class="priority">优先级{conf.properties.priority + 1}</span>
          <div class="actions">

            <i
              class="el-icon-close icon"
              onClick={this.eventLancher.bind(
                ctx,
                "deleteNode",
                conf,
                ctx.data
              )}
            ></i>
          </div>
        </header>
        <div class="body">
          <pre class="text" >{conf.content}</pre>
        </div>
        <div
          class="icon-wrapper left"
          onClick={ctx.eventLancher.bind(
            ctx,
            "increasePriority",
            conf,
            ctx.data
          )}
        >
          <i class="el-icon-arrow-left icon left-arrow"></i>
        </div>
        <div
          class="icon-wrapper right"
          onClick={ctx.eventLancher.bind(
            ctx,
            "decreasePriority",
            conf,
            ctx.data
          )}
        >
          <i class="el-icon-arrow-right icon right-arrow"></i>
        </div>
      </section>
    );
  },
  concurrent: function(ctx, conf, h) {
      // <i
      //    class="el-icon-document-copy icon"
      //    onClick={this.eventLancher.bind(ctx, "copyNode", conf, ctx.data)}
      //  ></i>
    return (
      <section
        class="flow-path-card concurrent"
        onClick={this.eventLancher.bind(ctx, "edit", conf)}
      >
        <header class="header">
          <div class="title-box" style="height: 20px;width:160px;">
            <span class="title-text">{conf.properties.title}</span>
            {
              <input
                vModel_trim={conf.properties.title}
                class="title-input"
                style="margin-top:1px;"
                onClick={stopPro}
              />
            }
          </div>
          <div class="actions">

            <i
              class="el-icon-close icon"
              onClick={this.eventLancher.bind(
                ctx,
                "deleteNode",
                conf,
                ctx.data
              )}
            ></i>
          </div>
        </header>
        <div class="body">
          <pre class="text" >{conf.content}</pre>
        </div>
        <div
          class="icon-wrapper left"
          onClick={ctx.eventLancher.bind(
            ctx,
            "increasePriority",
            conf,
            ctx.data
          )}
        >
          <i class="el-icon-arrow-left icon left-arrow"></i>
        </div>
        <div
          class="icon-wrapper right"
          onClick={ctx.eventLancher.bind(
            ctx,
            "decreasePriority",
            conf,
            ctx.data
          )}
        >
          <i class="el-icon-arrow-right icon right-arrow"></i>
        </div>
      </section>
    );
  }
};

/* 添加节点按钮类型
    modify by nbacheng 2023-11-01
*/
function addNodeButton(ctx, data, h, isBranch = false) {
  // 只有非条件节点和条件分支树下面的那个按钮 才能添加新分支树
  console.log("addNodeButton data,isBranch",data,isBranch);
  //let couldAddBranch = !hasConditionBranch(data) || isBranch;
  let isEmpty = data.type === "empty";
  if (isEmpty && !isBranch) {
    return "";
  }
  return (
    <div class="add-node-btn-box flex  justify-center">
      <div class="add-node-btn">
        <el-popover placement="right" trigger="click" width="400">
          <div class="condition-box">
            <div>
              <div class="condition-icon" onClick={ctx.eventLancher.bind( ctx, "addApprovalNode",  data, isBranch )} >
                <i class="el-icon-s-check" style="font-size: 20px"></i>
              </div>
              审批人
            </div>

            <div>
              <div class="condition-icon" onClick={ctx.eventLancher.bind( ctx, "addCopyNode",  data, isBranch )} >
                <i class="el-icon-s-promotion" style="vertical-align: middle;font-size: 20px"></i>
              </div>
              抄送人
            </div>

            <div>
              <div class="condition-icon" onClick={this.eventLancher.bind(ctx, "appendBranch", data, isBranch)}>
                <i class="el-icon-share" style="color:rgb(21, 188, 131);font-size: 20px"></i>
              </div>
              条件分支
            </div>

            <div>
              <div class="condition-icon" onClick={this.eventLancher.bind(ctx, "addConcurrentBranch", data, isBranch)}>
                <i class="el-icon-s-operation" style="color:rgb(21, 188, 131);font-size: 20px"></i>
              </div>
              并行分支
            </div>

            <div>
              <div class="condition-icon" onClick={this.eventLancher.bind(ctx, "addDelayNode", data, isBranch)}>
                <i class="el-icon-time" style="color:rgb(21, 188, 131);font-size: 20px"></i>
              </div>
              延迟等待
            </div>

            <div>
              <div class="condition-icon" onClick={this.eventLancher.bind(ctx, "addTriggerNode", data, isBranch)}>
                <i class="el-icon-set-up" style="color:rgb(21, 188, 131);font-size: 20px"></i>
              </div>
              触发器
            </div>
          </div>

          <button class="btn" type="button" slot="reference">
            <i class="el-icon-plus icon"></i>
          </button>
        </el-popover>
      </div>
    </div>
  );
}

/* 构造节点
    modify by nbacheng 2023-11-02
*/
function NodeFactory(ctx, data, h) {
  if (!data) return
  console.log("NodeFactory data",data)
  const showErrorTip = ctx.verifyMode && NodeUtils.checkNode(data) === false
  const showChildErrorTip = ctx.verifyMode && (NodeUtils.isConditionNode(data) || NodeUtils.isConcurrentNode(data)) && NodeUtils.checkChildNode(data) === false
  const showErrorDelayTip = ctx.verifyMode && NodeUtils.checkDelayNode(data) === false
  let res = [], branchNode = "", selfNode = null ;
    if (showErrorTip) { //通用节点的错误检查做特殊处理
    selfNode = (
      <div class="node-wrap">
        <div class={`node-wrap-box ${data.type} ${ showErrorTip ? 'error' : ''}` }>
          <el-tooltip content="未设置条件表达式" placement="top" effect="dark">
            <div class="error-tip" onClick={this.eventLancher.bind(ctx, "edit", data)}>!!!</div>
          </el-tooltip>
          {nodes[data.type].call(ctx, ctx, data, h)}
          {addNodeButton.call(ctx, ctx, data, h)}
        </div>
      </div>
    );
    } else if (showChildErrorTip) { //对并发与条件节点的错误检查做特殊处理
    selfNode = (
        <div class="node-wrap">
          <div class={`node-wrap-box ${data.type} ${ showChildErrorTip  ? 'error' : ''}` }>
            <el-tooltip content="未设置审批人员节点" placement="top" effect="dark">
              <div class="error-tip" onClick={ctx.eventLancher.bind( ctx, "addApprovalNode",  data, false )}>!!!</div>
            </el-tooltip>
            {nodes[data.type].call(ctx, ctx, data, h)}
            {addNodeButton.call(ctx, ctx, data, h)}
          </div>
        </div>
      );
    } else if (showErrorDelayTip) { //对延时节点的错误检查做特殊处理
    selfNode = (
        <div class="node-wrap">
          <div class={`node-wrap-box ${data.type} ${ showErrorDelayTip  ? 'error' : ''}` }>
            <el-tooltip content="未设置延时时间" placement="top" effect="dark">
              <div class="error-tip" onClick={this.eventLancher.bind(ctx, "edit", data)}>!!!</div>
            </el-tooltip>
            {nodes[data.type].call(ctx, ctx, data, h)}
            {addNodeButton.call(ctx, ctx, data, h)}
          </div>
        </div>
      );
    } else { //正常节点的显示
      selfNode = (
          <div class="node-wrap">
            <div class={`node-wrap-box ${data.type} ${''}` }>
              {nodes[data.type].call(ctx, ctx, data, h)}
              {addNodeButton.call(ctx, ctx, data, h)}
            </div>
          </div>
        );
    }

  if (hasConditionBranch(data)) {
    // 如果节点是数组 而且是条件分支 添加分支样式包裹
    // {data.childNode && NodeFactory.call(ctx, ctx, data.childNode, h)}
    console.log("hasConditionBranch data",data)
    branchNode = (
      <div class="branch-wrap">
        <div class="branch-box-wrap">
          <div class="branch-box  flex justify-center relative">
            <button
              class="btn"
              onClick={this.eventLancher.bind(ctx, "appendConditionNode", data)}
            >
              添加条件
            </button>
            {data.conditionNodes.map(d => NodeFactory.call(ctx, ctx, d, h))}
          </div>
        </div>
        {addNodeButton.call(ctx, ctx, data, h, true)}
      </div>
    );
  }

  if (isCondition(data)) {
    console.log("isCondition data",data)
    return (
      <div class="col-box">
        <div class="center-line"></div>
        <div class="top-cover-line"></div>
        <div class="bottom-cover-line"></div>
        {selfNode}
        {branchNode}
        {NodeFactory.call(ctx, ctx, data.childNode, h)}
      </div>
    );
  }

  if (hasConcurrentBranch(data)) {
    console.log("hasConcurrentBranch data",data)
    // 如果节点是数组 而且是并行分支 添加分支样式包裹
    // {data.childNode && NodeFactory.call(ctx, ctx, data.childNode, h)}
    branchNode = (
      <div class="branch-wrap">
        <div class="branch-box-wrap">
          <div class="branch-box  flex justify-center relative">
            <button
              class="btn"
              onClick={this.eventLancher.bind(ctx, "addConcurrentNode", data)}
            >
              添加并行
            </button>
            {data.concurrentNodes.map(d => NodeFactory.call(ctx, ctx, d, h))}
          </div>
        </div>
        {addNodeButton.call(ctx, ctx, data, h, true)}
      </div>
    );
  }

  if (isConcurrent(data)) {
    console.log("isConcurrent data",data)
    return (
      <div class="col-box">
        <div class="center-line"></div>
        <div class="top-cover-line"></div>
        <div class="bottom-cover-line"></div>
        {selfNode}
        {branchNode}
        {NodeFactory.call(ctx, ctx, data.childNode, h)}
      </div>
    );
  }
  res.push(selfNode);
  branchNode && res.push(branchNode);
  data.childNode && res.push(NodeFactory.call(ctx, ctx, data.childNode, h));
  return res;
}

function addEndNode(h) {
  return <section class="end-node">流程结束</section>;
}

export default {
  props: {
    data: { type: Object, required: true },
    // 点击发布时需要校验节点数据完整性 此时打开校验模式
    verifyMode: {type: Boolean, default: true},
  },
  watch:{

  },
  methods: {
    /**
     *事件触发器 统筹本组件所有事件并转发到父组件中
     * @param { Object } 包含event（事件名）和args（事件参数）两个参数
     */
    eventLancher(event, ...args) {
      // args.slice(0,-1) vue 会注入MouseEvent到最后一个参数 去除事件对象
      let param = { event, args: args.slice(0, -1) };
      this.$emit("emits", param);
    }
  },
  render(h) {
    return (
      <div style="display: inline-flex; flex-direction: column; align-items: center;">
        {this.data && NodeFactory.call(this, this, this.data, h)}
        {addEndNode(h)}
      </div>
    );
  }
};
</script>

<style lang="stylus" scoped>
@import 'index.styl';
</style>
