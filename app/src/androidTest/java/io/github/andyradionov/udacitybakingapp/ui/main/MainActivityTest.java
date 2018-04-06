package io.github.andyradionov.udacitybakingapp.ui.main;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
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

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testOpenCloseDrawer() throws Exception {
        onView(withContentDescription(getString(R.string.drawer_open))).perform(click());
        onView(withContentDescription(getString(R.string.drawer_close))).perform(click());
    }


    @Test
    public void testClickRecyclerViewItem_OpenStepsActivity() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        Recipe[] recipes = RecipesLoader.loadFromJsonFile(appContext);

        int textIndex = 1;
        onView(withId(R.id.rv_recipes_container))
                .perform(RecyclerViewActions.actionOnItemAtPosition(textIndex, click()));

        String title = recipes[textIndex].getName();
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(title)));

    }

    @Test
    public void testClickDrawerItem_OpenStepsActivity() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        Recipe[] recipes = RecipesLoader.loadFromJsonFile(appContext);

        int textIndex = 2;

        onView(withContentDescription(getString(R.string.drawer_open))).perform(click());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(textIndex));

        String title = recipes[textIndex].getName();
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(title)));

    }

    private String getString(int resId) {
        return getInstrumentation().getTargetContext().getString(resId);
    }

}