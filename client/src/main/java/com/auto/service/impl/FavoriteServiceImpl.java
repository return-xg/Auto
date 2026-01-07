package com.auto.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auto.domain.Favorite;
import com.auto.mapper.FavoriteMapper;
import com.auto.service.IFavoriteService;

/**
 * 收藏Service业务层处理
 *
 * @author auto
 * @date 2026-01-07
 */
@Service
public class FavoriteServiceImpl implements IFavoriteService
{
    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 查询收藏列表
     *
     * @param favorite 收藏
     * @return 收藏
     */
    @Override
    public List<Favorite> selectFavoriteList(Favorite favorite)
    {
        return favoriteMapper.selectFavoriteList(favorite);
    }

    /**
     * 新增收藏
     *
     * @param favorite 收藏
     * @return 结果
     */
    @Override
    public int insertFavorite(Favorite favorite)
    {
        // 检查是否已收藏
        if (isFavorite(favorite.getUserId(), favorite.getProductId()))
        {
            return 0; // 已收藏，返回0表示未新增
        }
        return favoriteMapper.insertFavorite(favorite);
    }

    /**
     * 删除收藏
     *
     * @param id 收藏主键
     * @return 结果
     */
    @Override
    public int deleteFavoriteById(Long id)
    {
        return favoriteMapper.deleteFavoriteById(id);
    }

    /**
     * 根据用户ID和商品ID删除收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 结果
     */
    @Override
    public int deleteFavoriteByUserAndProduct(Long userId, Long productId)
    {
        return favoriteMapper.deleteFavoriteByUserAndProduct(userId, productId);
    }

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    @Override
    public boolean isFavorite(Long userId, Long productId)
    {
        Favorite favorite = favoriteMapper.selectFavoriteByUserAndProduct(userId, productId);
        return favorite != null;
    }
}
