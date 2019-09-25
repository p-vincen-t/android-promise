package promise.app.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import dev4vin.promise.data.db.Column;
import dev4vin.promise.data.db.Model;
import dev4vin.promise.model.List;
import promise.app.mock.MockObject;

public class MockTable extends Model<MockObject> {

  private static Column<String> name;

  static {
    name = new Column<>("name", Column.Type.TEXT.NOT_NULL(), 1);
  }

  /**
   * gets all the columns for this model from the child class for creation purposes
   * see {@link #onCreate(SQLiteDatabase)}
   *
   * @return list of columns
   */
  @Override
  public List<? extends Column> getColumns() {
    return List.fromArray(name);
  }

  @Override
  public String getName() {
    return "mock_table";
  }

  @Override
  public ContentValues get(MockObject mockObject) {
    ContentValues values = new ContentValues();
    values.put(name.getName(), mockObject.getString());
    return values;
  }

  @Override
  public MockObject from(Cursor cursor) {
    return new MockObject(cursor.getInt(id.getIndex()), cursor.getString(name.getIndex()));
  }
}
