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
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.holders.RecipeViewHolder;
import com.alexsav.baking_app.models.Recipes;
import com.alexsav.baking_app.ui.Listeners;
import com.alexsav.baking_app.utils.GlideApp;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class BakingAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private final Context context;
    private final List<Recipes> recipes;
    private final Listeners.OnItemClickListener itemClickListener;

    public BakingAdapter(Context context, List<Recipes> recipes, Listeners.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.recipes = recipes;
        this.itemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create View and inflate the recipe_list_item into it
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, final int position) {
        recipeViewHolder.textViewRecipeTitle.setText(recipes.get(position).getName());
        recipeViewHolder.textViewServings.setText(context.getString(R.string.servings, recipes.get(position).getServings()));

        String recipeImage = recipes.get(position).getImage();
        if (!recipeImage.isEmpty()) {
            GlideApp.with(context)
                    .load(recipeImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_recipes_main_image)
                    .into(recipeViewHolder.imageViewRecipe);
        }

        recipeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }


}
