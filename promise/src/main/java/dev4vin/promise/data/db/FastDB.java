/*
 *
 *  * Copyright 2017, Peter Vincent
 *  * Licensed under the Apache License, Version 2.0, Promise.
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package dev4vin.promise.data.db;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.Arrays;

import dev4vin.promise.Promise;
import dev4vin.promise.data.db.query.QueryBuilder;
import dev4vin.promise.data.log.LogUtil;
import dev4vin.promise.model.Identifiable;
import dev4vin.promise.model.List;
import dev4vin.promise.model.SList;
import dev4vin.promise.util.Conditions;

/**
 * Fast db database, easier access to SQLite database
 * @see Crud
 */
public abstract class FastDB extends SQLiteOpenHelper implements Crud<SQLiteDatabase> {
  /**
   * the default name of the database
   */
  private static final String DEFAULT_NAME = "fast";
  /*private static Map<IndexCreated, Table<?, SQLiteDatabase>> indexCreatedTableHashMap;*/
  private String TAG = LogUtil.makeTag(FastDB.class);
  /**
   * database context
   */
  private Context context;

  /**
   * @param name database identifier
   * @param factory factory for database cursor
   * @param version version number of the database
   * @param errorHandler handles errors from the database
   */
  private FastDB(
      String name,
      SQLiteDatabase.CursorFactory factory,
      int version,
      DatabaseErrorHandler errorHandler) {
    super(Promise.instance().context(), name, factory, version, errorHandler);
    LogUtil.d(TAG, "fast db init");
    this.context = Promise.instance().context();
    /*initTables();*/
  }

  /**
   * @param name database identifier
   * @param version version number of the database
   * @param cursorListener for logging query statements
   * @param listener corruption listener
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public FastDB(String name, int version, final FastDbCursorFactory.Listener cursorListener, final Corrupt listener) {
    this(
        name,
        cursorListener != null ? new FastDbCursorFactory(cursorListener) : null,
        version,
        dbObj -> {
          assert listener != null;
          listener.onCorrupt();
        });
  }

  /**
   * @param version version of the database
   */
  public FastDB(int version) {
    this(DEFAULT_NAME, version, null, null);
  }

  /*private void initTables() {
    indexCreatedTableHashMap = new ArrayMap<>();
    List<Table<?, SQLiteDatabase>> tables = Conditions.checkNotNull(tables());
    for (int i = 0; i < tables.size(); i++)
        indexCreatedTableHashMap.put(new IndexCreated(i, false), tables.get(i));
  }*/

  /**
   * @param db new database instance
   */
  @Override
  public final void onCreate(SQLiteDatabase db) {
    create(db);
  }

