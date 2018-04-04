package io.github.andyradionov.udacitybakingapp.ui;

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
        setRetainInstance(true);
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
