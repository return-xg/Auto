package com.auto.mapper;

import java.util.List;
import com.auto.domain.Store;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店Mapper接口
 *
 * @author auto
 * @date 2026-01-07
 */
@Mapper
public interface StoreMapper
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
     * 删除门店
     *
     * @param id 门店主键
     * @return 结果
     */
    public int deleteStoreById(Long id);

    /**
     * 批量删除门店
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStoreByIds(Long[] ids);
}
