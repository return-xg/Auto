import request from '@/utils/request'

export function listFavorite(query) {
  return request({
    url: '/favorite/list',
    method: 'get',
    params: query
  })
}

export function addFavorite(productId) {
  return request({
    url: '/favorite/add',
    method: 'post',
    params: { productId }
  })
}

export function delFavorite(productId) {
  return request({
    url: '/favorite/remove/' + productId,
    method: 'delete'
  })
}

export function checkFavorite(productId) {
  return request({
    url: '/favorite/check',
    method: 'get',
    params: { productId }
  })
}
