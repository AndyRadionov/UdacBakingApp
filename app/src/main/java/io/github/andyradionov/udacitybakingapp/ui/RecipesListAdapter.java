package io.github.andyradionov.udacitybakingapp.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.andyradionov.udacitybakingapp.R;
import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.databinding.ItemRecipeCardBinding;
import io.github.andyradionov.udacitybakingapp.viewmodels.RecipeViewModel;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.RecipeListViewHolder> {

    private Context mContext;
    private Recipe[] mRecipes;
    private OnRecipeItemClickListener mClickListener;

    public interface OnRecipeItemClickListener {
        void onRecipeItemClick(Recipe recipe);
    }

    public RecipesListAdapter(Context context, OnRecipeItemClickListener clickListener,
                              Recipe[] recipes) {
        Timber.d("RecipesListAdapter constructor call");

        mContext = context;
        mClickListener = clickListener;
        mRecipes = recipes;
    }

    @NonNull
    @Override
    public RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ItemRecipeCardBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.item_recipe_card, parent, false);

        return new RecipeListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListViewHolder holder, int position) {
        Timber.d("onBindViewHolder");
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        Timber.d("getItemCount");
        return mRecipes != null ? mRecipes.length : 0;
    }

    public class RecipeListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ItemRecipeCardBinding mBinding;

        RecipeListViewHolder(ItemRecipeCardBinding binding) {
            super(binding.getRoot());

            Timber.d("RecipeListViewHolder Constructor call");
            mBinding = binding;
            mBinding.setRecipeViewModel(new RecipeViewModel());
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Timber.d("bind");

            Recipe recipe = mRecipes[position];

            mBinding.getRecipeViewModel().setRecipe(recipe);
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            Timber.d("onClick Recipe");
            Recipe recipe = mRecipes[getAdapterPosition()];
            mClickListener.onRecipeItemClick(recipe);
        }
    }
}
