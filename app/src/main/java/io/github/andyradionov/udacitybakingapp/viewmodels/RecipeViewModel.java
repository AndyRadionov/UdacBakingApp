package io.github.andyradionov.udacitybakingapp.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import timber.log.Timber;

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
        Timber.d("getName()");
        return mRecipe.getName();
    }

    public Recipe getRecipe() {
        Timber.d("getRecipe()");
        return mRecipe;
    }

    public void setRecipe(Recipe recipe) {
        Timber.d("setRecipe()");
        mRecipe = recipe;
        notifyChange();
    }

    @Bindable
    public String getStepShortDescription() {
        Timber.d("getStepShortDescription()");
        return mRecipe.getSteps().get(mStepNumber - 1).getShortDescription();
    }

    @Bindable
    public String getStepDescription() {
        Timber.d("getStepDescription()");
        return mRecipe.getSteps().get(mStepNumber - 1).getDescription();
    }

    @Bindable
    public String getStepIngredients() {
        Timber.d("getStepIngredients()");
        return mRecipe.getIngredientsString();
    }

    @Bindable
    public int getStepNumber() {
        Timber.d("getStepNumber()");
        return mStepNumber;
    }

    public void setStepNumber(int stepNumber) {
        Timber.d("setStepNumber()");
        mStepNumber = stepNumber;
        notifyChange();
    }
}
