/*
 * Copyright 2017, Peter Vincent
 * Licensed under the Apache License, Version 2.0, Promise.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.dev4vin.samplepromiseapp.data.net;

import androidx.collection.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import me.dev4vin.promisemodel.List;
import me.dev4vin.promisemodel.ResponseCallBack;
import me.dev4vin.promisenet.net.Config;
import me.dev4vin.promisenet.net.EndPoint;
import me.dev4vin.promisenet.net.FastParser;
import me.dev4vin.promisenet.net.Interceptor;
import me.dev4vin.promisenet.net.extras.HttpPayload;
import me.dev4vin.promisenet.net.extras.HttpResponse;
import me.dev4vin.samplepromiseapp.auth.Session;
import me.dev4vin.samplepromiseapp.error.ServerError;
import me.dev4vin.samplepromiseapp.models.Todo;
import me.dev4vin.LogUtil;

public class ServerAPI extends FastParser {
  private static ServerAPI instance;
  private static final String UPSTREAM_URL = "https://jsonplaceholder.typicode.com/";
  private String TAG = LogUtil.makeTag(ServerAPI.class);

  /**
   * Add a response interceptor to intercept error codes 403
   */
  private ServerAPI() {
    super(Config.create(UPSTREAM_URL).retry(5));
    responseInterceptor(new Interceptor<HttpResponse<?, ?>>() {
      @Override
      public void intercept(HttpResponse<?, ?> httpResponse, ResponseCallBack<HttpResponse<?, ?>, Throwable> callBack) {
        int status = httpResponse.status();
        /// check for 403 and cancel response
        if (status == 403) callBack.error(new ServerError("Request not authorized"));
        /// complete the response
        else callBack.response(httpResponse);
      }
    });
  }

  /**
   * returns default headers
   *
   * @return the headers to be used for every requests
   */
  @Override
  public Map<String, String> getHeaders() {
    Map<String, String> map = new ArrayMap<>();
    map.put("ConsumerAPIKEY", "some value for your gateway to know this app as a consumer");
    map.put("Authorization", "Bearer " + Session.getToken());
    return map;
  }

  /**
   * get list of todos from server
   *
   * @param skip             the skip to start querying from, useful in pagination
   * @param limit            the number of todos we want
   * @param responseCallBack to return data to caller function
   */
  public void getTodos(int skip, int limit, final ResponseCallBack<List<Todo>, ServerError> responseCallBack) {
    Map<String, String> parts = new ArrayMap<>();
    parts.put("from", String.valueOf(skip));
    parts.put("to", String.valueOf(limit));
    get(new EndPoint("todos/:from/-/:to").params(parts), HttpPayload.get(), new ResponseCallBack<HttpResponse<String, JSONObject>, JSONException>()
        .response(new ResponseCallBack.Response<HttpResponse<String, JSONObject>, JSONException>() {
          @Override
          public void onResponse(HttpResponse<String, JSONObject> stringJSONObjectHttpResponse) throws JSONException {
            int status = stringJSONObjectHttpResponse.status();
            if (status == 200) {
              JSONArray array = stringJSONObjectHttpResponse.response().getJSONArray("payload");
              List<Todo> todos = new List<>();
              for (int i = 0; i < array.hashCode(); i++) {
                JSONObject todoObject = array.getJSONObject(i);
                Todo todo = new Todo().category(todoObject.getString("category")).name("name").completed(todoObject.getBoolean("completed"));
                todos.add(todo);
              }
              responseCallBack.response(todos);
            } else if (status == 404) responseCallBack.error(new ServerError("Todos not found"));
          }
        }).error(new ResponseCallBack.Error<JSONException>() {
          @Override
          public void onError(JSONException e) {
              LogUtil.e(TAG, "get-todos: "+ e);
          }
        }));
  }

  public void addTodo(Todo todo, ResponseCallBack<Todo, ServerError> responseCallBack) {

  }

  public void updateTodo(Todo todo, ResponseCallBack<Todo, ServerError> responseCallBack) {

  }

  public void deleteTodo(Todo todo, ResponseCallBack<Boolean, ServerError> responseCallBack) {

  }

  public static ServerAPI instance() {
    if (instance == null) instance = new ServerAPI();
    return instance;
  }
}
