import request from '@/utils/request'

// 查询代码运行列表
export function listResult(query) {
  return request({
    url: '/codeRunning/result/list',
    method: 'get',
    params: query
  })
}
export function analysis(id){
  return request({
    url: '/codeRunning/result/analysis/'+id,
    method: 'get'
  })
}

// 查询代码运行详细
export function getResult(id) {
  return request({
    url: '/codeRunning/result/' + id,
    method: 'get'
  })
}

// 新增代码运行
export function addResult(data) {
  return request({
    url: '/codeRunning/result',
    method: 'post',
    data: data
  })
}

// 修改代码运行
export function updateResult(data) {
  return request({
    url: '/codeRunning/result',
    method: 'put',
    data: data
  })
}

// 删除代码运行
export function delResult(id) {
  return request({
    url: '/codeRunning/result/' + id,
    method: 'delete'
  })
}
