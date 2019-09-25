package dev4vin.promise.model.function;

public interface ReturnConsumer<R, T> {
  public R accept(T t);
}
