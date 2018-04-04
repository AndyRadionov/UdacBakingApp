package io.github.andyradionov.udacitybakingapp.data.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.github.andyradionov.udacitybakingapp.R;

/**
 * @author Andrey Radionov
 */

public class WidgetPreferenceHelper {

    private WidgetPreferenceHelper() {
    }

    public static int loadRecipeId(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPrefs.getInt(
                context.getString(R.string.pref_recipe_id_key),
                context.getResources().getInteger(R.integer.pref_recipe_id_default));
    }

    public static void updateRecipeId(Context context, int recipeId) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        sharedPrefs.edit()
                .putInt(context.getString(R.string.pref_recipe_id_key), recipeId)
                .apply();
    }
}
