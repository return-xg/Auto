import request from '@/utils/request'

export function listCart(query) {
  return request({
    url: '/cart/list',
    method: 'get',
    params: query
  })
}

export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    params: data
  })
}

export function updateQuantity(data) {
  return request({
    url: '/cart/update/quantity',
    method: 'put',
    params: data
  })
}

export function deleteProduct(data) {
  return request({
    url: '/cart/delete',
    method: 'delete',
    params: data
  })
}

export function deleteProducts(data) {
  return request({
    url: '/cart/delete/batch',
    method: 'delete',
    params: { userId: data.userId },
    data: data.productIds
  })
}

export function clearCart(data) {
  return request({
    url: '/cart/clear',
    method: 'delete',
    params: data
  })
}

export function updateSelected(data) {
  return request({
    url: '/cart/update/selected',
    method: 'put',
    params: data
  })
}

export function updateAllSelected(data) {
  return request({
    url: '/cart/update/selected/all',
    method: 'put',
    params: data
  })
}

export function getCartCount(query) {
  return request({
    url: '/cart/count',
    method: 'get',
    params: query
  })
}
