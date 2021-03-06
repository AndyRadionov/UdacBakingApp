package io.github.andyradionov.udacitybakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.app.App;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.network.IngredientsCallback;
import io.github.andyradionov.udacitybakingapp.data.utils.WidgetPreferenceHelper;
import io.github.andyradionov.udacitybakingapp.ui.steps.BakingActivity;
import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider implements IngredientsCallback {

    private static final String PREV_RECIPE_ACTION = "prev_recipe_action";
    private static final String NEXT_RECIPE_ACTION = "next_recipe_action";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                Recipe recipe, int appWidgetId) {
        Timber.d("updateAppWidget() with Recipe: %s", recipe);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        views.setTextViewText(R.id.tv_widget_title, recipe.getName());
        views.setTextViewText(R.id.tv_widget_ingredients, recipe.getIngredientsString());

        views.setOnClickPendingIntent(R.id.btn_widget_prev,
                getPendingSelfIntent(context, PREV_RECIPE_ACTION));
        views.setOnClickPendingIntent(R.id.btn_widget_next,
                getPendingSelfIntent(context, NEXT_RECIPE_ACTION));

        Intent appIntent = new Intent(context, BakingActivity.class);
        appIntent.putExtra(BakingActivity.RECIPE_EXTRA, recipe);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_widget_ingredients, appPendingIntent);
        views.setOnClickPendingIntent(R.id.tv_widget_title, appPendingIntent);


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("onUpdate()");

        int recipeId = WidgetPreferenceHelper.loadRecipeId(context);
        App.getBakingNetworkData().loadRecipeById(context, this, recipeId);
//        Recipe recipe = RecipesLoader.loadRecipeById(context, recipeId);
//
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, recipe, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive" + intent.getAction());

        int recipeId = WidgetPreferenceHelper.loadRecipeId(context);
        int minRecipeId = 1;
        int maxRecipeId = WidgetPreferenceHelper.loadMaxRecipeId(context);
        Timber.d("=============MAX_ID=" + maxRecipeId);
        Timber.d("=============ID=" + recipeId);
        if (PREV_RECIPE_ACTION.equals(intent.getAction())) {
            recipeId = --recipeId < minRecipeId ? maxRecipeId : recipeId;
        } else if (NEXT_RECIPE_ACTION.equals(intent.getAction())) {
            recipeId = ++recipeId > maxRecipeId ? minRecipeId : recipeId;
        }
        Timber.d("=============ID=" + recipeId);

        WidgetPreferenceHelper.updateRecipeId(context, recipeId);
        App.getBakingNetworkData().loadRecipeById(context, this, recipeId);

//        Recipe recipe = RecipesLoader.loadRecipeById(context, recipeId);
//        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
//        int[] widgetIds = widgetManager.getAppWidgetIds(
//                new ComponentName(context, RecipeWidgetProvider.class));
//
//        for (int widgetId : widgetIds) {
//            updateAppWidget(context, widgetManager, recipe, widgetId);
//        }
    }

    private static PendingIntent getPendingSelfIntent(Context context, String action) {
        Timber.d("getPendingSelfIntent()");
        Intent intent = new Intent(context, RecipeWidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void showRecipe(Context context, Recipe recipe) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = widgetManager.getAppWidgetIds(
                new ComponentName(context, RecipeWidgetProvider.class));
        for (int appWidgetId : widgetIds) {
            updateAppWidget(context, widgetManager, recipe, appWidgetId);
        }
    }
}
