package io.github.andyradionov.udacitybakingapp.ui.steps;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.utils.RecipesLoader;
import io.github.andyradionov.udacitybakingapp.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class BakingActivityTest {

    private Recipe mRecipe;

    @Rule
    public ActivityTestRule<BakingActivity> mActivityTestRule =
            new ActivityTestRule<BakingActivity>(BakingActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    mRecipe = RecipesLoader.
                            loadRecipeById(InstrumentationRegistry.getTargetContext(), 1);
                    Intent intent = new Intent(InstrumentationRegistry.getTargetContext(),
                            BakingActivity.class);
                    intent.putExtra(BakingActivity.RECIPE_EXTRA, mRecipe);
                    return intent;
                }
            };

    @Test
    public void testClickRecyclerViewItem_OpenDetails() {

        int textIndex = 1;
        onView(withId(R.id.rv_steps_container))
                .perform(RecyclerViewActions.actionOnItemAtPosition(textIndex, click()));

        String title = mRecipe.getName();
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(title)));

        onView(withId(R.id.tv_recipe_instructions))
                .check(matches(withText(mRecipe.getSteps()
                        .get(textIndex).getDescription())));
    }
}