package io.github.andyradionov.udacitybakingapp.ui.steps;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.databinding.ActivityBakingBinding;
import io.github.andyradionov.udacitybakingapp.databinding.ActivityBakingBindingSw600dpImpl;
import io.github.andyradionov.udacitybakingapp.ui.base.BaseDrawerActivity;
import io.github.andyradionov.udacitybakingapp.ui.details.DetailsActivity;
import io.github.andyradionov.udacitybakingapp.ui.details.StepDetailsFragment;
import io.github.andyradionov.udacitybakingapp.viewmodels.BakingViewModel;
import timber.log.Timber;

public class BakingActivity extends BaseDrawerActivity
        implements RecipeStepsAdapter.OnStepItemClickListener,
        StepDetailsFragment.StepNavigationHandler {

    public static final String RECIPE_EXTRA = "recipe_extra";
    private BakingViewModel mBakingViewModel;
    private boolean mIsTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        ActivityBakingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_baking);

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
                    .replace(R.id.master_list_fragment, new RecipeStepsFragment())
                    .commit();

            if (binding instanceof ActivityBakingBindingSw600dpImpl) {
                mIsTwoPane = true;

                fragmentManager.beginTransaction()
                        .replace(R.id.detail_recipe_fragment, getDetailsFragment(recipe,
                                mBakingViewModel.getStepNumber().getValue()))
                        .commit();
            }
        }
    }

    @Override
    public void onStepItemClick(int stepNumber) {
        Timber.d("onStepItemClick(): %d", stepNumber);

        mBakingViewModel.getStepNumber().setValue(stepNumber);
        if (mIsTwoPane) {
            replaceDetailsFragment(mBakingViewModel.getRecipe().getValue(), stepNumber);
        } else {
            startDetailsActivity();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult()");

        if (requestCode == DetailsActivity.REQUEST_SHOW_DETAILS) {
            if (resultCode == DetailsActivity.RESULT_SHOW_PREVIOUS) {
                mBakingViewModel.decStepNumber();
                startDetailsActivity();
            } else if (resultCode == DetailsActivity.RESULT_SHOW_NEXT) {
                mBakingViewModel.incStepNumber();
                startDetailsActivity();
            }
        }
    }

    private void startDetailsActivity() {
        Timber.d("startDetailsActivity()");

        Intent startDetails = new Intent(this, DetailsActivity.class);
        startDetails.putExtra(DetailsActivity.RECIPE_EXTRA,
                mBakingViewModel.getRecipe().getValue());
        startDetails.putExtra(DetailsActivity.STEP_NUMBER_EXTRA,
                mBakingViewModel.getStepNumber().getValue());
        startActivityForResult(startDetails, DetailsActivity.REQUEST_SHOW_DETAILS);
    }
}
