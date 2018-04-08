package io.github.andyradionov.udacitybakingapp.data.network;

import java.util.List;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @author Andrey Radionov
 */

public interface BakingApi {

    @GET(".")
    Observable<List<Recipe>> getBakingRecipes();
}
