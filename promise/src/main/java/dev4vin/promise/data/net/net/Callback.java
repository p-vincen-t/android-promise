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
package dev4vin.promise.data.net.net;

/**
 * Communicates responses from a server or offline requests. One and only one method will be
 * invoked in withCallback to a given request.
 * <p>
 * Callback methods are executed using the {@link FastParserEngine} callback executor. When none is
 * specified, the following defaults are used:
 * <ul>
 * <li>Android: Callbacks are executed on the application's main (UI) thread.</li>
 * <li>JVM: Callbacks are executed on the background thread which performed the request.</li>
 * </ul>
 *
 * @param <T> Successful withCallback body type.
 */
public interface Callback<T> {
  /**
   * Invoked for a received HTTP withCallback.
   * <p>
   * Note: An HTTP withCallback may still indicate an application-level failure such as a 404 or 500.
   * Call {@link Response#isSuccessful()} to determine if the withCallback indicates success.
   */
  void onResponse(Call<T> call, Response<T> response);

  /**
   * Invoked when a network exception occurred talking to the server or when an unexpected
   * exception occurred creating the request or processing the withCallback.
   */
  void onFailure(Call<T> call, Throwable t);
}
