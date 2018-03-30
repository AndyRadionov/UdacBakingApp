package io.github.andyradionov.udacitybakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;

public class DetailsActivity extends BaseDrawerActivity
    implements StepDetailsFragment.StepNavigationHandler {

    public static final int REQUEST_SHOW_DETAILS = 20;
    public static final int RESULT_SHOW_PREVIOUS = 21;
    public static final int RESULT_SHOW_NEXT = 22;


    public static final String RECIPE_EXTRA = "recipe_extra";
    public static final String STEP_NUMBER_EXTRA = "step_number_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        prepareDrawer();

        Intent startIntent = getIntent();
        Recipe recipe = startIntent.getParcelableExtra(RECIPE_EXTRA);
        int stepNumber = startIntent.getIntExtra(STEP_NUMBER_EXTRA, 0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        StepDetailsFragment detailsFragment =
                StepDetailsFragment.newInstance(recipe, stepNumber);
        fragmentManager.beginTransaction()
                .add(R.id.detail_recipe_fragment, detailsFragment)
                .commit();
    }

    @Override
    public void onPreviousClick() {
        setResult(RESULT_SHOW_PREVIOUS);
        finish();
    }

    @Override
    public void onNextClick() {
        setResult(RESULT_SHOW_NEXT);
        finish();
    }
}
