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
 * Null progress monitor. All methods are empty.
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0
 */
public class NullProgressMonitor extends ProgressMonitor {

	/**
	 * Creates new monitor.
	 * 
	 * @since SNC 1.0
	 */
	public NullProgressMonitor() {

	}

	/**
	 * Nothing is done.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void beginTask(String task, int ticks) {
		// no op
	}

	/**
	 * Nothing is done.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void setTask(String task) {
		// no op
	}

	/**
	 * Nothing is done.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void worked(int ticks, String task) {
		// no op
	}

	/**
	 * Nothing is done.
	 * 
	 * @since SNC 1.0
	 */
	@Override
	public void done(String task) {
		// no op
	}

}
