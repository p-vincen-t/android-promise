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

package dev4vin.promise.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import dev4vin.promise.util.Conditions;
import dev4vin.promise.model.function.BIConsumer;
import dev4vin.promise.model.function.Combiner;
import dev4vin.promise.model.function.EachFunction;
import dev4vin.promise.model.function.FilterFunction2;
import dev4vin.promise.model.function.GroupFunction;
import dev4vin.promise.model.function.GroupFunction2;
import dev4vin.promise.model.function.GroupFunction3;
import dev4vin.promise.model.function.JoinFunction;
import dev4vin.promise.model.function.MapFunction;
import dev4vin.promise.model.function.ReduceFunction;
import dev4vin.promise.util.Conditions;

/**
 * Created on 5/13/18 by yoctopus.
 */
public class List<T> extends ArrayList<T> {
  /**
   * Constructs an empty list with the specified initial capacity.
   *
   * @param initialCapacity the initial capacity of the list
   * @throws IllegalArgumentException if the specified initial capacity is negative
   */
  public List(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Constructs an empty list with an initial capacity of ten.
   */
  public List() {
  }

  /**
   * Constructs a list containing the elements of the specified collection, in the order they are
   * returned by the collection's iterator.
   *
   * @param c the collection whose elements are to be placed into this list
   * @throws NullPointerException if the specified collection is null
   */
  public List(@NonNull Collection<? extends T> c) {
    super(c);
  }

  @SafeVarargs
  public static <T> List<T> fromArray(T... t) {
    return new List<>(Arrays.asList(t));
  }

  public List<T> reverse() {
    Collections.reverse(this);
    return this;
  }

  public List<T> sample(int size) {
    List<T> list = new List<>();
    if (size() > size) for (int i = 0; i < size; i++) list.add(get(i));
    else list = this;
    return list;
  }

  @Nullable
  public T find(EachFunction<? super T> function) {
    return filter(function).first();
  }

  public int findIndex(EachFunction<? super T> function) {
    int match = 0;
    for (T t : this) {
      if (function.filter(t)) return match;
      match++;
    }
    return -1;
  }

  public List<T> shuffled() {
    Collections.shuffle(this);
    return this;
  }

  public <E> List<E> map(MapFunction<? extends E, ? super T> function) {
    List<E> list = new List<>();
    for (T t : this) list.add(function.from(t));
    return list;
  }

  public List<T> sorted(Comparator<? super T> comparator) {
    Collections.sort(this, comparator);
    return this;
  }

  public <K> List<T> arranged(final MapFunction<? extends K, ? super T> function, final Comparator<? super K> comparator) {
    return map(t -> new Arrangeable<T, K>().value(t).key(function.from(t)))
        .sorted(
            (o1, o2) -> comparator.compare(o1.key(), o2.key()))
        .map(
            arrangeable -> arrangeable.value());
  }

  public <E> List<E> group(GroupFunction3<E, T> function) {
    return function.group(this);
  }

  public <K, E> List<E> group(GroupFunction2<K, E, T> function) {
    List<E> es = new List<>();
    Map<K, List<T>> map = new ArrayMap<>();
    for (int i = 0, size = this.size(); i < size; i++) {
      T t = this.get(i);
      K key = function.getKey(t);
      if (map.containsKey(key)) {
        List<T> list = Conditions.checkNotNull(map.get(key));
        list.add(t);
      } else {
        List<T> list = new List<>();
        list.add(t);
        map.put(key, list);
      }
    }
    for (Map.Entry<K, List<T>> entry : map.entrySet()) {
      E e = function.get(entry.getKey());
      function.apply(e, entry.getValue());
      es.add(e);
    }
    return es;
  }

  public <K> List<Category<K, T>> groupBy(final GroupFunction<? extends K, ? super T> function) {
    return group(
        new GroupFunction2<K, Category<K, T>, T>() {
          @Override
          public K getKey(T t) {
            return function.getKey(t);
          }

          @Override
          public Category<K, T> get(K k) {
            return new Category<>(k);
          }

          @Override
          public void apply(Category<K, T> category, List<T> list) {
            category.list(list);
          }
        });
  }

  public <U> List<T> joinOn(List<? extends U> uList, JoinFunction<? super T, ? super U> function) {
    List<T> ts = new List<>();
    for (int i = 0, tSize = this.size(); i < tSize; i++) {
      T t = this.get(i);
      for (int i1 = 0, uSize = uList.size(); i1 < uSize; i1++) {
        U u = uList.get(i1);
        if (function.joinBy(t, u)) {
          ts.add(t);
          break;
        }
      }
    }
    return ts;
  }

  public <U> List<T> join(List<? extends U> uList, Combiner<? super U, T> function) {
    if (this.size() != uList.size())
      throw new IllegalArgumentException("Samples must be of same size");
    List<T> ts = new List<>();
    for (int i = 0, size = this.size(); i < size; i++) {
      T t = this.get(i);
      U u = uList.get(i);
      ts.add(function.join(t, u));
    }
    return ts;
  }

  public List<T> uniques() {
    return new List<>(new HashSet<>(this));
  }

  public <K> List<T> uniques(MapFunction<? extends K, ? super T> function, JoinFunction<? super T, ? super K> joinFunction) {
    return joinOn(map(function).uniques(), joinFunction);
  }

  public <U, K> List<T> reduce(List<? extends U> uList, final FilterFunction2<? extends K, ? super U, ? super T> function, final boolean reverse) {
    final Set<K> set = new HashSet<>(uList.map(
        function::getKey));
    return filter(
        t -> {
          if (reverse) return set.contains(function.filterBy(t));
          else return !set.contains(function.filterBy(t));
        });
  }

  public <K, U> List<T> merge(List<U> list, final boolean reverse, final FilterFunction2<? extends K, ? super U, ? super T> function,
      Combiner<U, T> combiner) {
    final Set<K> set = new HashSet<>(list.map(
        function::getKey));
    return filter(
        t -> reverse == set.contains(function.filterBy(t)))
        .join(list, combiner);
  }

  public <K, MERGE> List<Pair<T, K>> mergeWith(List<K> list, final FilterFunction2<MERGE, K, T> function) {
    List<Pair<T, K>> list1 = new List<>();
    /*Set<Arrangeable<K, MERGE>> set = new HashSet<>(
        list.map(new MapFunction<Arrangeable<K, MERGE>, K>() {
          @Override
          public Arrangeable<K, MERGE> from(K k) {
            return new Arrangeable<K, MERGE>().key(function.getKey(k)).value(k);
          }
        })
    );*/
    for (T t : this)
      for (K k : list)
        if (function.getKey(k).equals(function.filterBy(t))) list1.add(new Pair<>(t, k));
    return list1;
  }

  public boolean anyMatch(EachFunction<? super T> function) {
    boolean match = false;
    for (T t : this)
      if (function.filter(t)) {
        match = true;
        break;
      }
    return match;
  }

  public boolean allMatch(EachFunction<? super T> function) {
    boolean match = true;
    for (T t : this)
      match = match && function.filter(t);
    return match;
  }

  public List<T> filter(EachFunction<? super T> function) {
    List<T> list = new List<>();
    for (T t : this) if (function.filter(t)) list.add(t);
    return list;
  }

  public <U> List<T> reduce(List<? extends U> list, JoinFunction<? super T, ? super U> function) {
    List<T> ts = new List<>();
    for (int i = 0; i < this.size(); i++) {
      T t = this.get(i);
      for (int j = 0; j < list.size(); j++)
        if (!function.joinBy(t, list.get(j))) {
          ts.add(t);
          break;
        }
    }
    return ts;
  }

  public void biConsume(BIConsumer<? super T, ? super T> consumer) {
    Iterator<T> it = iterator();
    if (!it.hasNext()) return;
    T first = it.next();
    while (it.hasNext()) {
      T next = it.next();
      consumer.accept(first, next);
      first = next;
    }
  }

  @Nullable
  public T first() {
    return this.isEmpty() ? null : this.get(0);
  }

  @Nullable
  public T last() {
    return this.isEmpty() ? null : get(size() - 1);
  }

  public <K> K reduce(ReduceFunction<? extends K, T> function) {
    return function.reduce(this);
  }
}
