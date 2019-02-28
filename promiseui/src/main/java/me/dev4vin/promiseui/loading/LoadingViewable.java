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

package me.dev4vin.promiseui.loading;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ProgressBar;

import me.dev4vin.R;
import me.dev4vin.promiseui.Viewable;

public class LoadingViewable implements Viewable {
  protected ProgressBar progressBarLoading;
  private int loadingStateProgressBarWidth = 0, loadingStateProgressBarHeight = 0, loadingStateProgressBarColor = 0;

  public LoadingViewable loadingStateProgressBarWidth(int loadingStateProgressBarWidth) {
    this.loadingStateProgressBarWidth = loadingStateProgressBarWidth;
    return this;
  }

  public LoadingViewable loadingStateProgressBarHeight(int loadingStateProgressBarHeight) {
    this.loadingStateProgressBarHeight = loadingStateProgressBarHeight;
    return this;
  }

  public LoadingViewable loadingStateProgressBarColor(int loadingStateProgressBarColor) {
    this.loadingStateProgressBarColor = loadingStateProgressBarColor;
    return this;
  }

  @Override
  public int layout() {
    return R.layout.loading_viewable;
  }

  @Override
  public void init(View view) {
    progressBarLoading = view.findViewById(R.id.progress_bar_loading);

  }

  @Override
  public void bind(View view) {
    if (loadingStateProgressBarWidth != 0)
      progressBarLoading.getLayoutParams().width = loadingStateProgressBarWidth;
    if (loadingStateProgressBarHeight != 0)
      progressBarLoading.getLayoutParams().height = loadingStateProgressBarHeight;
    if (loadingStateProgressBarColor != 0) progressBarLoading.getIndeterminateDrawable()
        .setColorFilter(loadingStateProgressBarColor, PorterDuff.Mode.SRC_IN);

  }

  @Override
  public void index(int index) {

  }

  @Override
  public int index() {
    return 0;
  }
}
