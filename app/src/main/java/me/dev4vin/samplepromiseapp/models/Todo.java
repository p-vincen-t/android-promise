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

package me.dev4vin.samplepromiseapp.models;

import android.os.Parcel;
import android.view.View;

import me.dev4vin.promisedb.SModel;
import me.dev4vin.promiseui.Searchable;

public class Todo extends SModel implements Searchable {
  private String category;
  private String name;
  private boolean completed;

  public String category() {
    return category;
  }

  public Todo category(String category) {
    this.category = category;
    return this;
  }

  public String name() {
    return name;
  }

  public Todo name(String name) {
    this.name = name;
    return this;
  }

  public boolean completed() {
    return completed;
  }

  public Todo completed(boolean completed) {
    this.completed = completed;
    return this;
  }

  @Override
  public boolean onSearch(String query) {
    return false;
  }

  @Override
  public int layout() {
    return 0;
  }

  @Override
  public void init(View view) {

  }

  @Override
  public void bind(View view) {

  }

  int index;

  @Override
  public void index(int index) {
    this.index = index;
  }

  @Override
  public int index() {
    return index;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.category);
    dest.writeString(this.name);
    dest.writeByte(this.completed ? (byte) 1 : (byte) 0);
    dest.writeInt(this.index);
  }

  @Override
  public String toString() {
    return "Todo{" +
        "category='" + category + '\'' +
        ", name='" + name + '\'' +
        ", completed=" + completed +
        ", index=" + index +
        '}';
  }

  /*@Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Todo todo = (Todo) o;
    return completed == todo.completed &&
        index == todo.index &&
        Objects.equals(category, todo.category) &&
        Objects.equals(name, todo.name);
  }

  @Override
  public int hashCode() {

    return Objects.hash(category, name, completed, index);
  }*/

  public Todo() {
  }

  private Todo(Parcel in) {
    super(in);
    this.category = in.readString();
    this.name = in.readString();
    this.completed = in.readByte() != 0;
    this.index = in.readInt();
  }

  public static final Creator<Todo> CREATOR = new Creator<Todo>() {
    @Override
    public Todo createFromParcel(Parcel source) {
      return new Todo(source);
    }

    @Override
    public Todo[] newArray(int size) {
      return new Todo[size];
    }
  };
}
