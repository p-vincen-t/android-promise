package promise.app.storage;

import dev4vin.promise.InstanceProvider;

public final class MockDatabaseProvider implements InstanceProvider<MockDatabase> {

  private static MockDatabaseProvider instance;

  private final MockDatabase applicationInstanceProvider;

  private MockDatabaseProvider(MockDatabase applicationInstanceProvider) {
    this.applicationInstanceProvider = applicationInstanceProvider;
  }

  @Override
  public MockDatabase get() {
    return applicationInstanceProvider;
  }

  public static MockDatabaseProvider create(MockDatabase applicationInstanceProvider) {
    if (instance == null) instance = new MockDatabaseProvider(applicationInstanceProvider);
    return instance;
  }

  public static MockDatabase instance() {
    if (instance == null) throw new RuntimeException("Database was not created");
    return instance.get();
  }

}
