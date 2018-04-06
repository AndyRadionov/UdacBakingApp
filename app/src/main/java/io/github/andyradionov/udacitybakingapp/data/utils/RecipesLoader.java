package io.github.andyradionov.udacitybakingapp.data.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.InputStream;
import java.lang.reflect.Type;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class RecipesLoader {

    private static Recipe[] sRecipesCache;

    private RecipesLoader() {
    }

    public static Recipe[] loadFromJsonFile(Context context) {
        Timber.d("loadFromJsonFile()");

        if (sRecipesCache != null) {
            Timber.d("Load Recipes from cache");
            return sRecipesCache;
        }

        Recipe[] result = null;
        try {
            InputStream stream = context.getAssets().open("baking.json");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String sightsJson = new String(buffer);

            GsonBuilder gsonBuilder = new GsonBuilder();
            JsonDeserializer<RecipeStep> deserializer = new StepDeserializer();
            gsonBuilder.registerTypeAdapter(RecipeStep.class, deserializer);
            Gson gson = gsonBuilder.create();

            result = gson.fromJson(sightsJson, Recipe[].class);
            sRecipesCache = result;

        } catch (Exception e) {
            Timber.d("loadFromJsonFile exception: %s", e.getMessage());
            Toast.makeText(context, R.string.recipes_list_load_error, Toast.LENGTH_LONG)
                    .show();
        }
        return result == null ? new Recipe[0] : result;
    }

    public static Recipe loadRecipeById(Context context, int recipeId) {
        Timber.d("loadRecipeById(): %d", recipeId);

        if (sRecipesCache == null) {
            loadFromJsonFile(context);
        }

        for (Recipe recipe : sRecipesCache) {
            if (recipe.getId() == recipeId) {
                return recipe;
            }
        }
        Timber.d("Return empty Recipe");
        return new Recipe();
    }

    private static class StepDeserializer implements JsonDeserializer<RecipeStep> {

        @Override
        public RecipeStep deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            RecipeStep step = new RecipeStep();

            int id = jsonObject.get("id").getAsInt();
            String shortDescription = jsonObject.get("shortDescription").getAsString();
            String description = jsonObject.get("description").getAsString();
            description = description.replaceAll("^\\d+.\\s+", "");
            String videoURL = jsonObject.get("videoURL").getAsString();
            String thumbnailURL = jsonObject.get("thumbnailURL").getAsString();

            step.setId(id);
            step.setShortDescription(shortDescription);
            step.setDescription(description);
            step.setVideoURL(videoURL);
            step.setThumbnailURL(thumbnailURL);

            if (TextUtils.isEmpty(videoURL) && thumbnailURL.endsWith(".mp4")) {
                step.setVideoURL(thumbnailURL);
                step.setThumbnailURL("");
            }
            return step;
        }
    }
}
