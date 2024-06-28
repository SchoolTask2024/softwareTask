import request from '@/utils/request'

// 执行测试用例集优化
export function updateTest1Optimize(data) {
  return request({
    url: '/test1/test1List/test1Optimize',
    method: 'put',
    data: data
  })
}

