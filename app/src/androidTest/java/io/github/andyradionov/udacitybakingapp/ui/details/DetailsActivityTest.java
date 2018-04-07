package io.github.andyradionov.udacitybakingapp.ui.details;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.utils.RecipesLoader;
import io.github.andyradionov.udacitybakingapp.ui.steps.BakingActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class DetailsActivityTest {

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
    public void testClickNextBtn_clickPrevBtn() {
        int textIndex = 1;
        onView(withId(R.id.rv_steps_container))
                .perform(RecyclerViewActions.actionOnItemAtPosition(textIndex, click()));

        onView((withId(R.id.btn_next_step))).perform(click());

        onView(withId(R.id.tv_recipe_instructions))
                .check(matches(withText(mRecipe.getSteps()
                        .get(1).getDescription())));

        onView(isRoot()).perform(waitId(R.id.btn_prev_step, TimeUnit.SECONDS.toMillis(15)));
        onView((withId(R.id.btn_prev_step))).perform(click());

        onView(withId(R.id.tv_recipe_instructions))
                .check(matches(withText(mRecipe.getSteps()
                        .get(0).getDescription())));
    }

    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}