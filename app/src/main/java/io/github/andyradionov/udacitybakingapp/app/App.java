package io.github.andyradionov.udacitybakingapp.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
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
}
