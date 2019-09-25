package promise.app.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dev4vin.promise.IntermediateResult
import dev4vin.promise.Promise
import dev4vin.promise.data.log.LogUtil
import dev4vin.promise.view.SearchableAdapter
import kotlinx.android.synthetic.main.activity_search.*
import promise.app.R
import promise.app.mock.MockObject
import promise.app.mock.MockRepo
import promise.app.models.SearchableItem
import java.lang.IllegalStateException

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
    Promise.instance().execute {
      MockRepo().getMockObjects(50)
          .then { list, args ->
            IntermediateResult(list, args)
          }.then { list, any ->

            IntermediateResult(list, any)
          }
          .then { list, any ->
            /*searchableAdapter.add(list.map {
              SearchableItem(it.string)
            })*/
            IntermediateResult(list, any)
          }
          .then { _, any ->
            Promise.instance().executeOnUi {
              if (any is String) {
                title = any
              }
            }
            null
          }.error {
            LogUtil.e(TAG, " error ", it)
            null
          }
          .execute()
    }


    /* Promise.instance().execute({ someSearchItems() },
         ResponseCallBack<List<SearchableItem>, Throwable>()
             .withCallback {
               Promise.instance().executeOnUi {

               }
             })*/
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
            Promise.instance().executeOnUi {
              searchableAdapter.search(s)
            }
            return true
          }

          override fun onQueryTextChange(s: String): Boolean {
            if (TextUtils.isEmpty(s)) return false
            onQueryTextSubmit(s)
            return true
          }
        })
    return true
  }


  companion object {
    val TAG = LogUtil.makeTag(SearchActivity::class.java)
  }
}
