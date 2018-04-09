package io.github.andyradionov.udacitybakingapp.ui.steps;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;

import io.github.andyradionov.udacitybakingapp.IdlingResource.SimpleIdlingResource;
import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.databinding.ActivityBakingBinding;
import io.github.andyradionov.udacitybakingapp.databinding.ActivityBakingBindingSw600dpImpl;
import io.github.andyradionov.udacitybakingapp.ui.base.BaseDrawerActivity;
import io.github.andyradionov.udacitybakingapp.ui.details.StepDetailsFragment;
import io.github.andyradionov.udacitybakingapp.viewmodels.BakingViewModel;
import timber.log.Timber;

public class BakingActivity extends BaseDrawerActivity
        implements RecipeStepsAdapter.OnStepItemClickListener,
        StepDetailsFragment.StepNavigationHandler {

    public static final String RECIPE_EXTRA = "recipe_extra";
    private BakingViewModel mBakingViewModel;
    private boolean mIsTwoPane;
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_baking);
        mIsTwoPane = isTablet();

        Intent startIntent = getIntent();
        Recipe recipe = startIntent.getParcelableExtra(RECIPE_EXTRA);
        setTitle(recipe.getName());
        mBakingViewModel = ViewModelProviders.of(this).get(BakingViewModel.class);
        if (mBakingViewModel.getRecipe().getValue() == null) {
            mBakingViewModel.getRecipe().setValue(recipe);
        }

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.master_fragment, new RecipeStepsFragment())
                    .commit();

            if (mIsTwoPane) {
                fragmentManager.beginTransaction()
                        .replace(R.id.detail_fragment, getDetailsFragment(recipe,
                                mBakingViewModel.getStepNumber().getValue()))
                        .commit();
            }
        }
    }

    @Override
    public void onStepItemClick(int stepNumber) {
        Timber.d("onStepItemClick(): %d", stepNumber);

        mBakingViewModel.getStepNumber().setValue(stepNumber);
        replaceDetailsFragment(mBakingViewModel.getRecipe().getValue(),
                mBakingViewModel.getStepNumber().getValue());
    }

    @Override
    public void onPreviousClick() {
        Timber.d("onPreviousClick()");
        mBakingViewModel.decStepNumber();
        replaceDetailsFragment(mBakingViewModel.getRecipe().getValue(),
                mBakingViewModel.getStepNumber().getValue());
    }

    @Override
    public void onNextClick() {
        Timber.d("onNextClick()");
        mBakingViewModel.incStepNumber();
        replaceDetailsFragment(mBakingViewModel.getRecipe().getValue(),
                mBakingViewModel.getStepNumber().getValue());
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    protected void replaceDetailsFragment(Recipe recipe, int stepNumber) {
        Timber.d("replaceDetailsFragment() for step: %d", stepNumber);
        int fragmentLayoutId = mIsTwoPane ? R.id.detail_fragment : R.id.master_fragment;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(fragmentLayoutId, getDetailsFragment(recipe, stepNumber))
                .commit();
    }
}
