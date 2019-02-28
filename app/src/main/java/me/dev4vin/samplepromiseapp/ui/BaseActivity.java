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

package me.dev4vin.samplepromiseapp.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import me.dev4vin.LogUtil;
import me.dev4vin.Promise;
import me.dev4vin.promisemodel.List;
import me.dev4vin.promisemodel.ResponseCallBack;
import me.dev4vin.promisenet.net.FastParser;
import me.dev4vin.promiseui.cac.anim.Anim;
import me.dev4vin.promiseui.cac.anim.Animator;
import me.dev4vin.promiseui.cac.notif.NDialog;
import me.dev4vin.promiseui.cac.notif.Notification;
import me.dev4vin.samplepromiseapp.R;
import me.dev4vin.samplepromiseapp.ui.base.BasePresenter;

/**
 * Created by dev4vin on 6/15/17.
 */
public abstract class BaseActivity<Presenter extends BasePresenter> extends AppCompatActivity {
  private String TAG = LogUtil.makeTag(BaseActivity.class);
  private Notification notification;
  private Animator animator;
  private boolean hasBackButton = false;
  private Handler handler;
  private int NETWORK_ID_PROBLEM;

  protected Presenter presenter;

  @NonNull
  protected abstract Presenter createPresenter();

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (presenter == null) return;
    presenter.destroyView();
  }

  public boolean isHasBackButton() {
    return hasBackButton;
  }

  public void setHasBackButton(boolean hasBackButton) {
    this.hasBackButton = hasBackButton;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = createPresenter();
    notification = new Notification(this);
    animator = new Animator();
    handler = new Handler();
  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    if (isHasBackButton()) {
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }
    NETWORK_ID_PROBLEM = Promise.instance().listen(FastParser.SENDER, new ResponseCallBack<>()
        .response(object -> LogUtil.e(TAG, object)));
  }

  @Override
  public void finish() {
    Promise.instance().stopListening(NETWORK_ID_PROBLEM);
    super.finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home: {
        finish();
      }
    }
    return super.onOptionsItemSelected(item);
  }

  public void prepSpinner(
      @NonNull Spinner spinner, @NonNull List<String> items, final SpinnerChoice choice) {
    ArrayAdapter<String> startAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
    startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (choice != null) choice.onSelected(position);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
          }
        });
    spinner.setAdapter(startAdapter);
  }

  public void prepSpinner(
      @NonNull Spinner spinner,
      @NonNull List<String> items,
      String selected,
      final SpinnerChoice choice) {
    prepSpinner(spinner, items, choice);
    int index = new List<>(items).findIndex(s -> s.equals(selected));
    if (index == -1) return;
    spinner.setSelection(findPosition(items, selected));
  }

  public int findPosition(List<String> choices, String choice) {
    for (int i = 0; i < choices.size(); i++) if (choices.get(i).equalsIgnoreCase(choice)) return i;
    return 0;
  }

  public Notification getNotification() {
    return notification;
  }

  public void notifyNotificationBar(String message, PendingIntent pendingIntent) {
  }

  public void dialog(String title, String message) {
    dialog(title, message, new NDialog.DButton[]{});
  }

  public void dialog(String title, String message, boolean close) {
    dialog(title, message, close, new NDialog.DButton[]{});
  }

  public void dialog(String title, String message, NDialog.DButton... buttons) {
    if (buttons.length < 1) {
      notification.showDialog(title, message, null, null, null);
      return;
    }
    if (buttons.length > 3)
      throw new IllegalArgumentException("cant display more " + "than 3 button");
    if (buttons.length == 1) notification.showDialog(title, message, buttons[0], null, null);
    if (buttons.length == 2) notification.showDialog(title, message, buttons[0], buttons[1], null);
    if (buttons.length == 3)
      notification.showDialog(title, message, buttons[0], buttons[1], buttons[2]);
  }

  public void dialog(String title, String message, boolean close, NDialog.DButton... buttons) {
    if (buttons.length < 1) {
      notification.showDialog(title, message, null, null, null, close);
      return;
    }
    if (buttons.length > 3)
      throw new IllegalArgumentException("cant display more " + "than 3 button");
    if (buttons.length == 1) notification.showDialog(title, message, buttons[0], null, null, close);
    if (buttons.length == 2)
      notification.showDialog(title, message, buttons[0], buttons[1], null, close);
    if (buttons.length == 3)
      notification.showDialog(title, message, buttons[0], buttons[1], buttons[2], close);
  }

  public void shake(View view) {
    animator.addAnim(Anim.Attention.tada()).addView(view).animate();
  }

 /* public void showStatus(String status) {
    Snackbar.make(this, status, Snackbar.LENGTH_LONG).show();
  }

  public void showStatus(String status, NDialog.DButton button) {
    Snackbar.make(content, status, Snackbar.LENGTH_INDEFINITE)
        .setAction(
            button.getText(this),
            v -> {
              if (button.getListener() == null) return;
              button.getListener().onClick(v);
            })
        .show();
  }
*/

  /*public static void safeOpenActivityIntent(Context context, Intent activityIntent) {

    // Verify that the intent will resolve to an activity
    if (activityIntent.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(activityIntent);
    } else {
      Toast.makeText(context, R.string.app_not_available, Toast.LENGTH_LONG).show();
    }
  }
*/

  public interface SpinnerChoice {
    void onSelected(int id);
  }
}
