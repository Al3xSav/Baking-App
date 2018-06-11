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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.adapters.RecipesAdapter;
import com.alexsav.baking_app.models.Recipes;
import com.alexsav.baking_app.ui.Listeners;
import com.alexsav.baking_app.ui.fragments.RecipeStepDetailFragment;
import com.alexsav.baking_app.widget.AppWidgetService;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alexsav.baking_app.utils.ConnectionUtils.makeSnackBar;


public class RecipesInfoActivity extends AppCompatActivity {

    public static final String RECIPE_KEY = "recipe_key";

    // Bind the step list of the recipes
    @BindView(R.id.recyclerViewStepList)
    RecyclerView mRecyclerViewStepList;

    // Bind the activity of this content
    @BindView(android.R.id.content)
    View mParentLayout;

    private boolean doubleFragmentPanel;
    private Recipes mRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(RECIPE_KEY)) {
            mRecipes = bundle.getParcelable(RECIPE_KEY);
        } else {
            makeSnackBar(mParentLayout, getString(R.string.load_recipe_error));
            finish();
        }

        setContentView(R.layout.activity_recipes_info);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar and set recipes name as title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipes.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        doubleFragmentPanel = getResources().getBoolean(R.bool.double_fragment_panel);
        if (doubleFragmentPanel) {
            if (savedInstanceState == null && !mRecipes.getSteps().isEmpty()) {
                showSteps(0);
            }
        }
        setupRecyclerView();
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

    private void setupRecyclerView() {
        mRecyclerViewStepList.setAdapter(new RecipesAdapter(mRecipes, new Listeners.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showSteps(position);
            }
        }));
    }

    private void showSteps(int position) {
        if (doubleFragmentPanel) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepDetailFragment.STEP_KEY, mRecipes.getSteps().get(position));
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayoutRecipeStepsDetail, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putExtra(StepDetailsActivity.RECIPE_KEY, mRecipes);
            intent.putExtra(StepDetailsActivity.STEP_SELECTED_KEY, position);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_to_widget) {
            AppWidgetService.updateWidget(this, mRecipes);
            makeSnackBar(mParentLayout, String.format(getString(R.string.added_to_widget), mRecipes.getName()));
            return true;

        } else
            return super.onOptionsItemSelected(item);
    }
}
