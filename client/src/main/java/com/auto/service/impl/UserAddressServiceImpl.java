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
        // 校验手机号
        validatePhone(userAddress.getPhone());
        userAddress.setCreateTime(DateUtils.getNowDate());
        userAddress.setUserId(SecurityUtils.getUserId());
        
        // 如果设置为默认地址，先取消该用户的所有默认地址
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            userAddressMapper.cancelAllDefaultAddresses(userAddress.getUserId());
        }
        
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
        // 校验手机号
        validatePhone(userAddress.getPhone());
        userAddress.setUpdateTime(DateUtils.getNowDate());
        
        // 如果设置为默认地址，先取消该用户的所有默认地址
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            UserAddress existingAddress = userAddressMapper.selectUserAddressById(userAddress.getId());
            if (existingAddress != null) {
                userAddressMapper.cancelAllDefaultAddresses(existingAddress.getUserId());
            }
        }
        
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

    /**
     * 校验手机号格式是否正确，不正确则抛出异常
     *
     * @param phone 手机号
     * @throws IllegalArgumentException 当手机号格式不正确时抛出
     */
    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        // 手机号正则表达式：1开头，第二位是3-9，总共11位数字
        String regex = "^1[3-9]\\d{9}$";
        if (!phone.matches(regex)) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
    }
}
