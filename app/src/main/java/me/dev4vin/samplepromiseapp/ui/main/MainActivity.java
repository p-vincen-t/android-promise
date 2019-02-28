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

import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.dev4vin.promisemodel.List;
import me.dev4vin.promiseui.AdapterDivider;
import me.dev4vin.promiseui.PromiseAdapter;
import me.dev4vin.promiseui.SearchableAdapter;
import me.dev4vin.promiseui.loading.ProgressLayout;
import me.dev4vin.samplepromiseapp.R;
import me.dev4vin.samplepromiseapp.models.Todo;
import me.dev4vin.samplepromiseapp.ui.BaseActivity;
import me.dev4vin.samplepromiseapp.ui.views.LoadableView;

public class MainActivity extends BaseActivity<MainPresenter> implements PromiseAdapter.Listener<Todo>, MainView {
  protected Toolbar toolbar;
  protected RecyclerView todosList;
  protected ProgressLayout loadingView;
  protected FloatingActionButton fab;
  private SearchableAdapter<Todo> searchableAdapter;
  private AdapterDivider divider;

  @NonNull
  @Override
  protected MainPresenter createPresenter() {
    return new MainPresenter(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_main);
    initView();
    setSupportActionBar(toolbar);
  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show());

    divider = new AdapterDivider(this, LinearLayout.VERTICAL);
    searchableAdapter = new SearchableAdapter<>(this);
    todosList.setLayoutManager(new LinearLayoutManager(this));
    todosList.setItemAnimator(new DefaultItemAnimator());
    todosList.addItemDecoration(divider);
    loadingView.showLoading(new LoadableView("Loading todos, please wait..."));

    searchableAdapter.swipe(new PromiseAdapter.Swipe<Todo>() {
      @Override
      public void onSwipeRight(Todo todo, PromiseAdapter.Response response) {

      }

      @Override
      public void onSwipeLeft(Todo todo, PromiseAdapter.Response response) {

      }
    });
    todosList.setAdapter(searchableAdapter);
    presenter.getTodos();
  }

  @Override
  public void onClick(Todo todo, int id) {

  }

  private void initView() {
    toolbar = findViewById(R.id.toolbar);
    todosList = findViewById(R.id.todos_list);
    loadingView = findViewById(R.id.loading_view);
    fab = findViewById(R.id.fab);
  }

  @Override
  public void onAcquireTodos(List<Todo> todos) {
      searchableAdapter.add(todos);
  }

  @Override
  public void onAcquireTodosError(Throwable throwable) {
      runOnUiThread(() -> loadingView.showEmpty(R.drawable.server_error, "Could not get todos", throwable.getMessage()));
  }

  @Override
  public void onTodoAdded(Todo todo) {

  }

  @Override
  public void onDeleteTodo(Todo todo) {

  }

  @Override
  public void onUpdateTodo(Todo todo) {

  }
}
