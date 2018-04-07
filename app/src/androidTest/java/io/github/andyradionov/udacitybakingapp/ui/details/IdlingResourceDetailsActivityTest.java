package io.github.andyradionov.udacitybakingapp.ui.details;


import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.utils.RecipesLoader;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class IdlingResourceDetailsActivityTest {

    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<DetailsActivity> mActivityTestRule =
            new ActivityTestRule<DetailsActivity>(DetailsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Recipe recipe = RecipesLoader.
                            loadRecipeById(InstrumentationRegistry.getTargetContext(), 1);
                    Intent intent = new Intent(InstrumentationRegistry.getTargetContext(),
                            DetailsActivity.class);
                    intent.putExtra(DetailsActivity.RECIPE_EXTRA, recipe);
                    intent.putExtra(DetailsActivity.STEP_NUMBER_EXTRA, 0);

                    return intent;
                }
            };

    @Before
    public void registerIdlingResource() {

        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void idlingResourceTest() {
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.MINUTES);
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.MINUTES);
        onView(withIndex(withId(R.id.exo_play), 1)).perform(click());
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().register(mIdlingResource);
        }
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}