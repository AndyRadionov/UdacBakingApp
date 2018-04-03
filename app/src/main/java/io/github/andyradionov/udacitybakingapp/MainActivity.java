package io.github.andyradionov.udacitybakingapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.utils.RecipesLoader;
import io.github.andyradionov.udacitybakingapp.databinding.ActivityMainBinding;

public class MainActivity extends BaseDrawerActivity
        implements RecipesListAdapter.OnRecipeItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        prepareDrawer();

        RecipesListAdapter adapter = new RecipesListAdapter(this, mRecipes);
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
