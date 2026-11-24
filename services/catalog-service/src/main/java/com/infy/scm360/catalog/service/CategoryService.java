package com.infy.scm360.catalog.service;

import com.infy.scm360.catalog.domain.Category;
import com.infy.scm360.catalog.dto.CategoryDto;
import com.infy.scm360.catalog.repository.CategoryRepository;
import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> listAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(String id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return mapToDto(cat);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto req) {
        Category cat = new Category();
        cat.setId(UUID.randomUUID().toString());
        cat.setName(req.name());
        cat.setDescription(req.description());
        if (req.parentId() != null) {
            Category parent = categoryRepository.findById(req.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", req.parentId()));
            cat.setParent(parent);
        }
        cat.setCreatedAt(Instant.now());
        cat.setCreatedBy(SecurityUtils.getCurrentUserId());
        cat.setUpdatedAt(Instant.now());
        cat.setUpdatedBy(SecurityUtils.getCurrentUserId());

        categoryRepository.save(cat);
        return mapToDto(cat);
    }

    private CategoryDto mapToDto(Category c) {
        return new CategoryDto(
            c.getId(),
            c.getName(),
            c.getDescription(),
            c.getParent() != null ? c.getParent().getId() : null
        );
    }
}
