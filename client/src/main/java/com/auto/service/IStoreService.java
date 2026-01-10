package com.auto.service;

import java.util.List;
import com.auto.domain.Store;

/**
 * 门店Service接口
 *
 * @author auto
 * @date 2026-01-07
 */
public interface IStoreService
{
    /**
     * 查询门店
     *
     * @param id 门店主键
     * @return 门店
     */
    public Store selectStoreById(Long id);

    /**
     * 查询门店列表
     *
     * @param store 门店
     * @return 门店集合
     */
    public List<Store> selectStoreList(Store store);

    /**
     * 新增门店
     *
     * @param store 门店
     * @return 结果
     */
    public int insertStore(Store store);

    /**
     * 修改门店
     *
     * @param store 门店
     * @return 结果
     */
    public int updateStore(Store store);

    /**
     * 批量删除门店
     *
     * @param ids 需要删除的门店主键集合
     * @return 结果
     */
    public int deleteStoreByIds(Long[] ids);

    /**
     * 删除门店信息
     *
     * @param id 门店主键
     * @return 结果
     */
    public int deleteStoreById(Long id);
}
