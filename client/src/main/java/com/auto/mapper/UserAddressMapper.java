package com.auto.mapper;

import com.auto.domain.UserAddress;

import java.util.List;

/**
 * 收货地址Mapper接口
 * 
 * @author auto
 * @date 2026-01-07
 */
public interface UserAddressMapper 
{
    /**
     * 查询收货地址
     * 
     * @param id 收货地址主键
     * @return 收货地址
     */
    public UserAddress selectUserAddressById(Long id);

    /**
     * 查询收货地址列表
     * 
     * @param userAddress 收货地址
     * @return 收货地址集合
     */
    public List<UserAddress> selectUserAddressList(UserAddress userAddress);

    /**
     * 新增收货地址
     * 
     * @param userAddress 收货地址
     * @return 结果
     */
    public int insertUserAddress(UserAddress userAddress);

    /**
     * 修改收货地址
     * 
     * @param userAddress 收货地址
     * @return 结果
     */
    public int updateUserAddress(UserAddress userAddress);

    /**
     * 删除收货地址
     * 
     * @param id 收货地址主键
     * @return 结果
     */
    public int deleteUserAddressById(Long id);

    /**
     * 批量删除收货地址
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserAddressByIds(Long[] ids);
}
