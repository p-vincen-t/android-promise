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

package me.dev4vin.samplepromiseapp.auth;

public class Role {
  public static final String CAN_ADD_TODO = "can_add_todo", CAN_UPDATE_TODO = "can_update_todo";

  private String name;
  private boolean allowed;

  public String name() {
    return name;
  }

  public Role name(String name) {
    this.name = name;
    return this;
  }

  public boolean allowed() {
    return allowed;
  }

  public Role allowed(boolean allowed) {
    this.allowed = allowed;
    return this;
  }
}
