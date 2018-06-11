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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.models.Steps;
import com.alexsav.baking_app.ui.fragments.RecipeStepDetailFragment;

import java.util.List;

public class StepsAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final List<Steps> steps;

    public StepsAdapter(Context context, List<Steps> steps, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        this.steps = steps;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(RecipeStepDetailFragment.STEP_KEY, steps.get(position));
        RecipeStepDetailFragment recipeStepDetailFragment = new RecipeStepDetailFragment();
        recipeStepDetailFragment.setArguments(arguments);
        return recipeStepDetailFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(context.getString(R.string.step), position);
    }

    @Override
    public int getCount() {

        return steps.size();
    }

}
