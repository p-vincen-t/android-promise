/*
 *
 *  * Copyright 2017, Peter Vincent
 *  * Licensed under the Apache License, Version 2.0, Promise.
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package dev4vin.promise.model;

import dev4vin.promise.data.log.LogUtil;

/**
 * callback class for passing results of asynchronous operations
 * @param <T> type of result
 * @param <E> type of error
 */
public class ResponseCallBack<T, E extends Throwable> {
    private final String TAG = LogUtil.makeTag(ResponseCallBack.class);
    /**
     * the response consumer
     */
    private Response<? super T, ? extends E> response;
    /**
     * the error consumer
     */
    private Error<? super E> error;

    /**
     * passing the response back to the consumer
     * @param t response
     * @param <R> type of response
     */
    public <R extends T> void response(R t) {
        if (response != null) {
            try {
                response.onResponse(t);
            } catch (Throwable e) {
                LogUtil.e(TAG, e);
                error((E) e);
            }
        }
        else LogUtil.e(TAG,
                new IllegalStateException("Could not pass data: "+t+" , withCallback not provided"));
    }

    /**
     * pass the response consumer
     * @param response consumer for response
     * @return response callback with the consumer
     */
    public ResponseCallBack<T, E> withCallback(Response<? super T, ? extends E> response) {
        this.response = response;
        return this;
    }

    /**
     *
     * @param e
     * @param <R>
     */
    public <R extends E> void error(R e) {
        if (error != null) error.onError(e);
        else LogUtil.e(TAG,
                new IllegalStateException("Could not process withErrorCallback: "+e+" , withErrorCallback not provided"));
    }

    /**
     *
     * @param error
     * @return
     */
    public ResponseCallBack<T, E> withErrorCallback(Error<? super E> error) {
        this.error = error;
        return this;
    }

    /**
     *
     * @param <T>
     * @param <E>
     */
    public interface Response<T, E extends Throwable> {
        /**
         *
         * @param t
         * @throws E
         */
        void onResponse(T t) throws E;
    }

    /**
     *
     * @param <E>
     */
    public interface Error<E> {
        /**
         *
         * @param e
         */
        void onError(E e);
    }
}
