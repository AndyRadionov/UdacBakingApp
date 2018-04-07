package io.github.andyradionov.udacitybakingapp.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;

import io.github.andyradionov.udacitybakingapp.IdlingResource.SimpleIdlingResource;
import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.ui.base.BaseDrawerActivity;
import timber.log.Timber;

public class DetailsActivity extends BaseDrawerActivity
        implements StepDetailsFragment.StepNavigationHandler {

    public static final int REQUEST_SHOW_DETAILS = 20;
    public static final int RESULT_SHOW_PREVIOUS = 21;
    public static final int RESULT_SHOW_NEXT = 22;

    public static final String RECIPE_EXTRA = "recipe_extra";
    public static final String STEP_NUMBER_EXTRA = "step_number_extra";

    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        setContentView(R.layout.activity_details);
        prepareDrawer();

        Intent startIntent = getIntent();
        Recipe recipe = startIntent.getParcelableExtra(RECIPE_EXTRA);
        int stepNumber = startIntent.getIntExtra(STEP_NUMBER_EXTRA, 0);

        setTitle(recipe.getName());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.detail_recipe_fragment, getDetailsFragment(recipe, stepNumber))
                .commit();
    }

    @Override
    public void onPreviousClick() {
        Timber.d("onPreviousClick()");
        setResult(RESULT_SHOW_PREVIOUS);
        finish();
    }

    @Override
    public void onNextClick() {
        Timber.d("onNextClick()");
        setResult(RESULT_SHOW_NEXT);
        finish();
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
