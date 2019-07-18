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

package promise.view

import android.widget.Filter
import android.widget.Filterable
import promise.data.log.LogUtil
import promise.model.List
import promise.model.Searchable
import promise.util.Conditions

/**
 * Created by yoctopus on 11/21/17.
 */

class SearchableAdapter<T : Searchable>(listener: Listener<T>) : PromiseAdapter<T>(listener), Filterable {

  private val TAG = LogUtil.makeTag(SearchableAdapter::class.java)

  private val originalList: List<T> = List()

  override fun add(t: T) {
    super.add(t)
    originalList.add(Conditions.checkNotNull(t))
  }

  override fun add(list: List<T>) {
    super.add(list)
    originalList.addAll(Conditions.checkNotNull(list))
  }

  fun search(query: String) = filter.filter(query)

  override fun getFilter(): Filter = object : Filter() {
    override fun performFiltering(charSequence: CharSequence?): FilterResults? {
      if (charSequence.isNullOrEmpty() or charSequence.isNullOrBlank()) return null
      val filterData: List<T>?
      filterData = originalList.filter { t -> t.onSearch(charSequence.toString()) }
      val filterResults = FilterResults()
      filterResults.values = filterData
      return filterResults
      /*LogUtil.e(TAG, charSequence)
      val results = FilterResults()
      val filterData: List<T>?
      filterData = if (charSequence != null)
        originalList.filter { t -> t.onSearch(charSequence.toString()) }
      else
        originalList
      results.values = filterData
      if (filterData != null)
        results.count = filterData.size
      return results*/
    }

    override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
      if (filterResults != null && filterResults.count > 0) setList(filterResults.values as List<T>)
    }
  }
}