  /**
   * @param database current database
   * @param oldVersion old version number of the database
   * @param newVersion new version number of the database
   */
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    LogUtil.d(TAG, "onUpgrade", oldVersion, newVersion);
    if (shouldUpgrade(database, oldVersion, newVersion)) {
      LogUtil.d(TAG, "onUpgrade", "upgrading tables");
      upgrade(database, oldVersion, newVersion);
    }
  }

  /**
   * checks if the database should do upgrades to its tables
   * @param database current database
   * @param oldVersion old database version
   * @param newVersion new database version
   * @return flag to upgrade database
   */
  public abstract boolean shouldUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);

  /**
   * gets the name of the database
   * @return name of the database
   */
  public String name() {
    return this.getDatabaseName();
  }

  /**
   * gets all the tables to be added to this database
   * @return a list of tables
   */
  public abstract List<Table<?, ? super SQLiteDatabase>> tables();

  /**
   * creates the database structure
   * loops all the tables creating each one
   * @param database database to be structured
   */
  private void create(SQLiteDatabase database) {
    boolean created = true;
    for (Table<?, ? super SQLiteDatabase> table : Conditions.checkNotNull(tables())) {
      try {
        created = created && create(table, database);
      } catch (DBError dbError) {
        LogUtil.e(TAG, dbError);
        return;
      }
    }
  }

  /**
   * upgrades the database from one version to the next
   * @param database database to be upgraded
   * @param v1 old version
   * @param v2 new version
   */
  private void upgrade(SQLiteDatabase database, int v1, int v2) {
    for (Table<?, ? super SQLiteDatabase> table : Conditions.checkNotNull(tables())) {
      try {
        if ((v2 - v1) == 1) checkTableExist(table).onUpgrade(database, v1, v2);
        else {
          int i = v1;
          while (i < v2) {
            checkTableExist(table).onUpgrade(database, i, i + 1);
            i++;
          }
        }
      } catch (ModelError modelError) {
        LogUtil.e(TAG, modelError);
        return;
      }
    }
  }

  /**
   * adda a table to the existing database
   * @param database current database
   * @param tables tables to be added
   * @return true if tables are added successfully else false
   */
  @SafeVarargs
  public final boolean add(SQLiteDatabase database, Table<?, ? super SQLiteDatabase>... tables) {
    boolean created = true;
    for (Table<?, ? super SQLiteDatabase> table : tables) {
      try {
        created = created && create(table, database);
      } catch (DBError dbError) {
        LogUtil.e(TAG, dbError);
        return false;
      }
    }
    return created;
  }

  /**
   * removes table from the database
    * @param database current database
   * @param tables tables to be removed
   * @return true if tables are dropped else false
   */
  @SafeVarargs
  public final boolean drop(SQLiteDatabase database, Table<?, ? super SQLiteDatabase>... tables) {
    boolean created = true;
    for (Table<?, ? super SQLiteDatabase> table : tables) {
      try {
        created = created && drop(table, database);
      } catch (DBError dbError) {
        LogUtil.e(TAG, dbError);
        return false;
      }
    }
    return created;
  }

  /**
   * creates a table in the database
   * @param table table to be created
   * @param database current database
   * @return true if table created else false
   * @throws DBError if table not created
   */
  private boolean create(Table<?, ? super SQLiteDatabase> table, SQLiteDatabase database) throws DBError {
    try {
      table.onCreate(database);
    } catch (ModelError e) {
      throw new DBError(e);
    }
    return true;
  }

  /**
   * drops a table from the database
   * @param table table to be dropped
   * @param database current database
   * @return true if table is dropped
   * @throws DBError if table could not be dropped
   */
  private boolean drop(Table<?, ? super SQLiteDatabase> table, SQLiteDatabase database) throws DBError {
    try {
      checkTableExist(table).onDrop(database);
    } catch (ModelError e) {
      throw new DBError(e);
    }
    return true;
  }

  /**
   * queries the database for more read options not present in the models
   * @param builder contains query statements
   * @return a cursor for the fetched data
   */
  public Cursor query(QueryBuilder builder) {
    String sql = builder.build();
    String[] params = builder.buildParameters();
    LogUtil.d(TAG, "query: " + sql, " params: " + Arrays.toString(params));
    return getReadableDatabase().rawQuery(sql, params);
  }

  /**
   * reads data in the table
   * @param table table to be read
   * @param <T> type of data to be returned
   * @return an extras instance for more read options
   */
  @Override
  public <T extends Identifiable<Integer>> Table.Extras<T> read(Table<T, ? super SQLiteDatabase> table) {
    return checkTableExist(table).read(getReadableDatabase());
  }

  /**
   * reads all the records in the table
   * @param table model to be read
   * @param <T> type of data to be returned
   * @return a list of records of type T
   */
  @Override
  public <T extends Identifiable<Integer>> SList<? extends T> readAll(Table<T, ? super SQLiteDatabase> table) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), true);
  }

  /**
   * reads all the records fulfilled by the conditions passed in the column
   * @param table model to read
   * @param column column to read by
   * @param <T> type of records
   * @return a list of records of type T
   */
  @Override
  public <T extends Identifiable<Integer>> SList<? extends T> readAll(Table<T, ? super SQLiteDatabase> table, Column column) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), column);
  }

  /**
   * updates a record in the table
   * @param t record to update must have {@link Model#id} more than 0
   * @param table table to update the record in
   * @param <T> type of t
   * @return true if updated else false
   */
  @Override
  public <T extends Identifiable<Integer>> boolean update(T t, Table<T, ? super SQLiteDatabase> table) {
    return checkTableExist(table).onUpdate(t, getWritableDatabase());
  }

  /**
   * updates the record t with condition in the column
   * @param t record to update
   * @param table table to update in
   * @param column column to check when doing update {@link Column}
   * @param <T> type or record being updated
   * @return true if updated else false
   */
  @Override
  public <T extends Identifiable<Integer>> boolean update(T t, Table<T, ? super SQLiteDatabase> table, Column column) {
    try {
      return checkTableExist(table).onUpdate(t, getWritableDatabase(), column);
    } catch (ModelError modelError) {
      LogUtil.e(TAG, "update withErrorCallback", modelError);
      return false;
    }
  }

  /**
   * reads all the records matching all the conditions in the passed columns
   * @param table table to read from
   * @param columns columns to refer conditions
   * @param <T> type of records read
   * @return a list of records
   */
  @Override
  public <T extends Identifiable<Integer>> SList<? extends T> readAll(Table<T, ? super SQLiteDatabase> table, Column[] columns) {
    return checkTableExist(table).onReadAll(getReadableDatabase(), columns);
  }

  /**
   * deletes the record t from the said table
   * @param table table to delete from
   * @param t record to delete must have {@link Model#id} more than 0
   * @param <T> type of record being deleted
   * @return true if deleted else false
   */
  @Override
  public <T extends Identifiable<Integer>> boolean delete(Table<T, ? super SQLiteDatabase> table, T t) {
    return checkTableExist(table).onDelete(t, getWritableDatabase());
  }

  /**
   * drops the records in the table with conditions in the passed column
   * @param table table to delete records from
   * @param column column to refer conditions
   * @return true if records deleted else false
   */
  @Override
  public boolean delete(Table<?, ? super SQLiteDatabase> table, Column column) {
    return checkTableExist(table).onDelete(getWritableDatabase(), column);
  }

  /**
   * drops records from the tables passed
   * @param tables tables to clear records from
   * @return true if records cleared else false
   */
  @SafeVarargs
  public final boolean delete(Table<?, ? super SQLiteDatabase>... tables) {
    boolean delete = true;
    for (Table<?, ? super SQLiteDatabase> table : tables)
      delete = delete && delete(table);
    return delete;
  }

  /**
   * deletes a list of records from the table where the column value matched each of the items in list
   * @param table table to delete records from
   * @param column column to refer values from
   * @param list contains values to match with column
   * @param <T> type of records being deleted
   * @return true if records deleted else false
   */
  @Override
  public <T> boolean delete(Table<?, ? super SQLiteDatabase> table, Column<? extends T> column, List<? extends T> list) {
    return checkTableExist(table).onDelete(getWritableDatabase(), column, list);
  }

  /**
   * saves a record in the tables
   * @param t record to be saved
   * @param table table to save into
   * @param <T> type of record being saved
   * @return id of the record saved with reference in the table
   */
  @Override
  public <T extends Identifiable<Integer>> long save(T t, Table<T, ? super SQLiteDatabase> table) {
    return checkTableExist(table).onSave(t, getWritableDatabase());
  }

  /**
   * saves a list of records in the table
   * @param list records to save
   * @param table table to save records into
   * @param <T> type of records to save
   * @return true if records saved else false
   */
  @Override
  public <T extends Identifiable<Integer>> boolean save(SList<? extends T> list, Table<T, ? super SQLiteDatabase> table) {
    return checkTableExist(table).onSave(list, getWritableDatabase());
  }

  /**
   * deletes all the table from this database
   * @return true if all tables dropped
   */
  @Override
  public boolean deleteAll() {
    synchronized (FastDB.class) {
      boolean deleted = true;
      for (Table<?, ? super SQLiteDatabase> table : Conditions.checkNotNull(tables()))
        deleted = deleted && delete(checkTableExist(table));
      return deleted;
    }
  }

  /**
   * gets the last id of the last record in the table
   * @param table table to fetch last id
   * @return the id of the last row in the table
   */
  @Override
  public int getLastId(Table<?, ? super SQLiteDatabase> table) {
    return checkTableExist(table).onGetLastId(getReadableDatabase());
  }

  /**
   * gets the database context
   * @return context used to create the database
   */
  public Context getContext() {
    return context;
  }

  /**
   * is supposed to check if the table exists, if it doesn't, create it
   * @param  table to check
   * @param <T> type of records in the table
   * @return table
   */
  private <T extends Identifiable<Integer>> Table<T, ? super SQLiteDatabase> checkTableExist(Table<T, ? super SQLiteDatabase> table) {
    return Conditions.checkNotNull(table);
    /*synchronized (this) {
        IndexCreated indexCreated = getIndexCreated(table);
        if (indexCreated.created) {
            return table;
        }
        SQLiteDatabase database = context.openOrCreateDatabase(name(),
                Context.MODE_PRIVATE, null);
        try {
            database.query(table.name(), null, null, null, null, null, null);
        } catch (SQLException e) {
            try {
                table.onCreate(database);
            } catch (ModelError modelError) {
                LogUtil.e(TAG, modelError);
                throw new RuntimeException(modelError);
            }
        }
    }*/
  }

  /*private IndexCreated getIndexCreated(Table<?, SQLiteDatabase> table) {
    for (Iterator<Map.Entry<IndexCreated, Table<?, SQLiteDatabase>>> iterator =
            indexCreatedTableHashMap.entrySet().iterator();
        iterator.hasNext(); ) {
      Map.Entry<IndexCreated, Table<?, SQLiteDatabase>> entry = iterator.next();
      Table<?, SQLiteDatabase> table1 = entry.getValue();
      if (table1.getName().equalsIgnoreCase(table.getName())) return entry.getKey();
    }
    return new IndexCreated(0, false);
  }*/

  private static class IndexCreated {
    int id;
    boolean created;

    IndexCreated(int id, boolean created) {
      this.id = id;
      this.created = created;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) return true;
      if (!(object instanceof IndexCreated)) return false;
      IndexCreated that = (IndexCreated) object;
      return id == that.id && created == that.created;
    }

    @Override
    public int hashCode() {
      int result = id;
      result = 31 * result + (created ? 1 : 0);
      return result;
    }
  }
}
