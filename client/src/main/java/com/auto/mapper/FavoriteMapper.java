package com.auto.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.auto.domain.Favorite;

/**
 * 收藏Mapper接口
 *
 * @author auto
 * @date 2026-01-07
 */
public interface FavoriteMapper
{
    /**
     * 查询收藏列表
     *
     * @param favorite 收藏
     * @return 收藏集合
     */
    public List<Favorite> selectFavoriteList(Favorite favorite);

    /**
     * 根据用户ID和商品ID查询收藏
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 收藏
     */
    public Favorite selectFavoriteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

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
    public int deleteFavoriteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
