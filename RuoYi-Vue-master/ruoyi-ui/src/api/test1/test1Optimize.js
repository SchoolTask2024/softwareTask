import request from '@/utils/request'

// 查询测试用例集优化列表
export function listTest1Optimize(query) {
  return request({
    url: '/test1/test1Optimize/list',
    method: 'get',
    params: query
  })
}

// 查询测试用例集优化详细
export function getTest1Optimize(id) {
  return request({
    url: '/test1/test1Optimize/' + id,
    method: 'get'
  })
}

// 新增测试用例集优化
export function addTest1Optimize(data) {
  return request({
    url: '/test1/test1Optimize',
    method: 'post',
    data: data
  })
}

// 执行测试用例集优化
export function updateTest1Optimize(data) {
  return request({
    url: '/test1/test1Optimize',
    method: 'put',
    data: data
  })
}

// 删除测试用例集优化
export function delTest1Optimize(id) {
  return request({
    url: '/test1/test1Optimize/' + id,
    method: 'delete'
  })
}
