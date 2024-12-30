import request from '@/utils/request'

// 查询模型模板列表
export function listWfModelTemplate(query) {
  return request({
    url: '/flowable/wfModelTemplate/list',
    method: 'get',
    params: query
  })
}

// 查询模型模板列表
export function listWfModelTemplateVo(query) {
  return request({
    url: '/flowable/wfModelTemplate/listWfModelTemplate',
    method: 'get',
    params: query
  })
}

// 查询模型模板详细
export function getWfModelTemplate(modelTemplateId) {
  return request({
    url: '/flowable/wfModelTemplate/' + modelTemplateId,
    method: 'get'
  })
}

// 新增模型模板
export function addWfModelTemplate(data) {
  return request({
    url: '/flowable/wfModelTemplate',
    method: 'post',
    data: data
  })
}

// 修改模型模板
export function updateWfModelTemplate(data) {
  return request({
    url: '/flowable/wfModelTemplate',
    method: 'put',
    data: data
  })
}

// 修改模型模板xml
export function editBpmnXml(data) {
  return request({
    url: '/flowable/wfModelTemplate/editBpmnXml',
    method: 'put',
    data: data
  })
}

// 删除模型模板
export function delWfModelTemplate(modelTemplateId) {
  return request({
    url: '/flowable/wfModelTemplate/' + modelTemplateId,
    method: 'delete'
  })
}
