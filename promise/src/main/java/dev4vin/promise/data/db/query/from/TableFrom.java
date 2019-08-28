package dev4vin.promise.data.db.query.from;

import dev4vin.promise.data.db.Table;
import dev4vin.promise.data.db.Utils;
import dev4vin.promise.model.List;
import dev4vin.promise.model.function.MapFunction;

public class TableFrom extends AliasableFrom<TableFrom> {
  private Table table;

  public TableFrom(Table table) {
    this.table = table;
  }

  @Override
  public String build() {
    String ret = (!Utils.isNullOrWhiteSpace(table.getName()) ? table.getName() : "");

    if (!Utils.isNullOrWhiteSpace(alias)) ret = ret + " AS " + alias;

    return ret;
  }

  @Override
  public List<String> buildParameters() {
    return Utils.EMPTY_LIST.map(
        new MapFunction<String, Object>() {
          @Override
          public String from(Object o) {
            return String.valueOf(o);
          }
        });
  }
}
