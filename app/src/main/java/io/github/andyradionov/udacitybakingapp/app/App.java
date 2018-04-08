package io.github.andyradionov.udacitybakingapp.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import io.github.andyradionov.udacitybakingapp.data.network.BakingApi;
import io.github.andyradionov.udacitybakingapp.data.network.BakingNetworkData;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class App extends Application {

    public static final String BAKING_BASE_URL = "http://go.udacity.com/android-baking-app-json/";


    private static BakingApi sBakingApi;
    private static BakingNetworkData sBakingNetworkData;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        sBakingApi = createApi();
        sBakingNetworkData = new BakingNetworkData();
    }

    public static BakingApi getBakingApi() {
        return sBakingApi;
    }

    public static BakingNetworkData getBakingNetworkData() {
        return sBakingNetworkData;
    }

    /**
     * Check if Internet is Available on device
     *
     * @param context of application
     * @return internet status
     */
    public static boolean isInternetAvailable(Context context) {
        Timber.d("isInternetAvailable");
        ConnectivityManager mConMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return mConMgr != null
                && mConMgr.getActiveNetworkInfo() != null
                && mConMgr.getActiveNetworkInfo().isAvailable()
                && mConMgr.getActiveNetworkInfo().isConnected();
    }

    private static BakingApi createApi() {
        Timber.d("createApi");

        Gson gson = new GsonBuilder()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BAKING_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(BakingApi.class);
    }
}
