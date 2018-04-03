package io.github.andyradionov.udacitybakingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import timber.log.Timber;

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
        Timber.d("onCreate");
        prepareDrawer();

        Intent startIntent = getIntent();
        mRecipe = startIntent.getParcelableExtra(RECIPE_EXTRA);

        setTitle(mRecipe.getName());
        FragmentManager fragmentManager = getSupportFragmentManager();

        RecipeStepsFragment stepsFragment = RecipeStepsFragment.newInstance(mRecipe);
        fragmentManager.beginTransaction()
                .add(R.id.master_list_fragment, stepsFragment)
                .commit();

        if (findViewById(R.id.detail_recipe_fragment) != null) {
            mIsTwoPane = true;

            fragmentManager.beginTransaction()
                    .add(R.id.detail_recipe_fragment, getDetailsFragment(mRecipe, mCurrentStep))
                    .commit();
        }
    }

    @Override
    public void onStepItemClick(int stepNumber) {
        mCurrentStep = stepNumber;
        if (mIsTwoPane) {
            replaceDetailsFragment(mRecipe, stepNumber);
        } else {
            startDetailsActivity();
        }
    }

    @Override
    public void onPreviousClick() {
        mCurrentStep--;
        replaceDetailsFragment(mRecipe, mCurrentStep);
    }

    @Override
    public void onNextClick() {
        mCurrentStep++;
        replaceDetailsFragment(mRecipe, mCurrentStep);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DetailsActivity.REQUEST_SHOW_DETAILS) {
            if (resultCode == DetailsActivity.RESULT_SHOW_PREVIOUS) {
                mCurrentStep--;
                startDetailsActivity();
            } else if (resultCode == DetailsActivity.RESULT_SHOW_NEXT) {
                mCurrentStep++;
                startDetailsActivity();
            }
        }
    }

    private void startDetailsActivity() {
        Intent startDetails = new Intent(this, DetailsActivity.class);
        startDetails.putExtra(DetailsActivity.RECIPE_EXTRA, mRecipe);
        startDetails.putExtra(DetailsActivity.STEP_NUMBER_EXTRA, mCurrentStep);
        startActivityForResult(startDetails, DetailsActivity.REQUEST_SHOW_DETAILS);
    }
}
