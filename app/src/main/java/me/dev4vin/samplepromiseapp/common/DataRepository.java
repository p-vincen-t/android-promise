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

package me.dev4vin.samplepromiseapp.common;

import me.dev4vin.promisemodel.List;
import me.dev4vin.promisemodel.ResponseCallBack;
import me.dev4vin.samplepromiseapp.data.db.AppDatabase;
import me.dev4vin.samplepromiseapp.data.net.ServerAPI;
import me.dev4vin.samplepromiseapp.error.ServerError;
import me.dev4vin.samplepromiseapp.models.Todo;

public class DataRepository {
  private static DataRepository instance;

  private final AppDatabase appDatabase;
  private final ServerAPI serverAPI;

  private DataRepository() {
    appDatabase = AppDatabase.instance();
    serverAPI = ServerAPI.instance();
  }

  /**
   * get todos from the either the database or the upstream server
   * if todos are in the database, return the ones in the database hence fetch from server, store
   * in the database and return a fetch from the database
   *
   * @param skip             the todos to skip in the database
   * @param limit            the number of todos needed
   * @param responseCallBack return response back to caller
   */
  public void getTodos(int skip, int limit, final ResponseCallBack<List<Todo>, Exception> responseCallBack) {
    List<Todo> todos;
    synchronized (appDatabase) {
      todos = appDatabase.todos(skip, limit);
    }
    if (todos.isEmpty()) {
      serverAPI.getTodos(skip, limit, new ResponseCallBack<List<Todo>, ServerError>()
          .response(new ResponseCallBack.Response<List<Todo>, ServerError>() {
            @Override
            public void onResponse(List<Todo> todos) {
              synchronized (appDatabase) {
                appDatabase.saveTodos(todos);
              }
              responseCallBack.response(todos);
            }
          }).error(new ResponseCallBack.Error<ServerError>() {
            @Override
            public void onError(ServerError serverError) {
              responseCallBack.error(serverError);
            }
          }));
    } else responseCallBack.response(todos);
  }

  public static DataRepository instance() {
    if (instance == null) instance = new DataRepository();
    return instance;
  }
}
