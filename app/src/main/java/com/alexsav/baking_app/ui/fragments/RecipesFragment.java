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
package com.alexsav.baking_app.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.adapters.BakingAdapter;
import com.alexsav.baking_app.api.NetworkCallBackAPI;
import com.alexsav.baking_app.api.NetworkUtilsAPI;
import com.alexsav.baking_app.idling.GlobalApplication;
import com.alexsav.baking_app.models.Recipes;
import com.alexsav.baking_app.ui.Listeners;
import com.alexsav.baking_app.utils.PreferencesUtils;
import com.alexsav.baking_app.widget.AppWidgetService;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.alexsav.baking_app.utils.ConnectionUtils.isNetworkAvailable;
import static com.alexsav.baking_app.utils.ConnectionUtils.makeSnackBar;

public class RecipesFragment extends Fragment {

    private static final String RECIPES_KEY = "recipes";
    // Bind recycler view of the recipes to a variable
    @BindView(R.id.recyclerViewRecipes)
    RecyclerView mRecipesRecyclerView;
    // Bind the refresher to a variable
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefresh;

    private OnRecipeClickListener mListener;
    private Unbinder unbinder;
    private List<Recipes> mRecipes;
    private GlobalApplication globalApplication;
    /**
     * Load the recipes when the app launch, with or without internet connection
     * No need to pull to refresh
     */
    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mRecipes == null) {
                refreshItems();
            }
        }
    };

    // Constructor
    public RecipesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment bind view to butter knife
        View viewRoot = inflater.inflate(R.layout.fragment_recipes, container, false);
        unbinder = ButterKnife.bind(this, viewRoot);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        setupRecyclerView();

        // Get the IdlingResource instance
        globalApplication = (GlobalApplication) Objects.requireNonNull(getActivity()).getApplicationContext();
        globalApplication.setIdleState(false);

        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPES_KEY)) {
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPES_KEY);
            mRecipesRecyclerView.setAdapter(new BakingAdapter(getActivity().
                    getApplicationContext(), mRecipes, new Listeners.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mListener.onRecipeSelected(mRecipes.get(position));
                }
            }));
            dataLoadedTakeCareLayout();
        }
        return viewRoot;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeClickListener) {
            mListener = (OnRecipeClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Logger.d("onDestroyView");
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).registerReceiver(networkChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getActivity()).unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecipes != null && !mRecipes.isEmpty())
            outState.putParcelableArrayList(RECIPES_KEY, (ArrayList<? extends Parcelable>) mRecipes);
    }

    private void setupRecyclerView() {
        mRecipesRecyclerView.setVisibility(View.GONE);
        mRecipesRecyclerView.setHasFixedSize(true);

        boolean twoPaneMode = getResources().getBoolean(R.bool.double_fragment_panel);
        if (twoPaneMode) {
            mRecipesRecyclerView.setLayoutManager(
                    new GridLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext(), 3));
        } else {
            mRecipesRecyclerView.setLayoutManager(
                    new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false));
        }

        mRecipesRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
    }

    private void refreshItems() {
        // Set SwipeRefreshLayout that refreshing in case that refreshItems get called by the networkChangeReceiver
        if (isNetworkAvailable(Objects.requireNonNull(getActivity()).getApplicationContext())) {
            mSwipeRefresh.setRefreshing(true);

            NetworkUtilsAPI.getInstance().getRecipes(new NetworkCallBackAPI<List<Recipes>>() {
                @Override
                public void onResponse(final List<Recipes> result) {
                    if (result != null) {
                        mRecipes = result;
                        mRecipesRecyclerView.setAdapter(
                                new BakingAdapter(getActivity().getApplicationContext(),
                                        mRecipes, new Listeners.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        mListener.onRecipeSelected(mRecipes.get(position));
                                    }
                                }));
                        // Set the default recipe for the widget
                        if (PreferencesUtils.loadRecipes(getActivity().getApplicationContext()) == null) {
                            AppWidgetService.updateWidget(getActivity(), mRecipes.get(0));
                        }

                    } else {
                        makeSnackBar(getView(), getString(R.string.load_recipe_error));
                        mSwipeRefresh.setRefreshing(true);
                    }
                    dataLoadedTakeCareLayout();
                }

                @Override
                public void onCancel() {
                    dataLoadedTakeCareLayout();
                }

            });
        } else {
            makeSnackBar(getView(), getString(R.string.internet_connection));
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void dataLoadedTakeCareLayout() {
        boolean loaded = mRecipes != null && mRecipes.size() > 0;
        mSwipeRefresh.setRefreshing(false);
        if (loaded) {
            mRecipesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecipesRecyclerView.setVisibility(View.GONE);
        }
        globalApplication.setIdleState(true);

    }

    public interface OnRecipeClickListener {
        void onRecipeSelected(Recipes recipes);
    }
}
