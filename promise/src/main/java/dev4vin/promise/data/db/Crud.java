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


import dev4vin.promise.model.Identifiable;
import dev4vin.promise.model.List;
import dev4vin.promise.model.SList;


interface Crud<X> {

    <T extends Identifiable<Integer>> Table.Extras<T> read(Table<T, ? super X> table);

    <T extends Identifiable<Integer>> SList<? extends T> readAll(Table<T, ? super X> table);

    <T extends Identifiable<Integer>> SList<? extends T> readAll(Table<T, ? super X> table, Column column);

    <T extends Identifiable<Integer>> boolean update(T t, Table<T, ? super X> table, Column column);

    <T extends Identifiable<Integer>> boolean update(T t, Table<T, ? super X> table);

    <T extends Identifiable<Integer>> SList<? extends T> readAll(Table<T, ? super X> table, Column[] columns);

    boolean delete(Table<?, ? super X> table, Column column);

    <T extends Identifiable<Integer>> boolean delete(Table<T, ? super X> table, T t);
    <T> boolean delete(Table<?, ? super X> table, Column<? extends T> column, List<? extends T> list);

    <T extends Identifiable<Integer>> long save(T t, Table<T, ? super X> table);

    <T extends Identifiable<Integer>> boolean save(SList<? extends T> list, Table<T, ? super X> table);

    boolean deleteAll();

    int getLastId(Table<?, ? super X> table);
}
