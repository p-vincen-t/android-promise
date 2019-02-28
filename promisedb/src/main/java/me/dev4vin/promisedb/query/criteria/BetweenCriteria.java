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

package me.dev4vin.promisedb.query.criteria;


import me.dev4vin.promisedb.query.projection.AliasedProjection;
import me.dev4vin.promisedb.query.projection.Projection;
import me.dev4vin.promisemodel.List;

public class BetweenCriteria extends Criteria {
  private Projection projection;
  private Object valueStart;
  private Object valueEnd;

  public BetweenCriteria(Projection projection, Object valueStart, Object valueEnd) {
    this.projection = projection;
    this.valueStart = valueStart;
    this.valueEnd = valueEnd;

    if (this.projection instanceof AliasedProjection)
      this.projection = ((AliasedProjection) this.projection).removeAlias();
  }

  @Override
  public String build() {
    StringBuilder sb = new StringBuilder();

    if (projection != null) sb.append(projection.build());

    sb.append(" BETWEEN ");
    sb.append((valueStart != null ? "?" : "NULL"));
    sb.append(" AND ");
    sb.append((valueEnd != null ? "?" : "NULL"));

    return sb.toString();
  }

  @Override
  public List<String> buildParameters() {
    List<String> ret = new List<>();

    if (projection != null) ret.addAll(projection.buildParameters());

    if (valueStart != null) ret.add(String.valueOf(valueStart));

    if (valueEnd != null) ret.add(String.valueOf(valueEnd));

    return ret;
  }
}
