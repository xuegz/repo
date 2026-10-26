package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.entity.Category;
import com.jiancai.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 品类业务
 */
@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    /** 启用的品类列表（按排序） */
    public List<Category> listEnabled() {
        return lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId)
                .list();
    }

    /** 全部品类（管理端分页时用） */
    public List<Category> listAll() {
        return list(Wrappers.<Category>lambdaQuery().orderByAsc(Category::getSort));
    }
}
