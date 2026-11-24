package com.infy.scm360.catalog.controller;

import com.infy.scm360.catalog.dto.CategoryDto;
import com.infy.scm360.catalog.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories() {
        return ResponseEntity.ok(categoryService.listAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(req));
    }
}
