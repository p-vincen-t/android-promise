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

package me.dev4vin.promisedb;

import android.database.sqlite.SQLiteDatabase;

import me.dev4vin.promisemodel.Extras;
import me.dev4vin.promisemodel.List;
import me.dev4vin.promisemodel.ResponseCallBack;
import me.dev4vin.promisemodel.Store;

/** Created on 7/23/18 by yoctopus. */
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
      public <Y> List<T> filter(List<T> list, Y... y) {
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
