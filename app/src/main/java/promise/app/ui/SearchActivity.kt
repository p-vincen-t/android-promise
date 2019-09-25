package promise.app.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.util.Pair
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dev4vin.promise.IntermediateResult
import dev4vin.promise.Promise
import dev4vin.promise.data.log.LogUtil
import dev4vin.promise.tx.Tx
import dev4vin.promise.tx.TxManager
import dev4vin.promise.view.SearchableAdapter
import dev4vin.promise.view.loading.LoadingViewable
import kotlinx.android.synthetic.main.activity_search.*
import promise.app.R
import promise.app.mock.MockObject
import promise.app.mock.MockRepo
import promise.app.mock.mockRepoStore
import promise.app.models.SearchableItem

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
    loading_view.showLoading()
    /*TxManager.instance().execute(object: Tx<MockObject, String, Int>() {
      override fun getProgress(): Progress<MockObject, String> =
          object: Progress<MockObject, String> {
            override fun onCalculateProgress(t: MockObject): String = t.string

            override fun onProgress(x: String) {
              title = x
            }
          }

      *//**
       * gets the callback methods used for executing the transaction
       *
       * @return a callbacks object
       *//*
      override fun getCallBackExecutor(): CallBackExecutor<MockObject, Int> {
        return CallBackExecutor {

        }
      }
    }.complete {

    }, Pair(arrayOf(10, 20, 30, 40), 0))*/
    Promise.instance().execute {
      MockRepo().getMockObjects(100)
          .then { list, any ->
            Promise.instance().executeOnUi {
              if (any is String) {
                title = any + "(" + list.size + ")"
              }
            }
            IntermediateResult(list, any)
          }
          .then { list, _ ->
            Promise.instance().executeOnUi {
              loading_view.showContent()
            }
            searchableAdapter.add(list.map {
              SearchableItem((it as MockObject).string)
            })
            null
          }.error {
            LogUtil.e(TAG, " error ", it)
            null
          }
          .execute()
    }

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
