package com.paveltinnik.recipes.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table//(name = "recipes")
public class Recipe {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long recipeId;

    @JsonIgnore
    private String creator;

    @NotNull
    @NotBlank
    private String category;

    @NotNull
    @NotBlank
    private String name;

    private LocalDateTime date;

    @NotNull
    @NotBlank
    private String description;

    @ElementCollection
    @Size(min = 1)
    private List<String> ingredients = new ArrayList<>();

    @ElementCollection
    @Size(min = 1)
    private List<String> directions = new ArrayList<>();
}


