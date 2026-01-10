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
    url: '/order/create',
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

export function cancelOrder(id, reason) {
  return request({
    url: '/order/cancel',
    method: 'post',
    params: {
      orderId: id,
      reason: reason
    }
  })
}

export function confirmOrder(id) {
  return request({
    url: '/order/confirm',
    method: 'post',
    params: {
      orderId: id
    }
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
    url: '/order/pay',
    method: 'post',
    params: {
      orderId: id,
      payType: data.payType,
      success: data.success
    }
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
