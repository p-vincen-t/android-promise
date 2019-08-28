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

import androidx.annotation.Nullable;
import okhttp3.Headers;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;

/** An HTTP withCallback. */
public final class Response<T> {
  /** Create a synthetic successful withCallback with {@code body} as the deserialized body. */
  public static <T> Response<T> success(@Nullable T body) {
    return success(body, new okhttp3.Response.Builder() //
        .code(200)
        .message("OK")
        .protocol(Protocol.HTTP_1_1)
        .request(new Request.Builder().url("http://localhost/").build())
        .build());
  }

  /**
   * Create a synthetic successful withCallback with an HTTP status code of {@code code} and
   * {@code body} as the deserialized body.
   */
  public static <T> Response<T> success(int code, @Nullable T body) {
    if (code < 200 || code >= 300)
      throw new IllegalArgumentException("code < 200 or >= 300: " + code);
    return success(body, new okhttp3.Response.Builder() //
        .code(code)
        .message("Response.success()")
        .protocol(Protocol.HTTP_1_1)
        .request(new Request.Builder().url("http://localhost/").build())
        .build());
  }

  /**
   * Create a synthetic successful withCallback using {@code headers} with {@code body} as the
   * deserialized body.
   */
  public static <T> Response<T> success(@Nullable T body, Headers headers) {
    Utils.checkNotNull(headers, "headers == null");
    return success(body, new okhttp3.Response.Builder() //
        .code(200)
        .message("OK")
        .protocol(Protocol.HTTP_1_1)
        .headers(headers)
        .request(new Request.Builder().url("http://localhost/").build())
        .build());
  }

  /**
   * Create a successful withCallback from {@code rawResponse} with {@code body} as the deserialized
   * body.
   */
  public static <T> Response<T> success(@Nullable T body, okhttp3.Response rawResponse) {
    Utils.checkNotNull(rawResponse, "rawResponse == null");
    if (!rawResponse.isSuccessful())
      throw new IllegalArgumentException("rawResponse must be successful withCallback");
    return new Response<>(rawResponse, body, null);
  }

  /**
   * Create a synthetic withErrorCallback withCallback with an HTTP status code of {@code code} and {@code body}
   * as the withErrorCallback body.
   */
  public static <T> Response<T> error(int code, ResponseBody body) {
    if (code < 400) throw new IllegalArgumentException("code < 400: " + code);
    return error(body, new okhttp3.Response.Builder() //
        .code(code)
        .message("Response.withErrorCallback()")
        .protocol(Protocol.HTTP_1_1)
        .request(new Request.Builder().url("http://localhost/").build())
        .build());
  }

  /** Create an withErrorCallback withCallback from {@code rawResponse} with {@code body} as the withErrorCallback body. */
  public static <T> Response<T> error(ResponseBody body, okhttp3.Response rawResponse) {
    Utils.checkNotNull(body, "body == null");
    Utils.checkNotNull(rawResponse, "rawResponse == null");
    if (rawResponse.isSuccessful()) {
      throw new IllegalArgumentException("rawResponse should not be successful withCallback");
    }
    return new Response<>(rawResponse, null, body);
  }

  private final okhttp3.Response rawResponse;
  private final @Nullable T body;
  private final @Nullable ResponseBody errorBody;

  private Response(okhttp3.Response rawResponse, @Nullable T body,
      @Nullable ResponseBody errorBody) {
    this.rawResponse = rawResponse;
    this.body = body;
    this.errorBody = errorBody;
  }

  /** The raw withCallback from the HTTP client. */
  public okhttp3.Response raw() {
    return rawResponse;
  }

  /** HTTP status code. */
  public int code() {
    return rawResponse.code();
  }

  /** HTTP status message or null if unknown. */
  public String message() {
    return rawResponse.message();
  }

  /** HTTP headers. */
  public Headers headers() {
    return rawResponse.headers();
  }

  /** Returns true if {@link #code()} is in the range [200..300). */
  public boolean isSuccessful() {
    return rawResponse.isSuccessful();
  }

  /** The deserialized withCallback body of a {@linkplain #isSuccessful() successful} withCallback. */
  public @Nullable T body() {
    return body;
  }

  /** The raw withCallback body of an {@linkplain #isSuccessful() unsuccessful} withCallback. */
  public @Nullable ResponseBody errorBody() {
    return errorBody;
  }

  @Override public String toString() {
    return rawResponse.toString();
  }
}
