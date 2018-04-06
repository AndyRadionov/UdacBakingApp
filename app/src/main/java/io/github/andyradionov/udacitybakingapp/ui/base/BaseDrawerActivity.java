package io.github.andyradionov.udacitybakingapp.ui.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.ListView;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.utils.RecipesLoader;
import io.github.andyradionov.udacitybakingapp.ui.steps.BakingActivity;
import io.github.andyradionov.udacitybakingapp.ui.details.StepDetailsFragment;
import io.github.andyradionov.udacitybakingapp.ui.details.StepDetailsVideoFragment;
import io.github.andyradionov.udacitybakingapp.viewmodels.DrawerViewModel;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerViewModel mDrawerViewModel;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected(): %s", item.getTitle());

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
        Timber.d("prepareDrawer()");

        mDrawerViewModel = ViewModelProviders.of(this).get(DrawerViewModel.class);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);

        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this);
        drawerArrow.setColor(getResources().getColor(android.R.color.black));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                null,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawerToggle.setDrawerArrowDrawable(drawerArrow);
        mDrawerLayout.addDrawerListener(drawerToggle);

        if (mDrawerViewModel.getRecipes().getValue() == null) {
            Recipe[] recipes = RecipesLoader.loadFromJsonFile(this);
            mDrawerViewModel.getRecipes().setValue(recipes);
        }

        mNavigationView = findViewById(R.id.nav_view);

        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

    }

    protected Fragment getDetailsFragment(Recipe recipe, int stepNumber) {
        Timber.d("getDetailsFragment() for step: %d", stepNumber);

        Fragment fragment;
        if (TextUtils.isEmpty(recipe.getVideoUrlForStep(stepNumber))) {
            fragment = StepDetailsFragment.newInstance(recipe, stepNumber);
        } else {
            fragment = StepDetailsVideoFragment.newInstance(recipe, stepNumber);
        }
        return fragment;
    }

    protected void replaceDetailsFragment(Recipe recipe, int stepNumber) {
        Timber.d("replaceDetailsFragment() for step: %d", stepNumber);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.detail_recipe_fragment, getDetailsFragment(recipe, stepNumber))
                .commit();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        Timber.d("setupDrawerContent()");

        final Menu menu = mNavigationView.getMenu();
        mNavigationView.setItemBackgroundResource(R.drawable.drawer_item_bg);

        Recipe[] recipes = mDrawerViewModel.getRecipes().getValue();
        for (int i = 0; i < recipes.length; i++) {
            menu.add(0, i, 0, recipes[i].getName());
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Recipe[] recipes = mDrawerViewModel.getRecipes().getValue();
                        startStepsActivity(recipes[menuItem.getItemId()]);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
    }

    private void startStepsActivity(Recipe recipe) {
        Timber.d("startStepsActivity()");

        Intent startSteps = new Intent(this, BakingActivity.class);
        startSteps.putExtra(BakingActivity.RECIPE_EXTRA, recipe);
        startActivity(startSteps);
    }
}
