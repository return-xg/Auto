package com.auto.web.controller.client;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.auto.common.core.controller.BaseController;
import com.auto.common.core.domain.AjaxResult;
import com.auto.common.core.page.TableDataInfo;
import com.auto.common.utils.SecurityUtils;
import com.auto.domain.Favorite;
import com.auto.service.IFavoriteService;

/**
 * 收藏Controller
 *
 * @author auto
 * @date 2026-01-07
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController extends BaseController
{
    @Autowired
    private IFavoriteService favoriteService;

    /**
     * 查询收藏列表
     */
    @GetMapping("/list")
    public TableDataInfo list()
    {
        startPage();
        Long userId = SecurityUtils.getUserId();
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        List<Favorite> list = favoriteService.selectFavoriteList(favorite);
        return getDataTable(list);
    }

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestParam Long productId)
    {
        try
        {
            Long userId = SecurityUtils.getUserId();
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            favorite.setCreateTime(new Date());
            
            int result = favoriteService.insertFavorite(favorite);
            if (result > 0)
            {
                return success("收藏成功");
            }
            else
            {
                return error("该商品已收藏");
            }
        }
        catch (Exception e)
        {
            return error("收藏失败：" + e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove/{productId}")
    public AjaxResult remove(@PathVariable Long productId)
    {
        try
        {
            Long userId = SecurityUtils.getUserId();
            int result = favoriteService.deleteFavoriteByUserAndProduct(userId, productId);
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error("取消收藏失败：" + e.getMessage());
        }
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public AjaxResult check(@RequestParam Long productId)
    {
        try
        {
            Long userId = SecurityUtils.getUserId();
            boolean isFavorite = favoriteService.isFavorite(userId, productId);
            return success(isFavorite);
        }
        catch (Exception e)
        {
            return error("查询失败：" + e.getMessage());
        }
    }
}
