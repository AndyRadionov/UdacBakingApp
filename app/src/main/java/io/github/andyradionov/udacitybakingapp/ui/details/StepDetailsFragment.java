package io.github.andyradionov.udacitybakingapp.ui.details;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentStepDetailsBinding;
import timber.log.Timber;


public class StepDetailsFragment extends Fragment {
    private static final String RECIPE_PARAM = "recipe_param";
    private static final String STEP_INDEX_PARAM = "step_index_param";

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

    public static StepDetailsFragment newInstance(Recipe recipe, int stepNumber) {
        Timber.d("newInstance() Step: %d, Recipe: %s", stepNumber, recipe);

        StepDetailsFragment fragment = new StepDetailsFragment();
        fragment.setArgsForStepsDetailsFragment(recipe, stepNumber);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.d("onAttach()");

        try {
            mNavigationHandler = (StepNavigationHandler) context;
        } catch (ClassCastException e) {
            Timber.d("onAttach exception: %s", e.getMessage());
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(RECIPE_PARAM);
            mStepIndex = getArguments().getInt(STEP_INDEX_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        setRetainInstance(true);
        FragmentStepDetailsBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_step_details, container, false);

        setUpButtons(binding.btnPrevStep, binding.btnNextStep);
        setButtonsEnabled(binding.btnPrevStep, binding.btnNextStep);

        RecipeStep recipeStep = mRecipe.getSteps().get(mStepIndex);
        binding.tvRecipeInstructions.setText(recipeStep.getDescription());

        return binding.getRoot();
    }

    protected void setArgsForStepsDetailsFragment(Recipe recipe, int stepNumber) {
        Timber.d("setArgsForStepsDetailsFragment() Step: %d, Recipe: %s", stepNumber, recipe);

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
        Timber.d("setButtonsEnabled()");

        if (mStepIndex == 0) {
            prev.setEnabled(false);
        }
        if (mStepIndex == mRecipe.getSteps().size() - 1) {
            next.setEnabled(false);
        }
    }
}
