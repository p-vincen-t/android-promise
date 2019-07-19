package promise.app.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.javafaker.Faker
import kotlinx.android.synthetic.main.activity_search.*
import promise.Promise
import promise.app.R
import promise.app.models.SearchableItem
import promise.model.List
import promise.model.ResponseCallBack
import promise.view.SearchableAdapter

class SearchActivity : AppCompatActivity() {

  private lateinit var searchableAdapter: SearchableAdapter<SearchableItem>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_search)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    searchableAdapter = SearchableAdapter(null)
    search_recyclerView.layoutManager = LinearLayoutManager(this)
    search_recyclerView.itemAnimator = DefaultItemAnimator()
    search_recyclerView.adapter = searchableAdapter
    Promise.instance().execute({ someSearchItems() },
        ResponseCallBack<List<SearchableItem>, Throwable>()
        .response {
          Promise.instance().executeOnUi {
            searchableAdapter.add(it)
          }
        })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    val search = menu.findItem(R.id.action_search)
    val searchView = search.actionView as SearchView
    searchView.isFocusable = false
    searchView.queryHint = getString(R.string.searching)
    searchView.setOnQueryTextListener(
        object : SearchView.OnQueryTextListener {

          override fun onQueryTextSubmit(s: String): Boolean {
            searchableAdapter.search(s)
            return true
          }

          override fun onQueryTextChange(s: String): Boolean {
            if (TextUtils.isEmpty(s)) return false
            searchableAdapter.search(s)
            return true
          }
        })
    return true
  }

  private fun someSearchItems(): List<SearchableItem> {
    val list = List<SearchableItem>()
    repeat((0 until 20).count()) {
      list.add(SearchableItem(Faker().name().name()))
    }
    return list
  }
}
