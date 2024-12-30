export default {
  start: {
    type: "start",
    content: "所有人",
    properties: { title: '发起人', initiator: 'ALL', formKey: "" }
  },
  approver: {
    type: "approver",
    content: "请设置审批人",
    properties: { title: '审批人' }
  },
  copy:{
    type: 'copy',
    content: '发起人自选',
    properties: {
      title: '抄送人',
      members: [],
      userOptional: true
    }
  },
  condition: {
    type: "condition",
    content: "请设置条件表达式",
    properties: { title: '条件', conditions: [], initiator: null }
  },
  concurrent: {
    type: "concurrent",
    content: "并行任务(同时进行)",
    properties: { title: '分支'}
  },
  delay: {
    type: "delay",
    content: "等待0分钟",
    properties: {
      title: '延时处理',
      type: "FIXED", //延时类型 FIXED:到达当前节点后延时固定时长 、AUTO:延时到 dateTime设置的时间
      time: 0, //延时时间
      unit: "M", //时间单位 D天 H小时 M分钟
      dateTime: "" //如果当天没有超过设置的此时间点，就延时到这个指定的时间，到了就直接跳过不延时
    }
  },
  trigger: {
    type: "trigger",
    content: "请设置触发器",
    properties: { title: '触发器' }
  },
  branch: { type: "branch", content: "", properties: {} },
  empty: { type: "empty", content: "", properties: {} }
}
