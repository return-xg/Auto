package com.auto.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auto.common.utils.DateUtils;
import com.auto.domain.Store;
import com.auto.mapper.StoreMapper;
import com.auto.service.IStoreService;

/**
 * 门店Service业务层处理
 *
 * @author auto
 * @date 2026-01-07
 */
@Service
public class StoreServiceImpl implements IStoreService
{
    @Autowired
    private StoreMapper storeMapper;

    /**
     * 查询门店
     */
    @Override
    public Store selectStoreById(Long id)
    {
        return storeMapper.selectStoreById(id);
    }

    /**
     * 查询门店列表
     */
    @Override
    public List<Store> selectStoreList(Store store)
    {
        return storeMapper.selectStoreList(store);
    }

    /**
     * 新增门店
     */
    @Override
    public int insertStore(Store store)
    {
        store.setCreateTime(DateUtils.getNowDate());
        return storeMapper.insertStore(store);
    }

    /**
     * 修改门店
     */
    @Override
    public int updateStore(Store store)
    {
        store.setUpdateTime(DateUtils.getNowDate());
        return storeMapper.updateStore(store);
    }

    /**
     * 批量删除门店
     */
    @Override
    public int deleteStoreByIds(Long[] ids)
    {
        return storeMapper.deleteStoreByIds(ids);
    }

    /**
     * 删除门店信息
     */
    @Override
    public int deleteStoreById(Long id)
    {
        return storeMapper.deleteStoreById(id);
    }
}
