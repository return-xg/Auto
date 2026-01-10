import request from '@/utils/request'

export function listFavorite(query) {
  return request({
    url: '/favorite/list',
    method: 'get',
    params: query
  })
}

export function addFavorite(data) {
  return request({
    url: '/favorite',
    method: 'post',
    data: data
  })
}

export function delFavorite(id) {
  return request({
    url: '/favorite/' + id,
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
