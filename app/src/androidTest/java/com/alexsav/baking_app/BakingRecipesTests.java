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

package com.alexsav.baking_app;

import android.content.Context;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.AndroidJUnit4;

import com.alexsav.baking_app.models.Recipes;
import com.alexsav.baking_app.ui.activities.RecipesInfoActivity;
import com.alexsav.baking_app.ui.activities.StepDetailsActivity;
import com.alexsav.baking_app.utils.BaseTest;
import com.alexsav.baking_app.utils.Navigation;
import com.alexsav.baking_app.utils.PreferencesUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BakingRecipesTests extends BaseTest {

    @Test
    public void clickRecyclerViewItemHasIntentWithAKey() {
        //Checks if the key is present
        Intents.init();

        Navigation.getMeToRecipeInfo(0);
        intended(hasExtraWithKey(RecipesInfoActivity.RECIPE_KEY));

        Intents.release();

    }

    @Test
    public void clickOnRecyclerViewItem_opensRecipeInfoActivity() {

        Navigation.getMeToRecipeInfo(0);

        onView(withId(R.id.textViewIngredients))
                .check(matches(isDisplayed()));

        onView(withId(R.id.recyclerViewStepList))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRecyclerViewStepItem_opensRecipeStepActivity_orFragment() {
        Navigation.getMeToRecipeInfo(0);

        boolean twoPaneMode = globalApplication.getResources().getBoolean(R.bool.double_fragment_panel);
        if (!twoPaneMode) {
            // Checks if the keys are present and the intent launched is StepDetailsActivity
            Intents.init();
            Navigation.selectRecipeStep(1);
            intended(hasComponent(StepDetailsActivity.class.getName()));
            intended(hasExtraWithKey(StepDetailsActivity.RECIPE_KEY));
            intended(hasExtraWithKey(StepDetailsActivity.STEP_SELECTED_KEY));
            Intents.release();

            // Check TabLayout
            onView(withId(R.id.tabLayout))
                    .check(matches(isCompletelyDisplayed()));
        } else {
            Navigation.selectRecipeStep(1);

            onView(withId(R.id.exoPlayerView))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void checkAddWidgetButtonFunctionality() {
        // Clear the preferences values
        globalApplication.getSharedPreferences(PreferencesUtils.PREFERENCES, Context.MODE_PRIVATE).edit()
                .clear()
                .commit();

        Navigation.getMeToRecipeInfo(0);

        onView(withId(R.id.action_add_to_widget))
                .check(matches(isDisplayed()))
                .perform(click());

        // Get the recipes base64 string from the sharedPrefs
        Recipes recipes = PreferencesUtils.loadRecipes(globalApplication);

        // Assert recipes is not null
        assertNotNull(recipes);
    }

}
