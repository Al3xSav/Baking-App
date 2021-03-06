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

package com.alexsav.baking_app.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.models.Recipes;
import com.alexsav.baking_app.utils.PreferencesUtils;


public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private Recipes recipes;

    public ListRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        recipes = PreferencesUtils.loadRecipes(mContext);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return recipes.getIngredients().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_recipes_list_item);
        row.setTextViewText(R.id.textViewIngredientItem, recipes.getIngredients().get(position).getIngredient());
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
