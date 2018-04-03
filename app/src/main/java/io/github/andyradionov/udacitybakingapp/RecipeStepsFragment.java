package io.github.andyradionov.udacitybakingapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
import io.github.andyradionov.udacitybakingapp.databinding.FragmentRecipeStepsBinding;
import timber.log.Timber;


public class RecipeStepsFragment extends Fragment {

    private static final String RECIPE_PARAM = "recipe_param";

    private Recipe mRecipe;
    private RecipeStepsAdapter.OnStepItemClickListener mStepItemClickListener;

    public RecipeStepsFragment() {
        // Required empty public constructor
    }

    public static RecipeStepsFragment newInstance(Recipe recipe) {
        Timber.d("RecipeStepsFragment newInstance");
        RecipeStepsFragment fragment = new RecipeStepsFragment();

        Bundle args = new Bundle();
        args.putParcelable(RECIPE_PARAM, recipe);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mStepItemClickListener = (RecipeStepsAdapter.OnStepItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.d("RecipeStepsFragment onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(RECIPE_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("RecipeStepsFragment onCreateView");
        FragmentRecipeStepsBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_recipe_steps, container, false);

        RecipeStepsAdapter adapter = new RecipeStepsAdapter(getContext(), mStepItemClickListener, mRecipe);
        binding.rvStepsContainer.setAdapter(adapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvStepsContainer.setLayoutManager(layoutManager);

        return binding.getRoot();
    }
}
