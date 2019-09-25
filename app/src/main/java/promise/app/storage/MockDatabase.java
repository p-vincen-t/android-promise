package promise.app.storage;

import android.database.sqlite.SQLiteDatabase;

import dev4vin.promise.data.db.FastDB;
import dev4vin.promise.data.db.Table;
import dev4vin.promise.model.List;
import dev4vin.promise.model.SList;
import promise.app.mock.MockObject;

public class MockDatabase extends FastDB {

  private static MockTable mockTable;
  private static int version = 1;

  static {
    mockTable = new MockTable();
  }

  public MockDatabase() {
    super(version);
  }

  /**
   * checks if the database should do upgrades to its tables
   *
   * @param database   current database
   * @param oldVersion old database version
   * @param newVersion new database version
   * @return flag to upgrade database
   */
  @Override
  public boolean shouldUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    return false;
  }

  /**
   * gets all the tables to be added to this database
   *
   * @return a list of tables
   */
  @Override
  public List<Table<?, ? super SQLiteDatabase>> tables() {
    return List.fromArray(mockTable);
  }

  public boolean addMock(MockObject mockObject) {
    return save(mockObject, mockTable) > 0;
  }

  public boolean addMocks(List<? extends MockObject> mockObjects) {

    return save(new SList<>(mockObjects), mockTable);
  }

  public List<? extends MockObject> getMockObjects() {
    return readAll(mockTable);
  }

}
