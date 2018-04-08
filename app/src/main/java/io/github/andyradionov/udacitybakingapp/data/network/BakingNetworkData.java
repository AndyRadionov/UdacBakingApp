package io.github.andyradionov.udacitybakingapp.data.network;

import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import io.github.andyradionov.udacitybakingapp.app.App;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.utils.WidgetPreferenceHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class BakingNetworkData {

    private List<Recipe> mCachedRecipes;

    public BakingNetworkData() {
        Timber.d("BakingNetworkData constructor call");

        mCachedRecipes = Collections.emptyList();
    }

    public void loadRecipes(final RecipeCallback callback) {
        Timber.d("getRecipes()");

        if (!mCachedRecipes.isEmpty()) {
            callback.showRecipes(mCachedRecipes);
            return;
        }

        App.getBakingApi()
                .getBakingRecipes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    callback.showError();
                })
                .subscribe(recipes -> {
                    if (recipes.isEmpty()) {
                        callback.showError();
                    } else {
                        mCachedRecipes = recipes;
                        callback.showRecipes(recipes);
                    }
                }, throwable -> callback.showError());
    }

    public void loadRecipeById(Context context, final IngredientsCallback callback, int id) {
        Timber.d("getRecipes()");

        if (!mCachedRecipes.isEmpty()) {
            Recipe recipe = filterById(mCachedRecipes, id);
            callback.showRecipe(context, recipe);
            return;
        }

        App.getBakingApi()
                .getBakingRecipes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Timber.d("Cant Load Recipes");
                })
                .subscribe(recipes -> {
                    if (recipes.isEmpty()) {
                        Timber.d("Cant Load Recipes");

                    } else {
                        mCachedRecipes = recipes;
                        updateMaxRecipeId(context, recipes);
                        Recipe recipe = filterById(recipes, id);
                        callback.showRecipe(context, recipe);
                    }
                }, throwable -> Timber.d("Cant Load Recipes"));
    }

    //Due to Java Streams needs API 24+
    private Recipe filterById(List<Recipe> recipes, int id) {
        for (Recipe recipe : recipes) {
            if (recipe.getId() == id) {
                return recipe;
            }
        }
        return new Recipe();
    }

    private void updateMaxRecipeId(Context context, List<Recipe> recipes) {
        int maxId = 1;
        for (Recipe recipe : recipes) {
            if (recipe.getId() > maxId) {
                maxId = recipe.getId();
            }
        }

        WidgetPreferenceHelper.updateMaxRecipeId(context, maxId);
    }
}
