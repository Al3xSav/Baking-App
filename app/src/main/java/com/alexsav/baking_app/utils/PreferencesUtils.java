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

package com.alexsav.baking_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.models.Recipes;

public class PreferencesUtils {
    public static final String PREFERENCES = "preferences";

    public static void saveRecipe(Context context, Recipes recipes) {
        SharedPreferences.Editor sharedPreferences =
                context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(context.getString(R.string.widget_key), Recipes.toBase64String(recipes));
        sharedPreferences.apply();
    }

    public static Recipes loadRecipes(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String recipeBase = sharedPreferences.getString(context.getString(R.string.widget_key), "");

        if ("".equals(recipeBase)) {
            return null;
        } else {
            return Recipes.fromBase64(sharedPreferences.getString(context.getString(R.string.widget_key), ""));
        }
    }
}
