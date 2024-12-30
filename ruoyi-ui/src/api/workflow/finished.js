import request from '@/utils/request'

// 收回任务
export function recallProcess(data) {
  return request({
    url: '/flowable/workflow/task/recallProcess',
    method: 'post',
    data: data
  })
}

// 撤回任务
export function revokeProcess(data) {
  return request({
    url: '/flowable/workflow/task/revokeProcess',
    method: 'post',
    data: data
  })
}
