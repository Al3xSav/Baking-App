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

package com.alexsav.baking_app.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alexsav.baking_app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsViewHolder extends RecyclerView.ViewHolder {
    //Bind steps of a recipe to a text view variable
    @BindView(R.id.textViewStepCounter)
    public TextView textViewStepCounter;

    //Bind the title of the steps of a recipe to a text view variable
    @BindView(R.id.textViewStepTitle)
    public TextView textViewStepTitle;

    public StepsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
