import request from '@/utils/request'

// 查询流程列表
export function listProcess(query) {
  return request({
    url: '/flowable/workflow/process/list',
    method: 'get',
    params: query
  })
}

// 查询流程列表
export function getProcessForm(query) {
  return request({
    url: '/flowable/workflow/process/getProcessForm',
    method: 'get',
    params: query
  })
}

// 部署流程实例
export function startProcess(processDefId, data) {
  return request({
    url: '/flowable/workflow/process/start/' + processDefId,
    method: 'post',
    data: data
  })
}

// 删除流程实例
export function delProcess(ids) {
  return request({
    url: '/flowable/workflow/process/instance/' + ids,
    method: 'delete'
  })
}

// 获取流程图
export function getBpmnXml(processDefId) {
  return request({
    url: '/flowable/workflow/process/bpmnXml/' + processDefId,
    method: 'get'
  })
}

export function detailProcess(query) {
  return request({
    url: '/flowable/workflow/process/detail',
    method: 'get',
    params: query
  })
}

// 我的发起的流程
export function listOwnProcess(query) {
  return request({
    url: '/flowable/workflow/process/ownList',
    method: 'get',
    params: query
  })
}

// 我待办的流程
export function listTodoProcess(query) {
  return request({
    url: '/flowable/workflow/process/todoList',
    method: 'get',
    params: query
  })
}

// 我待签的流程
export function listClaimProcess(query) {
  return request({
    url: '/flowable/workflow/process/claimList',
    method: 'get',
    params: query
  })
}

// 我已办的流程
export function listFinishedProcess(query) {
  return request({
    url: '/flowable/workflow/process/finishedList',
    method: 'get',
    params: query
  })
}

// 查询流程抄送列表
export function listCopyProcess(query) {
  return request({
    url: '/flowable/workflow/process/copyList',
    method: 'get',
    params: query
  })
}

// 查询流程我的抄送列表
export function listMyCopyProcess(query) {
  return request({
    url: '/flowable/workflow/process/myCopyList',
    method: 'get',
    params: query
  })
}

// 取消申请
export function stopProcess(data) {
  return request({
    url: '/flowable/workflow/task/stopProcess',
    method: 'post',
    data: data
  })
}

// 钉钉流程转bpmn
export function dingdingToBpmn(data) {
  return request({
    url: '/flowable/workflow/process/ddtobpmn',
    method: 'post',
    data: data
  })
}

// 根据菜单id获取可发起列表
export function getStartList(menuId) {
  return request({
    url: '/flowable/workflow/process/getStartList/' + menuId,
    method: 'get',
  })
}

// 重新发起流程实例
export function resubmit(data) {
  return request({
    url: '/flowable/workflow/process/resubmit',
    method: 'post',
    data: data
  })
}

// 删除抄送列表
export function delCopy(copyId) {
  return request({
    url: '/flowable/workflow/process/delCopy/' + copyId,
    method: 'delete',
  })
}
