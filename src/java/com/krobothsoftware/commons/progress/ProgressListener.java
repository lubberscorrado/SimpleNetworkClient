/*
 * Copyright 2013 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.krobothsoftware.commons.progress;

/**
 * The listener interface for receiving progress events. When the progress event
 * occurs, that object's appropriate method is invoked.
 *
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 */
public interface ProgressListener {

    /**
     * Called when task is incremented. If returned value is true, it will tell
     * the current task to stop. Depending on the task, it may not allow to
     * stop.
     *
     * @param value progress
     * @param task  current task name
     * @return true, to cancel the task.
     * @since COMMONS 1.0
     */
    public boolean onProgressUpdate(float value, String task);

    /**
     * Gets progress length
     *
     * @return progress length
     * @since 1.0
     */
    public float getProgressLength();

}
