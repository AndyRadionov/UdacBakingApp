package io.github.andyradionov.udacitybakingapp.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.Log;

import io.github.andyradionov.udacitybakingapp.RecipesData;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    private static RecipesData sRecipesData;
    private static Context sContext;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        sRecipesData = new RecipesData();
        sContext = getApplicationContext();

        Timber.plant(new Timber.DebugTree());

    }



    public static RecipesData getRecipesData() {
        return sRecipesData;
    }

    public static Context getAppContext() {
        return sContext;
    }

    /**
     * Check if Internet is Available on device
     *
     * @param context of application
     * @return internet status
     */
    public static boolean isInternetAvailable(Context context) {
        Log.d(TAG, "isInternetAvailable");
        ConnectivityManager mConMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return mConMgr != null
                && mConMgr.getActiveNetworkInfo() != null
                && mConMgr.getActiveNetworkInfo().isAvailable()
                && mConMgr.getActiveNetworkInfo().isConnected();
    }
}
