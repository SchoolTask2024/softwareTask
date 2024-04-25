import request from '@/utils/request'

// 查询测试列表列表
export function listTest1List(query) {
  return request({
    url: '/test1/test1List/list',
    method: 'get',
    params: query
  })
}

// 查询测试列表详细
export function getTest1List(id) {
  return request({
    url: '/test1/test1List/' + id,
    method: 'get'
  })
}

// 新增测试列表
export function addTest1List(data) {
  return request({
    url: '/test1/test1List',
    method: 'post',
    data: data
  })
}

// 修改测试列表
export function updateTest1List(data) {
  return request({
    url: '/test1/test1List',
    method: 'put',
    data: data
  })
}

// 删除测试列表
export function delTest1List(id) {
  return request({
    url: '/test1/test1List/' + id,
    method: 'delete'
  })
}
