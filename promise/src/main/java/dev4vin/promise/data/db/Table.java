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


import androidx.annotation.Nullable;

import dev4vin.promise.model.Identifiable;
import dev4vin.promise.model.List;
import dev4vin.promise.model.SList;


public interface Table<T extends Identifiable<Integer>, X> {
  boolean onCreate(X x) throws ModelError;

  boolean onUpgrade(X x, int v1, int v2) throws ModelError;

  Extras<T> read(X x);

  SList<? extends T> onReadAll(X x, boolean close);

  SList<? extends T> onReadAll(X x, Column... column);

  boolean onUpdate(T t, X x, Column column) throws ModelError;

  boolean onUpdate(T t, X x);

  boolean onDelete(X x, Column column);

  boolean onDelete(T t, X x);

  boolean onDelete(X x);

  <C> boolean onDelete(X x, Column<? extends C> column, List<? extends C> list);

  long onSave(T t, X x);

  boolean onSave(SList<? extends T> list, X x);

  boolean onDrop(X x) throws ModelError;

  void backup(X x);

  void restore(X x);

  int onGetLastId(X x);

  String getName();

  interface Extras<T extends Identifiable<Integer>> {
    @Nullable
    T first();

    @Nullable
    T last();

    SList<? extends T> all();

    SList<? extends T> limit(int limit);

    SList<? extends T> paginate(int offset, int limit);

    <N extends Number> SList<? extends T> between(Column<? extends N> column, N a, N b);

    SList<? extends T> where(Column... column);

    <N extends Number> SList<? extends T> notIn(Column<? extends N> column, N... bounds);

    SList<? extends T> like(Column... column);

    SList<? extends T> orderBy(Column column);

    SList<? extends T> groupBy(Column column);

    SList<? extends T> groupAndOrderBy(Column column, Column column1);
  }
}
