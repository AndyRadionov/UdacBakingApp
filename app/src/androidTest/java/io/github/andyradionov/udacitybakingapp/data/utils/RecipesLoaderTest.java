package io.github.andyradionov.udacitybakingapp.data.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;

import static org.junit.Assert.*;

/**
 * @author Andrey Radionov
 */
@RunWith(AndroidJUnit4.class)
public class RecipesLoaderTest {

    @Test
    public void testLoadFromJsonFile() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Recipe[] recipes = RecipesLoader.loadFromJsonFile(appContext);

        assertTrue(recipes != null);
        assertFalse(TextUtils.isEmpty(recipes[0].getName()));
    }

    @Test
    public void testLoadRecipeById() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Recipe recipe = RecipesLoader.loadRecipeById(appContext, 1);

        assertTrue(recipe != null);
        assertFalse(TextUtils.isEmpty(recipe.getName()));
    }

    @Test
    public void testLoadRecipeByWrongId() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        int wrongId = Integer.MAX_VALUE;
        Recipe recipe = RecipesLoader.loadRecipeById(appContext, wrongId);

        assertTrue(recipe != null);
        assertTrue(TextUtils.isEmpty(recipe.getName()));
    }
}
