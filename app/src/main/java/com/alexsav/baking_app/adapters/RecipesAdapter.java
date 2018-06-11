/*
Copyright 2018 Savtsouk Alexios

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.alexsav.baking_app.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.holders.IngredientsViewHolder;
import com.alexsav.baking_app.holders.StepsViewHolder;
import com.alexsav.baking_app.models.Ingredients;
import com.alexsav.baking_app.models.Recipes;
import com.alexsav.baking_app.ui.Listeners;

import java.util.Locale;

public class RecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Recipes recipes;
    private final Listeners.OnItemClickListener itemClickListener;

    public RecipesAdapter(Recipes recipes, Listeners.OnItemClickListener onItemClickListener) {
        this.recipes = recipes;
        this.itemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create View and inflate ingredients item and steps item
        if (viewType == 0) { // Ingredients
            return new IngredientsViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_item_recipe_ingredient, viewGroup, false));
        } else { // Steps
            return new StepsViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_item_recipe_steps, viewGroup, false));
        }

    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof IngredientsViewHolder) {
            IngredientsViewHolder viewHolder = (IngredientsViewHolder) holder;
            StringBuilder ingredientsBuilder = new StringBuilder();
            for (int i = 0; i < recipes.getIngredients().size(); i++) {
                Ingredients ingredients = recipes.getIngredients().get(i);
                ingredientsBuilder.
                        append(String.format(Locale.getDefault(),
                                "• %s (%d %s)",
                                ingredients.getIngredient(),
                                ingredients.getQuantity(),
                                ingredients.getMeasure()));
                if (i != recipes.getIngredients().size() - 1)
                    ingredientsBuilder.append("\n");
            }
            viewHolder.textViewIngredients.setText(ingredientsBuilder.toString());

        } else if (holder instanceof StepsViewHolder) {
            StepsViewHolder viewHolder = (StepsViewHolder) holder;
            viewHolder.textViewStepCounter.setText(String.valueOf(position - 1) + ".");
            viewHolder.textViewStepTitle.setText(recipes.getSteps().get(position - 1).getShortDescription());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(position - 1);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {

        return recipes.getSteps().size() + 1;
    }
}
