package io.github.andyradionov.udacitybakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.andyradionov.udacitybakingapp.data.model.Recipe;
import io.github.andyradionov.udacitybakingapp.data.model.RecipeStep;
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

        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_INGREDIENTS: {
                layoutId = R.layout.item_ingrediends_card;
                break;
            }
            case VIEW_TYPE_STEPS: {
                layoutId = R.layout.item_step_card;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new RecipeStepViewHolder(cardView);
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
        return position == 0 ? VIEW_TYPE_INGREDIENTS : VIEW_TYPE_STEPS;
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private CardView recipeCard;

        RecipeStepViewHolder(CardView itemView) {
            super(itemView);

            Timber.d("RecipeListViewHolder Constructor call");
            recipeCard = itemView;
            itemView.setOnClickListener(this);
        }

        void bindIngredients() {
            Timber.d("Bind Ingredients");
            TextView ingredients = recipeCard.findViewById(R.id.tv_ingredients);
            ingredients.setText(mRecipe.getIngredientsString());
        }
        void bindStep(int position) {
            Timber.d("Bind Step");

            RecipeStep recipeStep = mRecipe.getSteps().get(position - 1);
            TextView stepNumber = recipeCard.findViewById(R.id.tv_step_number);
            TextView stepDescription = recipeCard.findViewById(R.id.tv_step_description);
            stepNumber.setText(mContext.getString(R.string.step_number, position));
            stepDescription.setText(recipeStep.getShortDescription());
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
