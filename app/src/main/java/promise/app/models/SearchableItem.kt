package promise.app.models

import android.view.View
import android.widget.TextView
import promise.app.R
import promise.data.log.LogUtil
import promise.model.Searchable

class SearchableItem(private val item: String) : Searchable {
  private val TAG = LogUtil.makeTag(SearchableItem::class.java)

  override fun onSearch(query: String): Boolean {
    LogUtil.e(TAG, "_item_ ",this.item, " _query_ ", query)
    val matched = item.toLowerCase().contains(query.toLowerCase())
    if (matched) LogUtil.e(TAG, "matched_ ",matched)
    return matched
  }

  private lateinit var textView: TextView

  private var index: Int = 0

  override fun layout(): Int = R.layout.test_pojo_layout

  override fun init(view: View) {
    textView = view.findViewById(R.id.some_variable_textView)
  }

  override fun bind(view: View?) {
    textView.text = item
  }

  override fun index(index: Int) {
    this.index = index
  }

  override fun index(): Int = index
}