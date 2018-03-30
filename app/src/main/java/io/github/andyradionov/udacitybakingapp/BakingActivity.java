package io.github.andyradionov.udacitybakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;

public class BakingActivity extends BaseDrawerActivity
        implements RecipeStepsAdapter.OnStepItemClickListener,
        StepDetailsFragment.StepNavigationHandler {

    public static final String RECIPE_EXTRA = "recipe_extra";
    private Recipe mRecipe;
    private int mCurrentStep;
    private boolean mIsTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baking);

        prepareDrawer();

        Intent startIntent = getIntent();
        mRecipe = startIntent.getParcelableExtra(RECIPE_EXTRA);

        FragmentManager fragmentManager = getSupportFragmentManager();

        RecipeStepsFragment stepsFragment = RecipeStepsFragment.newInstance(mRecipe);
        fragmentManager.beginTransaction()
                .add(R.id.master_list_fragment, stepsFragment)
                .commit();

        if (findViewById(R.id.detail_recipe_fragment) != null) {
            mIsTwoPane = true;

            StepDetailsFragment detailsFragment =
                    StepDetailsFragment.newInstance(mRecipe, mCurrentStep);
            fragmentManager.beginTransaction()
                    .add(R.id.detail_recipe_fragment, detailsFragment)
                    .commit();
        }
    }


    @Override
    public void onStepItemClick(int stepIndex) {
        mCurrentStep = stepIndex;
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepDetailsFragment detailsFragment =
                StepDetailsFragment.newInstance(mRecipe, stepIndex);
        if (mIsTwoPane) {
            fragmentManager.beginTransaction()
                    .replace(R.id.detail_recipe_fragment, detailsFragment)
                    .commit();
        } else {
            startDetailsActivity(mRecipe, mCurrentStep);
        }
    }

    @Override
    public void onPreviousClick() {
        mCurrentStep--;
        FragmentManager fragmentManager = getSupportFragmentManager();

        StepDetailsFragment detailsFragment =
                StepDetailsFragment.newInstance(mRecipe, mCurrentStep);
        fragmentManager.beginTransaction()
                .replace(R.id.detail_recipe_fragment, detailsFragment)
                .commit();
    }

    @Override
    public void onNextClick() {
        mCurrentStep++;
        FragmentManager fragmentManager = getSupportFragmentManager();

        StepDetailsFragment detailsFragment =
                StepDetailsFragment.newInstance(mRecipe, mCurrentStep);
        fragmentManager.beginTransaction()
                .replace(R.id.detail_recipe_fragment, detailsFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DetailsActivity.REQUEST_SHOW_DETAILS) {
            if (resultCode == DetailsActivity.RESULT_SHOW_PREVIOUS) {
                mCurrentStep--;
                startDetailsActivity(mRecipe, mCurrentStep);
            } else if (resultCode == DetailsActivity.RESULT_SHOW_NEXT) {
                mCurrentStep++;
                startDetailsActivity(mRecipe, mCurrentStep);
            }
        }
    }
}
