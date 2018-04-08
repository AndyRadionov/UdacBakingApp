package io.github.andyradionov.udacitybakingapp.components;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.net.Uri;
import android.support.test.espresso.IdlingResource;
import android.view.View;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import io.github.andyradionov.udacitybakingapp.IdlingResource.SimpleIdlingResource;
import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.app.App;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentStepDetailsVideoBinding;
import io.github.andyradionov.udacitybakingapp.viewmodels.VideoPlayerViewModel;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class VideoPlayerComponent implements LifecycleObserver, Player.EventListener {

    private final Context mContext;
    private final FragmentStepDetailsVideoBinding mBinding;
    private VideoPlayerViewModel mViewModel;

    private SimpleExoPlayer mExoPlayer;
    private DefaultTrackSelector mTrackSelector;
    private SimpleIdlingResource mIdlingResource;
    public VideoPlayerComponent(Context context, FragmentStepDetailsVideoBinding binding,
                                VideoPlayerViewModel viewModel,
                                IdlingResource idlingResource) {
        Timber.d("VideoPlayerComponent constructor call");

        mContext = context;
        mBinding = binding;
        mViewModel = viewModel;
        mIdlingResource = (SimpleIdlingResource) idlingResource;
        mBinding.btnRefresh.setOnClickListener(v -> initializeVideo());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Timber.d("onCreate()");
        mBinding.playerView.requestFocus();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (Util.SDK_INT > 23) {
            Timber.d("onStart()");
            initializeVideo();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (Util.SDK_INT > 23) {
            Timber.d("onStop()");
            releasePlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (Util.SDK_INT <= 23) {
            Timber.d("onPause()");
            releasePlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if ((Util.SDK_INT <= 23)) {
            Timber.d("onResume()");
            initializeVideo();
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Timber.d("onPlayerStateChanged() state: %d", playbackState);

        if (playbackState == Player.STATE_BUFFERING) {
            setPlayerState(false, View.VISIBLE);
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(false);
            }
        } else if (playbackState == Player.STATE_READY){
            setPlayerState(true, View.INVISIBLE);
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        Timber.d("onPlayerError() error: %s", e.getMessage());

        if (!App.isInternetAvailable(mContext)) {
            showPlayerErrorMessage(mContext.getString(R.string.error_no_internet));
        } else {
            showPlayerErrorMessage(mContext.getString(R.string.error_loading_video));
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    private void initializeVideo() {
        Timber.d("initializeVideo()");

        if (App.isInternetAvailable(mContext)) {
            setErrorViewsVisibility(true, View.INVISIBLE);
            initializePlayer();
        } else {
            showPlayerErrorMessage(mContext.getString(R.string.error_no_internet));
        }
    }

    private void initializePlayer() {
        if (mExoPlayer == null) {
            Timber.d("initializePlayer()");

            mTrackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, mTrackSelector);

            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(mContext, mContext.getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(
                    mContext, null, new DefaultHttpDataSourceFactory(userAgent, null)))
                    .createMediaSource(Uri.parse(mViewModel.getVideoUrl().getValue()));
            mBinding.playerView.setPlayer(mExoPlayer);


            mExoPlayer.prepare(mediaSource);
            if (mViewModel.getPosition().getValue() != null) {
                mExoPlayer.seekTo(mViewModel.getPosition().getValue());
            }

        }
    }

    private void releasePlayer() {
        if (mBinding.playerView != null) {
            Timber.d("releasePlayer()");
            if (mExoPlayer != null) {
                updateResumePosition();
                mExoPlayer.stop();
                mExoPlayer.release();
                mExoPlayer = null;
                mTrackSelector = null;
            }
        }
    }

    private void showPlayerErrorMessage(String error) {
        Timber.d("showPlayerErrorMessage()");
        mBinding.tvVideoError.setText(error);
        setErrorViewsVisibility(false, View.VISIBLE);
    }

    private void setPlayerState(boolean isPlayerControlEnabled, int loadingIndicatorVisibility) {
        Timber.d("setPlayerState()");
        mBinding.pbVideoIndicator.setVisibility(loadingIndicatorVisibility);
        mBinding.playerControl.setEnabled(isPlayerControlEnabled);
        mBinding.playerControl.setEnabled(isPlayerControlEnabled);
    }

    private void setErrorViewsVisibility(boolean isPlayerControlEnabled, int visibility) {
        Timber.d("setErrorViewsVisibility()");
        mBinding.playerControl.setEnabled(isPlayerControlEnabled);
        mBinding.tvVideoError.setVisibility(visibility);
        mBinding.btnRefresh.setVisibility(visibility);
    }

    private void updateResumePosition() {
        Timber.d("updateResumePosition()");
        mViewModel.getPosition().setValue(Math.max(0L, mExoPlayer.getCurrentPosition()));
    }
}
