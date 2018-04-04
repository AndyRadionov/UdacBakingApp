package io.github.andyradionov.udacitybakingapp.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class DrawerViewModel extends ViewModel {
    private MutableLiveData<Recipe[]> mRecipes;

    public MutableLiveData<Recipe[]> getRecipes() {
        Timber.d("getRecipes()");
        if (mRecipes == null) {
            mRecipes = new MutableLiveData<>();
        }
        return mRecipes;
    }
}
