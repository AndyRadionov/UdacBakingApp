package io.github.andyradionov.udacitybakingapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import timber.log.Timber;

import static android.content.Context.NOTIFICATION_SERVICE;


public class StepDetailsFragment extends Fragment implements Player.EventListener {
    private static final String RECIPE_PARAM = "recipe_param";
    private static final String STEP_INDEX_PARAM = "step_index_param";

    private Button mPreviousButton;
    private Button mNextButton;
    private StepNavigationHandler mNavigationHandler;

    private Recipe mRecipe;
    private int mStepIndex;
    private boolean mHasVideo;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    public StepDetailsFragment() {
        // Required empty public constructor
    }

    public interface StepNavigationHandler {
        void onPreviousClick();
        void onNextClick();
    }

    public static StepDetailsFragment newInstance(Recipe recipe, int stepIndex) {
        StepDetailsFragment fragment = new StepDetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable(RECIPE_PARAM, recipe);
        args.putInt(STEP_INDEX_PARAM, stepIndex);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mNavigationHandler = (StepNavigationHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(RECIPE_PARAM);
            mStepIndex = getArguments().getInt(STEP_INDEX_PARAM);
            mHasVideo = !TextUtils.isEmpty(mRecipe.getSteps().get(mStepIndex).getVideoURL());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        int layoutId = mHasVideo ?
                R.layout.fragment_step_details_video :
                R.layout.fragment_step_details;

        final View rootView = inflater.inflate(layoutId, container, false);

        setUpButtons(rootView);
        setButtonsEnabled();

        RecipeStep recipeStep = mRecipe.getSteps().get(mStepIndex);
        TextView stepInstructions = rootView.findViewById(R.id.tv_recipe_instructions);
        stepInstructions.setText(recipeStep.getDescription());
        if (mHasVideo) {
            mPlayerView = rootView.findViewById(R.id.player_view);
            initializeMediaSession();
            initializePlayer(Uri.parse(recipeStep.getVideoURL()));
        }
        return rootView;
    }

    private void setUpButtons(View rootView) {
        mPreviousButton = rootView.findViewById(R.id.btn_prev_step);
        mNextButton = rootView.findViewById(R.id.btn_next_step);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationHandler.onPreviousClick();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationHandler.onNextClick();
            }
        });
    }

    private void setButtonsEnabled() {
        if (mStepIndex == 0) {
            mPreviousButton.setEnabled(false);
        }
        if (mStepIndex == mRecipe.getSteps().size() - 1) {
            mNextButton.setEnabled(false);
        }
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
        //mMediaSession.setCallback(new MediaCallback());
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext(),
                    null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "UdacityBakingApp");
            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(
                    getContext(), null, new DefaultHttpDataSourceFactory(userAgent, null)))
                    .createMediaSource(mediaUri);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        mNotificationManager.cancelAll();
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHasVideo) {
            releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHasVideo) {
            mExoPlayer.stop();
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
        if((playbackState == Player.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == Player.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        //showNotification(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

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

//    private class MediaCallback extends MediaSessionCompat.Callback {
//        @Override
//        public void onPlay() {
//            mExoPlayer.setPlayWhenReady(true);
//        }
//
//        @Override
//        public void onPause() {
//            mExoPlayer.setPlayWhenReady(false);
//        }
//
//        @Override
//        public void onSkipToPrevious() {
//            mExoPlayer.seekTo(0);
//        }
//    }
//
//    public static class RecipeMediaBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            MediaButtonReceiver.handleIntent(mMediaSession, intent);
//        }
//    }
//
//    private void showNotification(PlaybackStateCompat state) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
//
//        int icon;
//        String play_pause;
//        if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
//            icon = R.drawable.exo_controls_pause;
//            play_pause = getString(R.string.pause);
//        } else {
//            icon = R.drawable.exo_controls_play;
//            play_pause = getString(R.string.play);
//        }
//
//
//        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
//                icon, play_pause,
//                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
//                        PlaybackStateCompat.ACTION_PLAY_PAUSE));
//
//        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
//                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
//                MediaButtonReceiver.buildMediaButtonPendingIntent
//                        (getContext(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
//
//        PendingIntent contentPendingIntent = PendingIntent.getActivity
//                (getContext(), 0, new Intent(getContext(), DetailsActivity.class), 0);
//
//
//        builder.setContentTitle("TODODO")
//                .setContentText("TOTOTODO")
//                .setContentIntent(contentPendingIntent)
//                .setSmallIcon(R.drawable.ic_music_note)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .addAction(restartAction)
//                .addAction(playPauseAction)
//                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(mMediaSession.getSessionToken())
//                        .setShowActionsInCompactView(0,1));
//
//
//        mNotificationManager = (NotificationManager) getContext()
//                .getSystemService(NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, builder.build());
//    }
}
