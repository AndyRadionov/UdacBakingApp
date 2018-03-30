package io.github.andyradionov.udacitybakingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import timber.log.Timber;

/**
 * @author Andrey Radionov
 */

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.RecipeListViewHolder> {

    private Recipe[] mRecipes;
    private OnRecipeItemClickListener mClickListener;

    public interface OnRecipeItemClickListener {
        void onRecipeItemClick(Recipe recipe);
    }

    public RecipesListAdapter(OnRecipeItemClickListener clickListener, Recipe[] recipes) {
        Timber.d("RecipesListAdapter constructor call");

        mClickListener = clickListener;
        mRecipes = recipes;
    }

    @NonNull
    @Override
    public RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");

        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeListViewHolder(cardView);
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

        private CardView recipeCard;

        RecipeListViewHolder(CardView itemView) {
            super(itemView);

            Timber.d("RecipeListViewHolder Constructor call");
            recipeCard = itemView;
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Timber.d("bind");

            Recipe recipe = mRecipes[position];

            TextView recipeName = recipeCard.findViewById(R.id.tv_recipe_name);
            recipeName.setText(recipe.getName());

        }

        @Override
        public void onClick(View v) {
            Timber.d("onClick Recipe");
            Recipe recipe = mRecipes[getAdapterPosition()];
            mClickListener.onRecipeItemClick(recipe);
        }
    }
}
