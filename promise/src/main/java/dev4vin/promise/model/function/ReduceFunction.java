package dev4vin.promise.model.function;

import dev4vin.promise.model.List;

/**
 * Created on 7/5/18 by yoctopus.
 */
public interface ReduceFunction<K, T> {
    K reduce(List<T> list);
}
