package io.github.andyradionov.udacitybakingapp.data.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class WidgetPreferenceHelperTest {
    @Test
    public void testUpdateRecipeId() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        int testRecipeId = 3;

        WidgetPreferenceHelper.updateRecipeId(appContext, testRecipeId);
        int storedRecipeId = WidgetPreferenceHelper.loadRecipeId(appContext);

        assertEquals(testRecipeId, storedRecipeId);
    }
}
