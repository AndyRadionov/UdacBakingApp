package io.github.andyradionov.udacitybakingapp;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import io.github.andyradionov.udacitybakingapp.app.App;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import io.github.andyradionov.udacitybakingapp.databinding.ExoPlaybackControlViewBinding;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentStepDetailsVideoBinding;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class StepDetailsVideoFragment extends StepDetailsFragment implements Player.EventListener {

    private FragmentStepDetailsVideoBinding mBinding;
    private ExoPlaybackControlViewBinding mPlayerBinding;

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public StepDetailsVideoFragment() {
        // Required empty public constructor
    }

    public static StepDetailsVideoFragment newInstance(Recipe recipe, int stepIndex) {
        StepDetailsVideoFragment fragment = new StepDetailsVideoFragment();
        fragment.setArgsForStepsDetailsFragment(recipe, stepIndex);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_step_details_video, container, false);
        mPlayerBinding = DataBindingUtil
                .inflate(inflater, R.layout.exo_playback_control_view, container, false);

        setUpButtons(mBinding.btnPrevStep, mBinding.btnNextStep);
        setButtonsEnabled(mBinding.btnPrevStep, mBinding.btnNextStep);

        final RecipeStep recipeStep = mRecipe.getSteps().get(mStepIndex);
        mBinding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeVideo(recipeStep);
            }
        });
        mBinding.tvRecipeInstructions.setText(recipeStep.getDescription());

        initializeVideo(recipeStep);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releasePlayer();

    }

    @Override
    public void onPause() {
        super.onPause();

        mExoPlayer.stop();

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
            mPlayerBinding.exoPlay.setEnabled(false);
            mPlayerBinding.exoPrev.setEnabled(false);
        } else {
            mBinding.pbVideoIndicator.setVisibility(View.INVISIBLE);
            mPlayerBinding.exoPlay.setEnabled(true);
            mPlayerBinding.exoPrev.setEnabled(true);
        }

        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (!App.isInternetAvailable(getContext())) {
            showPlayerErrorMessage(getString(R.string.error_no_internet));
        } else {
            showPlayerErrorMessage(getString(R.string.error_loading_video));
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

    private void initializeVideo(RecipeStep recipeStep) {
        if (App.isInternetAvailable(getContext())) {
            hidePlayerErrorMessage();
            initializeMediaSession();
            initializeExoPlayer(Uri.parse(recipeStep.getVideoURL()));
        } else {
            showPlayerErrorMessage(getString(R.string.error_no_internet));
        }
    }

    private void showPlayerErrorMessage(String error) {
        mBinding.tvVideoError.setText(error);
        mBinding.tvVideoError.setVisibility(View.VISIBLE);
        mBinding.btnRefresh.setVisibility(View.VISIBLE);
    }

    private void hidePlayerErrorMessage() {
        mBinding.tvVideoError.setVisibility(View.INVISIBLE);
        mBinding.btnRefresh.setVisibility(View.INVISIBLE);
    }


    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), "//TODO");

        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setActive(true);
    }

    private void initializeExoPlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext(),
                    null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            mBinding.playerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(
                    getContext(), null, new DefaultHttpDataSourceFactory(userAgent, null)))
                    .createMediaSource(mediaUri);

            mExoPlayer.prepare(mediaSource);
            //todo автостарт
//            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }
}
