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

package com.alexsav.baking_app.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.adapters.StepsAdapter;
import com.alexsav.baking_app.models.Recipes;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alexsav.baking_app.utils.ConnectionUtils.makeSnackBar;


public class StepDetailsActivity extends AppCompatActivity {

    public static final String RECIPE_KEY = "recipe_k";
    public static final String STEP_SELECTED_KEY = "step_k";
    // Bind the tab layout to its variable
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    // Bind the step list of the recipes
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    // Bind the activity of this content
    @BindView(android.R.id.content)
    View mParentLayout;
    private Recipes mRecipes;
    private int mStepSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(RECIPE_KEY) && bundle.containsKey(STEP_SELECTED_KEY)) {
            mRecipes = bundle.getParcelable(RECIPE_KEY);
            mStepSelectedPosition = bundle.getInt(STEP_SELECTED_KEY);
        } else {
            makeSnackBar(mParentLayout, getString(R.string.load_recipe_error));
            finish();
        }

        // Show the Up button in the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipes.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        StepsAdapter adapter = new
                StepsAdapter(getApplicationContext(), mRecipes.getSteps(), getSupportFragmentManager());

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    actionBar.setTitle(mRecipes.getSteps().get(position).getShortDescription());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setCurrentItem(mStepSelectedPosition);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy");
    }
}
