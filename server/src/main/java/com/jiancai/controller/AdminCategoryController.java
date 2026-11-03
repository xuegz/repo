package com.jiancai.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Category;
import com.jiancai.entity.Product;
import com.jiancai.service.CategoryService;
import com.jiancai.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台端 - 品类管理（最多三级）
 */
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    private static final int MAX_LEVEL = 3;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public R<List<Category>> list() {
        return R.ok(categoryService.listAll());
    }

    @PostMapping("/save")
    public R<Void> save(@RequestBody Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BusinessException("请填写品类名称");
        }
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        Long parentId = category.getParentId();
        if (parentId != 0L) {
            Category parent = categoryService.getById(parentId);
            if (parent == null) {
                throw new BusinessException("上级品类不存在");
            }
            if (parentId.equals(category.getId())) {
                throw new BusinessException("不能把自己设为上级品类");
            }
            if (category.getId() != null
                    && categoryService.subtreeIds(category.getId()).contains(parentId)) {
                throw new BusinessException("不能挂到自己的子类下");
            }
            int parentLevel = categoryService.levelOf(parentId);
            if (parentLevel >= MAX_LEVEL) {
                throw new BusinessException("最多支持三级分类，三级分类下不能再添加子类");
            }
            if (category.getId() != null
                    && parentLevel + categoryService.subtreeHeight(category.getId()) > MAX_LEVEL) {
                throw new BusinessException("移动后层级会超过三级，请先调整子类");
            }
        }
        categoryService.saveOrUpdate(category);
        return R.ok();
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Long id) {
        long children = categoryService.count(
                Wrappers.<Category>lambdaQuery().eq(Category::getParentId, id));
        if (children > 0) {
            throw new BusinessException("该品类下还有子类，请先删除子类");
        }
        long products = productService.count(
                Wrappers.<Product>lambdaQuery().eq(Product::getCategoryId, id));
        if (products > 0) {
            throw new BusinessException("该品类下还有 " + products + " 个产品，不能删除");
        }
        categoryService.removeById(id);
        return R.ok();
    }
}
