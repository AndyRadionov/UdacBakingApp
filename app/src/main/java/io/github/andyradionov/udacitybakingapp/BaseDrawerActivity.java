package io.github.andyradionov.udacitybakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.utils.RecipesLoader;

/**
 * @author Andrey Radionov
 */

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected Recipe[] mRecipes;
    private Map<String, Recipe> mRecipesKeys;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void prepareDrawer() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);

        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this);
        drawerArrow.setColor(getResources().getColor(android.R.color.white));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                null,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawerToggle.setDrawerArrowDrawable(drawerArrow);
        mDrawerLayout.addDrawerListener(drawerToggle);

        mRecipes = RecipesLoader.loadFromJsonFile(this);

        mNavigationView = findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    protected Fragment getDetailsFragment(Recipe recipe, int stepNumber) {
        Fragment fragment;
        if (TextUtils.isEmpty(recipe.getVideoUrlForStep(stepNumber))) {
            fragment = StepDetailsFragment.newInstance(recipe, stepNumber);
        } else {
            fragment = StepDetailsVideoFragment.newInstance(recipe, stepNumber);
        }
        return fragment;
    }

    protected void replaceDetailsFragment(Recipe recipe, int stepNumber) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.detail_recipe_fragment, getDetailsFragment(recipe, stepNumber))
                .commit();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        mRecipesKeys = new HashMap<>(mRecipes.length);
        final Menu menu = mNavigationView.getMenu();
        for (Recipe mRecipe : mRecipes) {
            menu.add(mRecipe.getName());
            mRecipesKeys.put(mRecipe.getName(), mRecipe);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        String menuTitle = menuItem.getTitle().toString();
                        if (mRecipesKeys.containsKey(menuTitle)) {
                            startStepsActivity(mRecipesKeys.get(menuTitle));
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void startStepsActivity(Recipe recipe) {
        Intent startSteps = new Intent(this, BakingActivity.class);
        startSteps.putExtra(BakingActivity.RECIPE_EXTRA, recipe);
        startActivity(startSteps);
    }
}
