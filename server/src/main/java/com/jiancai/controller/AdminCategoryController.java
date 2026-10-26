package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.entity.Category;
import com.jiancai.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台端 - 品类管理
 */
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public R<List<Category>> list() {
        return R.ok(categoryService.listAll());
    }

    @PostMapping("/save")
    public R<Void> save(@RequestBody Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryService.saveOrUpdate(category);
        return R.ok();
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Long id) {
        categoryService.removeById(id);
        return R.ok();
    }
}
