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

package me.dev4vin.samplepromiseapp.ui.main;

import me.dev4vin.promisemodel.List;
import me.dev4vin.samplepromiseapp.models.Todo;
import me.dev4vin.samplepromiseapp.ui.base.BaseView;

public interface MainView extends BaseView {
  void onAcquireTodos(List<Todo> todos);
  void onAcquireTodosError(Throwable throwable);
  void onTodoAdded(Todo todo);
  void onDeleteTodo(Todo todo);
  void onUpdateTodo(Todo todo);
}
