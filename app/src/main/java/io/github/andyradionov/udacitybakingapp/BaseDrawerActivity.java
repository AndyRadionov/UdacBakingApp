package io.github.andyradionov.udacitybakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
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

    protected void prepareDrawer() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this);
        drawerArrow.setColor(getResources().getColor(android.R.color.white));

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,  /* значок-гамбургер для замены стрелки 'Up' */
                R.string.drawer_open,  /* добавьте строку "open drawer" - описание для  accessibility */
                R.string.drawer_close  /* добавьте "close drawer" - описание для accessibility */
        );
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerArrowDrawable(drawerArrow);

        mRecipes = RecipesLoader.loadFromJsonFile(this);

        mNavigationView = findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

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
                            startDetailsActivity(mRecipesKeys.get(menuTitle), 0);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            return true;
                        }
                        return false;
                    }
                });
    }

    protected void startDetailsActivity(Recipe recipe, int stepNumber) {
        Intent startDetails = new Intent(this, DetailsActivity.class);
        startDetails.putExtra(DetailsActivity.RECIPE_EXTRA, recipe);
        startDetails.putExtra(DetailsActivity.STEP_NUMBER_EXTRA, stepNumber);
        startActivityForResult(startDetails, DetailsActivity.REQUEST_SHOW_DETAILS);
    }
}
