package io.github.andyradionov.udacitybakingapp.ui.details;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.components.VideoPlayerComponent;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import io.github.andyradionov.udacitybakingapp.data.utils.ImageHelper;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentStepDetailsVideoBinding;
import io.github.andyradionov.udacitybakingapp.ui.steps.BakingActivity;
import io.github.andyradionov.udacitybakingapp.viewmodels.VideoPlayerViewModel;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class StepDetailsVideoFragment extends StepDetailsFragment implements LifecycleOwner {

    private VideoPlayerViewModel mVideoPlayerViewModel;
    private BakingActivity mActivity;

    public StepDetailsVideoFragment() {
    }

    public static StepDetailsVideoFragment newInstance(Recipe recipe, int stepNumber) {
        Timber.d("setButtonsEnabled() Step: %d, Recipe: %s", stepNumber, recipe);

        StepDetailsVideoFragment fragment = new StepDetailsVideoFragment();
        fragment.setArgsForStepsDetailsFragment(recipe, stepNumber);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BakingActivity) context;
        mVideoPlayerViewModel = ViewModelProviders
                .of(mActivity)
                .get(VideoPlayerViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        setRetainInstance(true);
        FragmentStepDetailsVideoBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_step_details_video, container, false);

        final RecipeStep recipeStep = mRecipe.getSteps().get(mStepIndex);

        setUpButtons(binding.btnPrevStep, binding.btnNextStep);
        setButtonsEnabled(binding.btnPrevStep, binding.btnNextStep);
        binding.tvRecipeInstructions.setText(recipeStep.getDescription());

        if (!TextUtils.isEmpty(recipeStep.getThumbnailURL())) {
            ImageHelper.loadImageIntoView(binding.ivStepDetailsImage, recipeStep.getThumbnailURL());
            binding.ivStepDetailsImage.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(recipeStep.getVideoURL())) {
            mVideoPlayerViewModel.getVideoUrl().setValue(recipeStep.getVideoURL());
        }
        getLifecycle().addObserver(new VideoPlayerComponent(
                getContext(), binding, mVideoPlayerViewModel, mActivity.getIdlingResource()));

        return binding.getRoot();
    }
}
