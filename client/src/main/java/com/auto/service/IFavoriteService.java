package com.auto.service;

import java.util.List;
import com.auto.domain.Favorite;

/**
 * 收藏Service接口
 *
 * @author auto
 * @date 2026-01-07
 */
public interface IFavoriteService
{
    /**
     * 查询收藏列表
     *
     * @param favorite 收藏
     * @return 收藏集合
     */
    public List<Favorite> selectFavoriteList(Favorite favorite);

    /**
     * 新增收藏
     *
     * @param favorite 收藏
     * @return 结果
     */
    public int insertFavorite(Favorite favorite);

    /**
     * 删除收藏
     *
     * @param id 收藏主键
     * @return 结果
     */
    public int deleteFavoriteById(Long id);

    /**
     * 根据用户ID和商品ID删除收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 结果
     */
    public int deleteFavoriteByUserAndProduct(Long userId, Long productId);

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    public boolean isFavorite(Long userId, Long productId);
}
