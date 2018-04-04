package io.github.andyradionov.udacitybakingapp.ui.steps;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.databinding.ItemIngredientsCardBinding;
import io.github.andyradionov.udacitybakingapp.databinding.ItemStepCardBinding;
import io.github.andyradionov.udacitybakingapp.viewmodels.RecipeViewModel;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepViewHolder> {

    private static final int VIEW_TYPE_INGREDIENTS = 0;
    private static final int VIEW_TYPE_STEPS = 1;

    private Context mContext;
    private Recipe mRecipe;
    private OnStepItemClickListener mClickListener;

    public interface OnStepItemClickListener {
        void onStepItemClick(int stepIndex);
    }

    public RecipeStepsAdapter(Context context, OnStepItemClickListener clickListener, Recipe recipe) {
        Timber.d("Constructor call");

        mContext = context;
        mClickListener = clickListener;
        mRecipe = recipe;
    }

    @NonNull
    @Override
    public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");

        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecipeViewModel viewModel = new RecipeViewModel(mRecipe);
        switch (viewType) {
            case VIEW_TYPE_INGREDIENTS: {
                ItemIngredientsCardBinding binding = DataBindingUtil
                        .inflate(inflater, R.layout.item_ingredients_card, parent, false);
                binding.setRecipeViewModel(viewModel);
                return new RecipeStepViewHolder(binding);
            }
            case VIEW_TYPE_STEPS: {
                ItemStepCardBinding binding = DataBindingUtil
                        .inflate(inflater, R.layout.item_step_card, parent, false);
                binding.setRecipeViewModel(viewModel);
                return new RecipeStepViewHolder(binding);
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepViewHolder holder, int position) {
        Timber.d("onBindViewHolder");

        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_INGREDIENTS: {
                holder.bindIngredients();
                break;
            }
            case VIEW_TYPE_STEPS: {
                holder.bindStep(position);
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public int getItemCount() {
        Timber.d("getItemCount()");
        int count = 0;
        if (mRecipe != null) {
            if (mRecipe.getIngredients() != null && !mRecipe.getIngredients().isEmpty()) {
                count++;
            }
            if (mRecipe.getSteps() != null && !mRecipe.getIngredients().isEmpty()) {
                count += mRecipe.getSteps().size();
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        Timber.d("getItemViewType() for position: %d", position);
        return position == 0 ? VIEW_TYPE_INGREDIENTS : VIEW_TYPE_STEPS;
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ViewDataBinding mBinding;

        RecipeStepViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            Timber.d("RecipeListViewHolder Constructor call");

            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        void bindIngredients() {
            Timber.d("bindIngredients()");
            mBinding.executePendingBindings();
        }

        void bindStep(int position) {
            Timber.d("bindStep() for position: %d", position);
            ((ItemStepCardBinding) mBinding).getRecipeViewModel().setStepNumber(position);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            Timber.d("onClick Recipe");
            if (getAdapterPosition() > 0) {
                mClickListener.onStepItemClick(getAdapterPosition() - 1);
            }
        }
    }
}
