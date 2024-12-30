import request from '@/utils/request'

// 查询请假管理列表
export function listLeave(query) {
  return request({
    url: '/work/leave/list',
    method: 'get',
    params: query
  })
}

// 查询请假管理详细
export function getLeave(leaveId) {
  return request({
    url: '/work/leave/' + leaveId,
    method: 'get'
  })
}

// 新增请假管理
export function addLeave(data) {
  return request({
    url: '/work/leave',
    method: 'post',
    data: data
  })
}

// 修改请假管理
export function updateLeave(data) {
  return request({
    url: '/work/leave',
    method: 'put',
    data: data
  })
}

// 删除请假管理
export function delLeave(leaveId) {
  return request({
    url: '/work/leave/' + leaveId,
    method: 'delete'
  })
}

// 提交审核
export function submit(leaveId) {
  return request({
    url: '/work/leave/submit/' + leaveId,
    method: 'put',
  })
}

// 撤销审核
export function revoke(leaveId) {
  return request({
    url: '/work/leave/revoke/' + leaveId,
    method: 'put',
  })
}

// 重新申请
export function reapply(leaveId) {
  return request({
    url: '/work/leave/reapply/' + leaveId,
    method: 'put',
  })
}
