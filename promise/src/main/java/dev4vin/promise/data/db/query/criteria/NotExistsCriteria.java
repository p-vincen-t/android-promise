package dev4vin.promise.data.db.query.criteria;

import dev4vin.promise.data.db.query.QueryBuilder;

public class NotExistsCriteria extends ExistsCriteria {
  public NotExistsCriteria(QueryBuilder subQuery) {
    super(subQuery);
  }

  @Override
  public String build() {
    return "NOT " + super.build();
  }
}
