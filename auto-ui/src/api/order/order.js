import request from '@/utils/request'

export function listOrder(query) {
  return request({
    url: '/order/list',
    method: 'get',
    params: query
  })
}

export function getOrder(id) {
  return request({
    url: '/order/' + id,
    method: 'get'
  })
}

export function createOrder(data) {
  return request({
    url: '/order',
    method: 'post',
    data: data
  })
}

export function updateOrder(data) {
  return request({
    url: '/order',
    method: 'put',
    data: data
  })
}

export function cancelOrder(id) {
  return request({
    url: '/order/' + id + '/cancel',
    method: 'put'
  })
}

export function confirmOrder(id) {
  return request({
    url: '/order/' + id + '/confirm',
    method: 'put'
  })
}

export function deleteOrder(id) {
  return request({
    url: '/order/' + id,
    method: 'delete'
  })
}

export function payOrder(id, data) {
  return request({
    url: '/order/' + id + '/pay',
    method: 'post',
    data: data
  })
}

export function refundOrder(id, data) {
  return request({
    url: '/order/' + id + '/refund',
    method: 'post',
    data: data
  })
}

export function getLogistics(id) {
  return request({
    url: '/order/' + id + '/logistics',
    method: 'get'
  })
}
