import request from '@/utils/request'

// 查询流程菜单列表
export function listWfFlowMenu(query) {
  return request({
    url: '/flowable/wfFlowMenu/list',
    method: 'get',
    params: query
  })
}

// 查询流程菜单详细
export function getWfFlowMenu(flowMenuId) {
  return request({
    url: '/flowable/wfFlowMenu/' + flowMenuId,
    method: 'get'
  })
}

// 查询流程菜单详细
export function getWfFlowMenuInfo(menuId) {
  return request({
    url: '/flowable/wfFlowMenu/getWfFlowMenuInfo/' + menuId,
    method: 'get'
  })
}

// 新增流程菜单
export function addWfFlowMenu(data) {
  return request({
    url: '/flowable/wfFlowMenu',
    method: 'post',
    data: data
  })
}

// 修改流程菜单
export function updateWfFlowMenu(data) {
  return request({
    url: '/flowable/wfFlowMenu',
    method: 'put',
    data: data
  })
}

// 删除流程菜单
export function delWfFlowMenu(flowMenuId) {
  return request({
    url: '/flowable/wfFlowMenu/' + flowMenuId,
    method: 'delete'
  })
}
