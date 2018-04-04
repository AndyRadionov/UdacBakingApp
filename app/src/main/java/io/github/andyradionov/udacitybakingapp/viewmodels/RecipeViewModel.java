package io.github.andyradionov.udacitybakingapp.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;

/**
 * @author Andrey Radionov
 */

public class RecipeViewModel extends BaseObservable {

    private Recipe mRecipe;
    private int mStepNumber;

    public RecipeViewModel() {
    }

    public RecipeViewModel(Recipe recipe) {
        setRecipe(recipe);
    }

    @Bindable
    public String getName() {
        return mRecipe.getName();
    }

    public Recipe getRecipe() {
        return mRecipe;
    }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
        notifyChange();
    }

    @Bindable
    public String getStepShortDescription() {
        return mRecipe.getSteps().get(mStepNumber - 1).getShortDescription();
    }

    @Bindable
    public String getStepDescription() {
        return mRecipe.getSteps().get(mStepNumber - 1).getDescription();
    }

    @Bindable
    public String getStepIngredients() {
        return mRecipe.getIngredientsString();
    }

    public void setStepNumber(int stepNumber) {
        mStepNumber = stepNumber;
        notifyChange();
    }
}
