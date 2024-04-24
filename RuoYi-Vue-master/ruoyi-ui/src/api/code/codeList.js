import request from '@/utils/request'

// 查询代码列表列表
export function listCodeList(query) {
  return request({
    url: '/code/codeList/list',
    method: 'get',
    params: query
  })
}

// 查询代码列表详细
export function getCodeList(id) {
  return request({
    url: '/code/codeList/' + id,
    method: 'get'
  })
}

// 新增代码列表
export function addCodeList(data) {
  return request({
    url: '/code/codeList',
    method: 'post',
    data: data
  })
}

// 修改代码列表
export function updateCodeList(data) {
  return request({
    url: '/code/codeList',
    method: 'put',
    data: data
  })
}

// 删除代码列表
export function delCodeList(id) {
  return request({
    url: '/code/codeList/' + id,
    method: 'delete'
  })
}
