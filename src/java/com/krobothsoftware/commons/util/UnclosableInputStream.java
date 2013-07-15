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

package com.krobothsoftware.commons.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * In order to disconnect HttpURLConnection correctly the
 * {@link java.net.HttpURLConnection#disconnect()} must be called before {@link #close()}
 * on InputStream. This class ensures the stream is never closed unless the
 * method {@link #forceClose()} is called. <a href=
 * "http://stackoverflow.com/questions/4767553/safe-use-of-httpurlconnection/11533423#11533423"
 * > More Info</a> </br> The delegate InputStream may be an internal NULL
 * InputStream.
 * <p>
 * <p/>
 * <pre>
 * &#064;Override
 * public int read() throws IOException {
 * 	return -1;
 * }
 *
 * &#064;Override
 * public int available() throws IOException {
 * 	return 0;
 * }
 * </pre>
 * <p/>
 * </p>
 * <b>There will never be a null delegate stream.</b>
 *
 * @author Kyle Kroboth
 * @since COMMONS 1.0
 */
public class UnclosableInputStream extends InputStream {
    private static final NullInputStream NULL = new NullInputStream();
    private final InputStream in;

    /**
     * Creates {@link #NULL} stream as delegate.
     *
     * @since COMMONS 1.0
     */
    public UnclosableInputStream() {
        this(NULL);
    }

    /**
     * Creates new stream with <code>in</code> as delegate.
     *
     * @param in if null, {@link #NULL} will be used instead
     * @since COMMONS 1.0
     */
    public UnclosableInputStream(InputStream in) {
        if (in == null) this.in = NULL;
        else
            this.in = in;
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public int read() throws IOException {
        return in.read();
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public int available() throws IOException {
        return in.available();
    }

    /**
     * Closes delegate stream. {@link InputStream#close}.
     *
     * @throws IOException
     * @since COMMONS 1.0
     */
    public void forceClose() throws IOException {
        in.close();
    }

    /**
     * Call {@link #forceClose()} to close stream.
     *
     * @since COMMONS 1.0
     */
    @Override
    public void close() throws IOException {
        // no op
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public int hashCode() {
        return in.hashCode();
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public boolean equals(Object obj) {
        return in.equals(obj);
    }

    /**
     * @since COMMONS 1.0
     */
    @Override
    public String toString() {
        return in.toString();
    }

    static class NullInputStream extends InputStream {

        NullInputStream() {
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int available() throws IOException {
            return 0;
        }

    }

}
