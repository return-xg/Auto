package com.auto.service.impl;

import java.util.List;
import com.auto.common.utils.DateUtils;
import com.auto.common.utils.SecurityUtils;
import com.auto.domain.UserAddress;
import com.auto.mapper.UserAddressMapper;
import com.auto.service.IUserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收货地址Service业务层处理
 * 
 * @author auto
 * @date 2026-01-07
 */
@Service
public class UserAddressServiceImpl implements IUserAddressService
{
    @Autowired
    private UserAddressMapper userAddressMapper;

    /**
     * 查询收货地址
     * 
     * @param id 收货地址主键
     * @return 收货地址
     */
    @Override
    public UserAddress selectUserAddressById(Long id)
    {
        return userAddressMapper.selectUserAddressById(id);
    }

    /**
     * 查询收货地址列表
     * 
     * @param userAddress 收货地址
     * @return 收货地址
     */
    @Override
    public List<UserAddress> selectUserAddressList(UserAddress userAddress)
    {
        return userAddressMapper.selectUserAddressList(userAddress);
    }

    /**
     * 新增收货地址
     * 
     * @param userAddress 收货地址
     * @return 结果
     */
    @Override
    public int insertUserAddress(UserAddress userAddress)
    {
        userAddress.setCreateTime(DateUtils.getNowDate());
        userAddress.setUserId(SecurityUtils.getUserId());
        return userAddressMapper.insertUserAddress(userAddress);
    }

    /**
     * 修改收货地址
     * 
     * @param userAddress 收货地址
     * @return 结果
     */
    @Override
    public int updateUserAddress(UserAddress userAddress)
    {
        userAddress.setUpdateTime(DateUtils.getNowDate());
        return userAddressMapper.updateUserAddress(userAddress);
    }

    /**
     * 批量删除收货地址
     * 
     * @param ids 需要删除的收货地址主键
     * @return 结果
     */
    @Override
    public int deleteUserAddressByIds(Long[] ids)
    {
        return userAddressMapper.deleteUserAddressByIds(ids);
    }

    /**
     * 删除收货地址信息
     * 
     * @param id 收货地址主键
     * @return 结果
     */
    @Override
    public int deleteUserAddressById(Long id)
    {
        return userAddressMapper.deleteUserAddressById(id);
    }
}
