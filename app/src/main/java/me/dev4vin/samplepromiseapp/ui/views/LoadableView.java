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

package me.dev4vin.samplepromiseapp.ui.views;

import android.view.View;

import me.dev4vin.promiseui.Viewable;


public class LoadableView implements Viewable {
  private String status;

  public LoadableView(String status) {
    this.status = status;
  }

  @Override
  public int layout() {
    return 0;
  }

  @Override
  public void init(View view) {

  }

  @Override
  public void bind(View view) {

  }

  @Override
  public void index(int index) {

  }

  @Override
  public int index() {
    return 0;
  }
}
