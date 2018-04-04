package io.github.andyradionov.udacitybakingapp.ui.steps;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentRecipeStepsBinding;
import io.github.andyradionov.udacitybakingapp.viewmodels.BakingViewModel;
import timber.log.Timber;


public class RecipeStepsFragment extends Fragment {

    private RecipeStepsAdapter.OnStepItemClickListener mStepItemClickListener;
    private BakingViewModel mBakingViewModel;

    public RecipeStepsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.d("onAttach()");

        try {
            mStepItemClickListener = (RecipeStepsAdapter.OnStepItemClickListener) context;
            mBakingViewModel = ViewModelProviders.of((FragmentActivity) context)
                    .get(BakingViewModel.class);
        } catch (ClassCastException e) {
            Timber.d("onAttach() exception: %s", e.getMessage());
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("RecipeStepsFragment onCreateView");
        setRetainInstance(true);

        FragmentRecipeStepsBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_recipe_steps, container, false);

        RecipeStepsAdapter adapter = new RecipeStepsAdapter(getContext(), mStepItemClickListener,
                mBakingViewModel.getRecipe().getValue());
        binding.rvStepsContainer.setAdapter(adapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvStepsContainer.setLayoutManager(layoutManager);
        return binding.getRoot();
    }
}
