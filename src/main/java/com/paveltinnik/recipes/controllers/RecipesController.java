package com.paveltinnik.recipes.controllers;

import com.paveltinnik.recipes.security.IAuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.paveltinnik.recipes.accessingdatajpa.MyUserServiceImpl;
import com.paveltinnik.recipes.accessingdatajpa.RecipeService;
import com.paveltinnik.recipes.models.MyUser;
import com.paveltinnik.recipes.models.Recipe;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class RecipesController {
    RecipeService recipeService;
    MyUserServiceImpl myUserService;
    IAuthenticationFacade authenticationFacade;

    @Autowired
    public RecipesController(RecipeService recipeService,
                             MyUserServiceImpl myUserService,
                             IAuthenticationFacade authenticationFacade) {
        this.recipeService = recipeService;
        this.myUserService = myUserService;
        this.authenticationFacade = authenticationFacade;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable("id") @Min(1) long id) {
        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("api/recipe/search")
    public ResponseEntity<Object> getRecipesByCategoryOrName(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "name", required = false) String name) {

        List<Recipe> recipeList = new ArrayList<>();

        if (category == null && name != null) {
            recipeList = recipeService.getRecipesByName(name
                    .trim()
                    .toLowerCase());
        } else if (name == null && category != null) {
            recipeList = recipeService.getRecipesByCategory(category
                    .trim()
                    .toLowerCase());
        } else if (category == null && name == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/recipe/new")
    public ResponseEntity<Object> setRecipe(@Valid @RequestBody Recipe recipe) {
        String name = getNameAuth();

        recipe.setDate(LocalDateTime.now());
        recipe.setCreator(name);

        recipeService.addRecipe(recipe);

        Map<String, Long> idMap = new HashMap<>();
        idMap.put("id", recipe.getRecipeId());

        return new ResponseEntity<>(idMap, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("api/recipe/{id}")
    public ResponseEntity<Object> deleteRecipe(@PathVariable long id) {
        String name = getNameAuth();

        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!name.equals(recipe.getCreator())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        recipeService.deleteRecipeById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("api/recipe/{id}")
    public ResponseEntity<Object> updateRecipe(@PathVariable long id,
                                               @Valid @RequestBody Recipe recipe,
                                               Principal principal) {
//        String name = getNameAuth();
        String name = principal.getName();

        Recipe oldRecipe = recipeService.getRecipeById(id);

        if (oldRecipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!name.equals(oldRecipe.getCreator())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        recipe.setCreator(name);
        recipe.setDate(LocalDateTime.now());
        recipeService.updateRecipe(recipe, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("api/register")
    public ResponseEntity<Object> register(@Valid @RequestBody MyUser myUser) {
        MyUser myUserFromDb = myUserService.getUser(myUser.getEmail());

        if (myUserFromDb == null) {
            myUserService.addUser(myUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already in database");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getNameAuth() {
        Authentication auth = authenticationFacade.getAuthentication();
        String currentUserName = null;

        if (!(auth instanceof AnonymousAuthenticationToken)) {
            currentUserName = auth.getName();
        }

        return currentUserName;
    }
}
