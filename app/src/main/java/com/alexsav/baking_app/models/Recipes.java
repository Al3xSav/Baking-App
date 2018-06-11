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
import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Recipes implements Parcelable {

    public static final Creator<Recipes> CREATOR = new Creator<Recipes>() {
        @Override
        public Recipes createFromParcel(Parcel source) {
            return new Recipes(source);
        }

        @Override
        public Recipes[] newArray(int size) {
            return new Recipes[size];
        }
    };
    @JsonProperty("image")
    private final String image;
    @JsonProperty("servings")
    private final int servings;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("ingredients")
    private final List<Ingredients> ingredients;
    @JsonProperty("id")
    private final int id;
    @JsonProperty("steps")
    private final List<Steps> steps;

    // Initialize the variables taken from JsonProperty
    private Recipes() {
        this.image = "";
        this.servings = 0;
        this.name = "";
        this.ingredients = new ArrayList<>();
        this.id = 0;
        this.steps = new ArrayList<>();
    }

    //Set
    private Recipes(Parcel parcel) {
        this.image = parcel.readString();
        this.servings = parcel.readInt();
        this.name = parcel.readString();
        this.ingredients = new ArrayList<>();
        parcel.readList(this.ingredients, Ingredients.class.getClassLoader());
        this.id = parcel.readInt();
        this.steps = new ArrayList<>();
        parcel.readList(this.steps, Steps.class.getClassLoader());
    }

    public static String toBase64String(Recipes recipes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Base64.encodeToString(objectMapper.writeValueAsBytes(recipes), 0);
        } catch (JsonProcessingException e) {
            Logger.e(e.getMessage());
        }
        return null;
    }

    public static Recipes fromBase64(String encoded) {
        if (!"".equals(encoded)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(Base64.decode(encoded, 0), Recipes.class);
            } catch (IOException e) {
                Logger.e(e.getMessage());
            }
        }
        return null;
    }

    // Get
    public String getImage() {
        return image;
    }

    public int getServings() {
        return servings;
    }

    public String getName() {
        return name;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public int getId() {
        return id;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    // Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeInt(this.servings);
        dest.writeString(this.name);
        dest.writeList(this.ingredients);
        dest.writeInt(this.id);
        dest.writeList(this.steps);
    }

    @Override
    public String toString() {
        return "Recipes{" +
                "image='" + image + '\'' +
                ", servings=" + servings +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", id=" + id +
                ", steps=" + steps +
                '}';
    }
}