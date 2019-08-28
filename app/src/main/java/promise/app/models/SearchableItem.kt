package promise.app.models

import android.view.View
import android.widget.TextView
import promise.app.R
import dev4vin.promise.data.log.LogUtil
import dev4vin.promise.model.Searchable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.graphics.Color


class SearchableItem(private val item: String) : Searchable {
  private val TAG = LogUtil.makeTag(SearchableItem::class.java)

  override fun onSearch(query: String): Boolean {
    if (item.toLowerCase().contains(query.toLowerCase())) {
      val spannable = SpannableString(item)
      spannable.setSpan(ForegroundColorSpan(Color.GREEN),
          item.indexOf(query), item.indexOf(query) + query.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

      textView.setText(spannable, TextView.BufferType.SPANNABLE)
      return true
    }
    return false
  }

  @Transient
  private lateinit var textView: TextView

  @Transient
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