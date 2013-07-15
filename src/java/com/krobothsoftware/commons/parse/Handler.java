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

package com.krobothsoftware.commons.parse;

import com.krobothsoftware.commons.progress.ProgressMonitor;
import org.slf4j.Logger;

/**
 * Base handler for parsers.
 *
 * @author Kyle Kroboth
 * @see Parser
 * @since COMMONS 1.0
 */
public abstract class Handler {

    /**
     * Monitor for progress.
     *
     * @since COMMONS 1.0
     */
    protected ProgressMonitor monitor;

    /**
     * Logger of {@link #Parser}.
     *
     * @since COMMONS 1.0
     */
    protected Logger log;

    /**
     * Sets logger.
     *
     * @param log logger
     * @since COMMONS 1.1.0
     */
    public void setLogger(Logger log) {
        this.log = log;
    }

    /**
     * Sets progress monitor.
     *
     * @param monitor progress monitor
     * @since COMMONS 1.1.0
     */
    public void setProgressMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

}
