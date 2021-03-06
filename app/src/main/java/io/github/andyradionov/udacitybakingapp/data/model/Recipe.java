package io.github.andyradionov.udacitybakingapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Andrey Radionov
 */

public class Recipe implements Parcelable {
    private int id;
    private String name;
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;
    private int servings;
    private String image;

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ingredients = in.createTypedArrayList(RecipeIngredient.CREATOR);
        steps = in.createTypedArrayList(RecipeStep.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIngredientsString() {
        StringBuilder ingredientsListBuilder = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            RecipeIngredient ingredient = ingredients.get(i);
            ingredientsListBuilder
                    .append(i + 1)
                    .append(". ")
                    .append(capitalizeFirstLetter(ingredient.getIngredient()))
                    .append(" - ")
                    .append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getMeasure())
                    .append("\n");
        }
        return ingredientsListBuilder.toString().trim();
    }

    public String getVideoUrlForStep(int stepNumber) {
        if (steps != null && !steps.isEmpty()) {
            return getSteps().get(stepNumber).getVideoURL();
        }
        return "";
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", servings=" + servings +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    private String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
