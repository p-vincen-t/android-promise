package dev4vin.promise.data.db;

import android.database.sqlite.SQLiteDatabase;

import dev4vin.promise.data.Extras;
import dev4vin.promise.data.Store;
import dev4vin.promise.model.List;
import dev4vin.promise.model.ResponseCallBack;
import dev4vin.promise.model.SModel;

public class FastDBStore<T extends SModel> implements Store<T, Table<T, SQLiteDatabase>, Throwable> {
  private FastDB fastDB;

  public FastDBStore(FastDB fastDB) {
    this.fastDB = fastDB;
  }

  @Override
  public void get(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      ResponseCallBack<Extras<T>, Throwable> callBack) {
    new StoreExtra<T, Throwable>() {
      @Override
      public <Y> List<? extends T> filter(List<? extends T> list, Y... y) {
        return list;
      }
    }.getExtras(fastDB.readAll(tsqLiteDatabaseTable), callBack);
  }

  @Override
  public void delete(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      T t,
      ResponseCallBack<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.delete(tsqLiteDatabaseTable, t));
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void update(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      T t,
      ResponseCallBack<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.update(t, tsqLiteDatabaseTable));
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void save(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      T t,
      ResponseCallBack<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.save(t, tsqLiteDatabaseTable) > 0);
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void clear(
      Table<T, SQLiteDatabase> tsqLiteDatabaseTable,
      ResponseCallBack<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.delete(tsqLiteDatabaseTable));
    } catch (Throwable e) {
      callBack.error(e);
    }
  }

  @Override
  public void clear(ResponseCallBack<Boolean, Throwable> callBack) {
    try {
      callBack.response(fastDB.deleteAll());
    } catch (Throwable e) {
      callBack.error(e);
    }
  }
}
