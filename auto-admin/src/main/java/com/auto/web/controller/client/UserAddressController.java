package com.auto.web.controller.client;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.auto.domain.UserAddress;
import com.auto.service.IUserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auto.common.annotation.Log;
import com.auto.common.core.controller.BaseController;
import com.auto.common.core.domain.AjaxResult;
import com.auto.common.enums.BusinessType;
import com.auto.common.utils.poi.ExcelUtil;
import com.auto.common.core.page.TableDataInfo;

/**
 * 收货地址Controller
 * 
 * @author auto
 * @date 2026-01-07
 */
@RestController
@RequestMapping("/address/address")
public class UserAddressController extends BaseController
{
    @Autowired
    private IUserAddressService userAddressService;

    /**
     * 查询收货地址列表
     */
    @GetMapping("/list")
    public TableDataInfo list(UserAddress userAddress)
    {
        userAddress.setUserId(getUserId());
        startPage();
        List<UserAddress> list = userAddressService.selectUserAddressList(userAddress);
        return getDataTable(list);
    }

    /**
     * 导出收货地址列表
     */
    @Log(title = "收货地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserAddress userAddress)
    {
        List<UserAddress> list = userAddressService.selectUserAddressList(userAddress);
        ExcelUtil<UserAddress> util = new ExcelUtil<UserAddress>(UserAddress.class);
        util.exportExcel(response, list, "收货地址数据");
    }

    /**
     * 获取收货地址详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        UserAddress address = userAddressService.selectUserAddressById(id);
        if (address == null) {
            return AjaxResult.error("地址不存在");
        }
        if (!address.getUserId().equals(getUserId())) {
            return AjaxResult.error("无权查看其他用户的地址");
        }
        return success(address);
    }

    /**
     * 新增收货地址
     */
    @Log(title = "收货地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserAddress userAddress)
    {
        userAddress.setUserId(getUserId());
        return toAjax(userAddressService.insertUserAddress(userAddress));
    }

    /**
     * 修改收货地址
     */
    @Log(title = "收货地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserAddress userAddress)
    {
        UserAddress existAddress = userAddressService.selectUserAddressById(userAddress.getId());
        if (existAddress == null) {
            return AjaxResult.error("地址不存在");
        }
        if (!existAddress.getUserId().equals(getUserId())) {
            return AjaxResult.error("无权修改其他用户的地址");
        }
        userAddress.setUserId(getUserId());
        return toAjax(userAddressService.updateUserAddress(userAddress));
    }

    /**
     * 删除收货地址
     */
    @Log(title = "收货地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for (Long id : ids) {
            UserAddress existAddress = userAddressService.selectUserAddressById(id);
            if (existAddress == null) {
                return AjaxResult.error("地址不存在");
            }
            if (!existAddress.getUserId().equals(getUserId())) {
                return AjaxResult.error("无权删除其他用户的地址");
            }
        }
        return toAjax(userAddressService.deleteUserAddressByIds(ids));
    }
}
