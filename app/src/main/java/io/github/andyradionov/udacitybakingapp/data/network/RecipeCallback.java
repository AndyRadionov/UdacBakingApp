package io.github.andyradionov.udacitybakingapp.data.network;

import java.util.List;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;

/**
 * @author Andrey Radionov
 */

public interface RecipeCallback {

    void showRecipes(List<Recipe> recipes);

    void showError();
}
