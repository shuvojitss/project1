package com.infy.scm360.catalog.repository;

import com.infy.scm360.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(String parentId);
}
