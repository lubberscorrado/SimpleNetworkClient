/* ===================================================
 * Copyright 2013 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== 
 */

package com.krobothsoftware.commons.progress;

/**
 * Monitor class for progresses.
 * <p>
 * To get a monitor instance, call {@link #newInstance(ProgressListener)}. If
 * <code>Listener</code> is null, it will create a {@link ProgressMonitor}
 * </p>
 * <p>
 * Each monitor has a set number of ticks. {@link #beginTask(String, int)}.
 * Calling the {@link #worked(int)} method will increment the tick X number of
 * times. When done, simply call {@link #done()} Note a <code>Listener</code>
 * may return a <i>cancel</i> request. {@link #isCanceled()}
 * </p>
 * 
 * @author Kyle Kroboth
 * @see com.krobothsoftware.commons.progress.SubProgressMonitor
 * @since SNC 1.0
 */
public class ProgressMonitor {
	ProgressListener listener;
	float length;
	int tick;
	int totalTicks = 1;
	float increment;
	String task;
	float value;
	private boolean cancaled;
	private boolean done;

	/**
	 * Gets Monitor for ProgressListener.
	 * 
	 * @param listener
	 * @return monitor, or Null monitor
	 * @since SNC 1.0
	 */
	public static ProgressMonitor newInstance(ProgressListener listener) {
		if (listener == null) return new NullProgressMonitor();
		return new ProgressMonitor(listener);
	}

	/*
	 * If needed to not specify any arguments.
	 * @since SNC 1.0
	 */
	protected ProgressMonitor() {

	}

	/**
	 * Creates new Monitor. Will <b>not</b> check if Listener is null.
	 * 
	 * @param listener
	 * @since SNC 1.0
	 */
	public ProgressMonitor(ProgressListener listener) {
		this.listener = listener;
		this.length = listener.getProgressLength();
		this.task = "";
	}

	/**
	 * Begins new task for monitor. Will reset Listener.
	 * 
	 * @param task
	 *            name of task
	 * @param ticks
	 *            number of total ticks
	 * @since SNC 1.0
	 */
	public void beginTask(String task, int ticks) {
		this.task = task;
		this.totalTicks = ticks;
		increment = length / totalTicks;
		listener.onProgressUpdate(0, task);
	}

	/**
	 * Sets the name of task.
	 * 
	 * @param task
	 * @since SNC 1.0
	 */
	public void setTask(String task) {
		this.task = task;
		listener.onProgressUpdate(value, task);
	}

	/**
	 * Works the monitor X number of times.
	 * 
	 * @param ticks
	 *            how many ticks to increment
	 * @since SNC 1.0
	 */
	public void worked(int ticks) {
		worked(ticks, task);
	}

	/**
	 * Works the monitor X number of times and set task name.
	 * 
	 * @param ticks
	 *            how many ticks to increment
	 * @param task
	 *            name of task
	 * @since SNC 1.0
	 */
	public void worked(int ticks, String task) {
		if (totalTicks == -1) return;
		this.task = task;
		float value = increment * (tick += ticks);
		updateListener(value, task);
	}

	/**
	 * Finishes task.
	 * 
	 * @since SNC 1.0
	 */
	public void done() {
		done(task);
	}

	/**
	 * Finishes task with task name.
	 * 
	 * @param task
	 *            finish task name
	 * @since SNC 1.0
	 */
	public void done(String task) {
		if (totalTicks == -1) return;
		tick = totalTicks;
		updateListener(length, task);
	}

	/**
	 * Checks if monitor got cancelled.
	 * 
	 * @return true, if canceled
	 * @since SNC 1.0
	 */
	public boolean isCanceled() {
		return cancaled;
	}

	/**
	 * Checks if monitor has finished.
	 * 
	 * @return true, if finished
	 * @since SNC 1.0
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * Updates listener with raw value and task.
	 * 
	 * @param value
	 *            raw value
	 * @param task
	 *            name of task
	 * @since SNC 1.0
	 */
	protected void updateListener(float value, String task) {
		this.value = value;
		if (value > length) {
			done = true;
			value = length;
		}
		cancaled = listener.onProgressUpdate(value, task);
	}

}
