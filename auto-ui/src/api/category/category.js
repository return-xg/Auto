import request from '@/utils/request'

export function listCategory(query) {
  return request({
    url: '/category/list',
    method: 'get',
    params: query
  })
}

export function getCategory(id) {
  return request({
    url: `/category/${id}`,
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    url: '/category',
    method: 'post',
    data: data
  })
}

export function updateCategory(data) {
  return request({
    url: '/category',
    method: 'put',
    data: data
  })
}

export function deleteCategory(ids) {
  const idsStr = Array.isArray(ids) ? ids.join(',') : ids
  return request({
    url: `/category/${idsStr}`,
    method: 'delete'
  })
}