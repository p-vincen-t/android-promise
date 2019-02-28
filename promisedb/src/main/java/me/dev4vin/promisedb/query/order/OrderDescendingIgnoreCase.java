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

package me.dev4vin.promisedb.query.order;


import me.dev4vin.promisedb.Utils;
import me.dev4vin.promisedb.query.projection.Projection;
import me.dev4vin.promisemodel.List;
import me.dev4vin.promisemodel.function.MapFunction;

public class OrderDescendingIgnoreCase extends Order {

  public OrderDescendingIgnoreCase(Projection projection) {
    super(projection);
  }

  @Override
  public String build() {
    String ret = " COLLATE NOCASE DESC";

    if (projection != null) ret = projection.build() + ret;

    return ret;
  }

  @Override
  public List<String> buildParameters() {
    if (projection != null) return projection.buildParameters();
    else
      return Utils.EMPTY_LIST.map(
          new MapFunction<String, Object>() {
            @Override
            public String from(Object o) {
              return String.valueOf(o);
            }
          });
  }
}
