package promise.app;

import android.app.Application;

import dev4vin.promise.Promise;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Promise.init(this, 100);
  }

}
