package io.github.andyradionov.udacitybakingapp.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.components.VideoPlayerComponent;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentStepDetailsVideoBinding;
import io.github.andyradionov.udacitybakingapp.viewmodels.VideoPlayerViewModel;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class StepDetailsVideoFragment extends StepDetailsFragment implements LifecycleOwner {

    VideoPlayerViewModel mModel;

    public StepDetailsVideoFragment() {
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
        setRetainInstance(true);
        FragmentStepDetailsVideoBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_step_details_video, container, false);

        final RecipeStep recipeStep = mRecipe.getSteps().get(mStepIndex);

        setUpButtons(binding.btnPrevStep, binding.btnNextStep);
        setButtonsEnabled(binding.btnPrevStep, binding.btnNextStep);
        binding.tvRecipeInstructions.setText(recipeStep.getDescription());

        mModel = ViewModelProviders.of(this).get(VideoPlayerViewModel.class);
        mModel.getVideoUrl().setValue(recipeStep.getVideoURL());

        getLifecycle().addObserver(new VideoPlayerComponent(
                getContext(), binding, mModel));

        return binding.getRoot();
    }
}
