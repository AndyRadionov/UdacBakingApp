package io.github.andyradionov.udacitybakingapp.data.network;

import android.content.Context;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;

/**
 * @author Andrey Radionov
 */

public interface IngredientsCallback {

    void showRecipe(Context context, Recipe recipe);
}
