package promise.app;

import android.app.Application;

import dev4vin.promise.Promise;
import promise.app.storage.MockDatabase;
import promise.app.storage.MockDatabaseProvider;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Promise.init(this, 100);
    MockDatabaseProvider.create(new MockDatabase());
  }

}
