package dev4vin.promise.data.db.query.order;

import dev4vin.promise.data.db.Utils;
import dev4vin.promise.data.db.query.projection.Projection;
import dev4vin.promise.model.List;
import dev4vin.promise.model.function.MapFunction;

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
