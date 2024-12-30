import request from '@/utils/request'

// 加签任务
export function addSignTask(data) {
  return request({
    url: '/flowable/workflow/task/addSign',
    method: 'post',
    data: data
  })
}

// 多实例加签任务
export function multiInstanceAddSignTask(data) {
  return request({
    url: '/flowable/workflow/task/multiInstanceAddSign',
    method: 'post',
    data: data
  })
}


// 完成任务
export function complete(data) {
  return request({
    url: '/flowable/workflow/task/complete',
    method: 'post',
    data: data
  })
}

// 委派任务
export function delegate(data) {
  return request({
    url: '/flowable/workflow/task/delegate',
    method: 'post',
    data: data
  })
}

// 转办任务
export function transfer(data) {
  return request({
    url: '/flowable/workflow/task/transfer',
    method: 'post',
    data: data
  })
}

// 退回任务
export function returnTask(data) {
  return request({
    url: '/flowable/workflow/task/return',
    method: 'post',
    data: data
  })
}

// 驳回任务
export function rejectTask(data) {
  return request({
    url: '/flowable/workflow/task/reject',
    method: 'post',
    data: data
  })
}

// 拒绝任务
export function refuseTask(data) {
  return request({
    url: '/flowable/workflow/task/refuse',
    method: 'post',
    data: data
  })
}

// 签收任务
export function claimTask(data) {
  return request({
    url: '/flowable/workflow/task/claim',
    method: 'post',
    data: data
  })
}

// 可退回任务列表
export function returnList(data) {
  return request({
    url: '/flowable/workflow/task/returnList',
    method: 'post',
    data: data
  })
}

// 跳转任务节点列表
export function userTaskList(data) {
  return request({
    url: '/flowable/workflow/task/userTask',
    method: 'post',
    data: data
  })
}

// 跳转任务
export function jumpTask(data) {
  return request({
    url: '/flowable/workflow/task/jump',
    method: 'post',
    data: data
  })
}
