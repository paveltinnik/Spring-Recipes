package com.paveltinnik.recipes.accessingdatajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paveltinnik.recipes.models.Recipe;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public void addRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public Recipe getRecipeById(long id) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        return recipeOptional.orElse(null);
    }

    public List<Recipe> getRecipesByCategory(String category) {
        List<Recipe> recipeList = recipeRepository.
                findAllByCategoryIgnoreCaseOrderByDateDesc(category);
        return recipeList;
    }

    public List<Recipe> getRecipesByName(String name) {
        List<Recipe> recipeList = recipeRepository.
                findAllByNameContainsIgnoreCaseOrderByDateDesc(name);
        return recipeList;
    }

    public void deleteRecipeById(long id) {
        recipeRepository.deleteById(id);
    }

    public void updateRecipe(Recipe recipe, long id) {
        Optional<Recipe> oldRecipe = recipeRepository.findById(id);
        if (oldRecipe.isPresent()) {
            recipe.setRecipeId(id);
            recipeRepository.save(recipe);
        }
    }
}
