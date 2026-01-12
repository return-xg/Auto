import request from '@/utils/request'

// 查询商品列表
export function listProduct(query) {
  return request({
    url: '/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getProduct(id) {
  return request({
    url: '/product/' + id,
    method: 'get'
  })
}

// 多维度搜索商品
export function searchProduct(query) {
  return request({
    url: '/product/search',
    method: 'get',
    params: query
  })
}

// 获取筛选选项
export function getFilterOptions(categoryId) {
  return request({
    url: '/product/filter-options',
    method: 'get',
    params: { categoryId }
  })
}

// 新增商品
export function addProduct(data) {
  return request({
    url: '/product',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 修改商品
export function updateProduct(data) {
  return request({
    url: '/product',
    method: 'put',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除商品
export function delProduct(ids) {
  return request({
    url: '/product/' + ids,
    method: 'delete'
  })
}

// 商品上架
export function putOnSale(id) {
  return request({
    url: '/product/' + id + '/putOnSale',
    method: 'put'
  })
}

// 商品下架
export function putOffSale(id) {
  return request({
    url: '/product/' + id + '/putOffSale',
    method: 'put'
  })
}

// 批量商品上架
export function batchPutOnSale(ids) {
  return request({
    url: '/product/batchPutOnSale',
    method: 'put',
    data: ids
  })
}

// 批量商品下架
export function batchPutOffSale(ids) {
  return request({
    url: '/product/batchPutOffSale',
    method: 'put',
    data: ids
  })
}

// 修改商品库存
export function updateStock(id, stock) {
  return request({
    url: '/product/' + id + '/updateStock',
    method: 'put',
    params: { stock }
  })
}

// 设置库存预警值
export function updateWarnStock(id, warnStock) {
  return request({
    url: '/product/' + id + '/updateWarnStock',
    method: 'put',
    params: { warnStock }
  })
}
