package com.auto.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auto.common.utils.DateUtils;
import com.auto.common.utils.StringUtils;
import com.auto.domain.Category;
import com.auto.mapper.CategoryMapper;
import com.auto.service.ICategoryService;

/**
 * 商品分类Service业务层处理
 *
 * @author auto
 * @date 2026-01-07
 */
@Service
public class CategoryServiceImpl implements ICategoryService
{
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询商品分类
     */
    @Override
    public Category selectCategoryById(Long id)
    {
        return categoryMapper.selectCategoryById(id);
    }

    /**
     * 查询商品分类列表
     */
    @Override
    public List<Category> selectCategoryList(Category category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    /**
     * 新增商品分类
     */
    @Override
    public int insertCategory(Category category)
    {
        category.setCreateTime(DateUtils.getNowDate());
        return categoryMapper.insertCategory(category);
    }

    /**
     * 修改商品分类
     */
    @Override
    public int updateCategory(Category category)
    {
        category.setUpdateTime(DateUtils.getNowDate());
        return categoryMapper.updateCategory(category);
    }

    /**
     * 批量删除商品分类
     */
    @Override
    public int deleteCategoryByIds(Long[] ids)
    {
        return categoryMapper.deleteCategoryByIds(ids);
    }

    /**
     * 删除商品分类信息
     */
    @Override
    public int deleteCategoryById(Long id)
    {
        // 原本检查子分类的逻辑已无意义，可保留或移除
        // 如果不需要级联删除，直接删除即可
        return categoryMapper.deleteCategoryById(id);
    }
}