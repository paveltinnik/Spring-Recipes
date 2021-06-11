package com.paveltinnik.recipes.accessingdatajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paveltinnik.recipes.models.Recipe;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    public List<Recipe> findAllByCategoryIgnoreCaseOrderByDateDesc(String category);
    public List<Recipe> findAllByNameContainsIgnoreCaseOrderByDateDesc(String name);
}
