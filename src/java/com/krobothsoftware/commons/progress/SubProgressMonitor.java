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
 * A sub monitor is task inside another. It only ticks to sub monitor's limit.
 * 
 * @author Kyle Kroboth
 * @see com.krobothsoftware.commons.progress.ProgressMonitor
 * @since COMMONS 1.0
 */
public class SubProgressMonitor extends ProgressMonitor {

	/**
	 * Creates new sub monitor with number of ticks.
	 * 
	 * @param monitor
	 *            to have sub monitor
	 * @param ticks
	 *            number of ticks
	 * @since COMMONS 1.0
	 */
	public SubProgressMonitor(ProgressMonitor monitor, int ticks) {
		if (monitor == null) throw new IllegalArgumentException(
				"Monitor may not be null");
		this.listener = monitor.listener;
		length = (int) monitor.increment * ticks;
		task = "";
	}

	/**
	 * Begins new task inside sub-task.
	 * 
	 * @since COMMONS 1.0
	 */
	@Override
	public void beginTask(String task, int ticks) {
		this.task = task;
		this.totalTicks = ticks;
		increment = length / totalTicks;
	}

	/**
	 * Completes sub-task.
	 * 
	 * @since COMMONS 1.0
	 */
	@Override
	public void done(String text) {
		updateListener(length, text);
	}

}
