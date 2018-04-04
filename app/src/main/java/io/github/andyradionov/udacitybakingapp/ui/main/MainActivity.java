package io.github.andyradionov.udacitybakingapp.ui.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.databinding.ActivityMainBinding;
import io.github.andyradionov.udacitybakingapp.ui.base.BaseDrawerActivity;
import io.github.andyradionov.udacitybakingapp.ui.steps.BakingActivity;

public class MainActivity extends BaseDrawerActivity
        implements RecipesListAdapter.OnRecipeItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        prepareDrawer();

        Recipe[] recipes = mDrawerViewModel.getRecipes().getValue();
        RecipesListAdapter adapter = new RecipesListAdapter(this, this, recipes);
        binding.rvRecipesContainer.setAdapter(adapter);

        binding.rvRecipesContainer.setLayoutManager(getLayoutManager());
    }

    @Override
    public void onRecipeItemClick(Recipe recipe) {
        Intent startBakingActivity = new Intent(this, BakingActivity.class);
        startBakingActivity.putExtra(BakingActivity.RECIPE_EXTRA, recipe);
        startActivity(startBakingActivity);
    }

    @NonNull
    private RecyclerView.LayoutManager getLayoutManager() {
        RecyclerView.LayoutManager layoutManager;
        if (isTablet()) {
            layoutManager = new GridLayoutManager(this, 3);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        return layoutManager;
    }

    private boolean isTablet() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        return widthDp >= 600;
    }
}
