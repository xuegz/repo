package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.config.CacheConfig;
import com.jiancai.entity.Category;
import com.jiancai.mapper.CategoryMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 品类业务（最多三级：一级 parentId=0，二级/三级逐层挂载）
 */
@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    /** 启用的品类列表（按排序） */
    @Cacheable(cacheNames = CacheConfig.CATEGORY, key = "'enabled'")
    public List<Category> listEnabled() {
        return lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId)
                .list();
    }

    /** 启用的一级品类（小程序首页与筛选入口） */
    @Cacheable(cacheNames = CacheConfig.CATEGORY, key = "'top'")
    public List<Category> listEnabledTop() {
        return lambdaQuery()
                .eq(Category::getStatus, 1)
                .eq(Category::getParentId, 0L)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId)
                .list();
    }

    /** 全部品类（管理端） */
    public List<Category> listAll() {
        return list(Wrappers.<Category>lambdaQuery().orderByAsc(Category::getSort));
    }

    /** 品类 id → 名称，商品列表填充展示信息时复用 */
    @Cacheable(cacheNames = CacheConfig.CATEGORY, key = "'nameMap'")
    public Map<Long, String> nameMap() {
        return list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
    }

    /** 层级：一级=1 */
    public int levelOf(Long id) {
        int level = 0;
        Long cur = id;
        while (cur != null && cur != 0L) {
            level++;
            Category c = getById(cur);
            if (c == null) {
                break;
            }
            cur = c.getParentId();
        }
        return level;
    }

    /** 以 id 为根的子树高度（自身=1） */
    public int subtreeHeight(Long id) {
        List<Category> all = list();
        return height(id, all);
    }

    private int height(Long id, List<Category> all) {
        int max = 1;
        for (Category c : all) {
            if (id.equals(c.getParentId())) {
                max = Math.max(max, 1 + height(c.getId(), all));
            }
        }
        return max;
    }

    /** 自身及全部后代 id（用于按一级品类查产品） */
    public List<Long> subtreeIds(Long rootId) {
        List<Category> all = list();
        Set<Long> ids = new HashSet<>();
        ids.add(rootId);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Category c : all) {
                if (c.getParentId() != null && ids.contains(c.getParentId()) && ids.add(c.getId())) {
                    changed = true;
                }
            }
        }
        return new ArrayList<>(ids);
    }
}
