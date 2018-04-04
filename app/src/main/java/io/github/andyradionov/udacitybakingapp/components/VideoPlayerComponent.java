package io.github.andyradionov.udacitybakingapp.components;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.net.Uri;
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

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.app.App;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentStepDetailsVideoBinding;
import io.github.andyradionov.udacitybakingapp.viewmodels.VideoPlayerViewModel;

/**
 * @author Andrey Radionov
 */

public class VideoPlayerComponent implements LifecycleObserver, Player.EventListener {

    private final Context mContext;
    private final FragmentStepDetailsVideoBinding mBinding;
    private VideoPlayerViewModel mViewModel;

    private SimpleExoPlayer mExoPlayer;
    private DefaultTrackSelector mTrackSelector;

    public VideoPlayerComponent(Context context, FragmentStepDetailsVideoBinding binding, VideoPlayerViewModel viewModel) {
        mContext = context;
        mBinding = binding;
        mViewModel = viewModel;

        mBinding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeVideo();
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        mBinding.playerView.requestFocus();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (Util.SDK_INT > 23) {
            initializeVideo();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if ((Util.SDK_INT <= 23)) {
            initializeVideo();
        }
    }

    public void initializeVideo() {
        if (App.isInternetAvailable(mContext)) {
            hidePlayerErrorMessage();
            initializePlayer();
        } else {
            showPlayerErrorMessage(mContext.getString(R.string.error_no_internet));
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
        if (playbackState == Player.STATE_BUFFERING) {
            mBinding.pbVideoIndicator.setVisibility(View.VISIBLE);
            mBinding.playerControl.setEnabled(false);
            mBinding.playerControl.setEnabled(false);
        } else {
            mBinding.pbVideoIndicator.setVisibility(View.INVISIBLE);
            mBinding.playerControl.setEnabled(true);
            mBinding.playerControl.setEnabled(true);
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

    private void releasePlayer() {
        if (mBinding.playerView != null) {
            updateResumePosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
            mTrackSelector = null;
        }
    }

    private void showPlayerErrorMessage(String error) {
        mBinding.playerControl.setEnabled(false);
        mBinding.tvVideoError.setText(error);
        mBinding.tvVideoError.setVisibility(View.VISIBLE);
        mBinding.btnRefresh.setVisibility(View.VISIBLE);
    }

    private void hidePlayerErrorMessage() {
        mBinding.playerControl.setEnabled(true);
        mBinding.tvVideoError.setVisibility(View.INVISIBLE);
        mBinding.btnRefresh.setVisibility(View.INVISIBLE);
    }

    private void initializePlayer() {
        if (mExoPlayer == null) {
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

    private void updateResumePosition() {
        mViewModel.getPosition().setValue(Math.max(0L, mExoPlayer.getCurrentPosition()));
    }
}
