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

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexsav.baking_app.R;
import com.alexsav.baking_app.models.Steps;
import com.alexsav.baking_app.utils.GlideApp;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.orhanobut.logger.Logger;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepDetailFragment extends Fragment {
    public static final String STEP_KEY = "step_k";
    private static final String POSITION_KEY = "pos_k";
    private static final String PLAY_WHEN_READY_KEY = "play_when_ready_k";

    // Bind instruction to a nested scroll view
    @BindView(R.id.nestedScrollViewInstruction)
    NestedScrollView mNestedScrollViewInstruction;
    // Bind exo player to a variable
    @BindView(R.id.exoPlayerView)
    SimpleExoPlayerView mExoPlayerView;
    // Bind step image to a image view
    @BindView(R.id.imgStepThumbnail)
    ImageView mImgStepThumbnail;
    // Bind instruction to a text view
    @BindView(R.id.textViewInstruction)
    TextView mTextViewInstructions;

    private SimpleExoPlayer simpleExoPlayer;
    private Steps mStep;
    private Unbinder unbinder;
    private long mCurrentPosition = 0;
    private boolean mPlayWhenReady = true;

    // Constructor
    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(STEP_KEY)) {
            mStep = getArguments().getParcelable(STEP_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment bind view to butter knife
        View rootView = inflater.inflate(R.layout.recipe_step_detail, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mCurrentPosition = savedInstanceState.getLong(POSITION_KEY);
            mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY_KEY);
        }

        unbinder = ButterKnife.bind(this, rootView);
        mTextViewInstructions.setText(mStep.getDescription());

        // Show thumbnail if url exists
        if (!mStep.getThumbnailURL().isEmpty()) {
            GlideApp.with(this)
                    .load(mStep.getThumbnailURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_recipes_main_image)
                    .into(mImgStepThumbnail);
            mImgStepThumbnail.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mStep.getVideoURL()))
            initializeExoPlayer(Uri.parse(mStep.getVideoURL()));
        else {
            // Un- hide InstructionsContainer because in case of phone landscape is hidden
            mNestedScrollViewInstruction.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Logger.d("onDestroyView");
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(POSITION_KEY, mCurrentPosition);
        outState.putBoolean(PLAY_WHEN_READY_KEY, mPlayWhenReady);
    }

    private void initializeExoPlayer(Uri mp4VideoUri) {
        if (simpleExoPlayer == null) {

            // Create a default TrackSelector
            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            // Create the player
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            // Bind the player to the view.
            mExoPlayerView.setPlayer(simpleExoPlayer);
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(Objects.requireNonNull(getContext()),
                    Util.getUserAgent(getContext(), getString(R.string.app_name)), bandwidthMeter);
            // This is the MediaSource representing the media to be played.
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
            // Prepare the player with the source.
            simpleExoPlayer.prepare(mediaSource);

            // onRestore
            if (mCurrentPosition != 0)
                simpleExoPlayer.seekTo(mCurrentPosition);

            mExoPlayerView.setVisibility(View.VISIBLE);
            simpleExoPlayer.setPlayWhenReady(mPlayWhenReady);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            mPlayWhenReady = simpleExoPlayer.getPlayWhenReady();
            mCurrentPosition = simpleExoPlayer.getCurrentPosition();
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }
}
