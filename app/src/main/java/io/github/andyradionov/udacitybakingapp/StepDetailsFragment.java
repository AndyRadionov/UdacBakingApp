package io.github.andyradionov.udacitybakingapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import io.github.andyradionov.udacitybakingapp.databinding.FragmentStepDetailsBinding;
import timber.log.Timber;


public class StepDetailsFragment extends Fragment {
    private static final String RECIPE_PARAM = "recipe_param";
    private static final String STEP_INDEX_PARAM = "step_index_param";

    private FragmentStepDetailsBinding mBinding;
    private StepNavigationHandler mNavigationHandler;

    protected Recipe mRecipe;
    protected int mStepIndex;

    public StepDetailsFragment() {
        // Required empty public constructor
    }

    public interface StepNavigationHandler {
        void onPreviousClick();
        void onNextClick();
    }

    public static StepDetailsFragment newInstance(Recipe recipe, int stepIndex) {
        StepDetailsFragment fragment = new StepDetailsFragment();
        fragment.setArgsForStepsDetailsFragment(recipe, stepIndex);
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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_step_details, container, false);

        setUpButtons(mBinding.btnPrevStep, mBinding.btnNextStep);
        setButtonsEnabled(mBinding.btnPrevStep, mBinding.btnNextStep);


        RecipeStep recipeStep = mRecipe.getSteps().get(mStepIndex);
        mBinding.tvRecipeInstructions.setText(recipeStep.getDescription());

        return mBinding.getRoot();
    }

    protected void setArgsForStepsDetailsFragment(Recipe recipe, int stepNumber) {
        Bundle args = new Bundle();
        args.putParcelable(RECIPE_PARAM, recipe);
        args.putInt(STEP_INDEX_PARAM, stepNumber);
        this.setArguments(args);
    }

    protected void setUpButtons(Button prev, Button next) {

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationHandler.onPreviousClick();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationHandler.onNextClick();
            }
        });
    }

    protected void setButtonsEnabled(Button prev, Button next) {
        if (mStepIndex == 0) {
            prev.setEnabled(false);
        }
        if (mStepIndex == mRecipe.getSteps().size() - 1) {
            next.setEnabled(false);
        }
    }
}
