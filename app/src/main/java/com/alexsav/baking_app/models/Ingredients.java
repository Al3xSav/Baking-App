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

package com.alexsav.baking_app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredients implements Parcelable {

    public static final Creator<Ingredients> CREATOR = new Creator<Ingredients>() {
        @Override
        public Ingredients createFromParcel(Parcel source) {
            return new Ingredients(source);
        }

        @Override
        public Ingredients[] newArray(int size) {
            return new Ingredients[size];
        }
    };
    @JsonProperty("quantity")
    private final int quantity;
    @JsonProperty("measure")
    private final String measure;
    @JsonProperty("ingredient")
    private final String ingredient;

    // Initialize the variables taken from JsonProperty
    public Ingredients() {
        this.quantity = 0;
        this.measure = "";
        this.ingredient = "";
    }

    // Set
    protected Ingredients(Parcel parcel) {
        this.quantity = parcel.readInt();
        this.measure = parcel.readString();
        this.ingredient = parcel.readString();
    }

    // Get
    public int getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    // Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.quantity);
        dest.writeString(this.measure);
        dest.writeString(this.ingredient);
    }

    @Override
    public String toString() {
        return "Ingredients{" +
                "quantity=" + quantity +
                ", measure='" + measure + '\'' +
                ", ingredient='" + ingredient + '\'' +
                '}';
    }
}