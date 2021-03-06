/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexsav.baking_app.idling;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Class taken from TeaTime project lesson by Udacity
 */
public class RecipesIdlingResource implements IdlingResource {

    // Idleness is controlled with this boolean.
    private final AtomicBoolean isIdleNow = new AtomicBoolean(true);
    @Nullable
    private volatile ResourceCallback mCallback;

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.mCallback = callback;
    }

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the {@link ResourceCallback}.
     *
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    public void setIdleState(boolean isIdleNow) {
        this.isIdleNow.set(isIdleNow);
        if (isIdleNow && mCallback != null) {
            Objects.requireNonNull(mCallback).onTransitionToIdle();
        }
    }
}