package io.github.andyradionov.udacitybakingapp.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class VideoPlayerViewModel extends ViewModel {
    private MutableLiveData<Long> mPosition;
    private MutableLiveData<String> mVideoUrl;

    public VideoPlayerViewModel() {
    }

    public MutableLiveData<Long> getPosition() {
        Timber.d("getPosition()");
        if (mPosition == null) {
            mPosition = new MutableLiveData<>();
        }
        return mPosition;
    }

    public MutableLiveData<String> getVideoUrl() {
        Timber.d("getVideoUrl()");
        if (mVideoUrl == null) {
            mVideoUrl = new MutableLiveData<>();
        }
        return mVideoUrl;
    }
}
