package promise.app.mock

import androidx.collection.ArrayMap
import com.github.javafaker.Faker
import dev4vin.promise.SingletonInstanceProvider
import dev4vin.promise.model.List
import dev4vin.promise.repo.AbstractAsyncIDataStore
import dev4vin.promise.repo.AbstractSyncIDataStore
import dev4vin.promise.repo.StoreRepository
import promise.app.storage.MockDatabase
import promise.app.storage.MockDatabaseProvider
import dev4vin.promise.PromiseCallback as Promise

val mockDatabase: MockDatabase = MockDatabaseProvider.instance()

class AsyncMockRepo : AbstractAsyncIDataStore<MockObject>() {
  private fun someSearchItems(number:Int): List<MockObject> {
    val list = List<MockObject>()
    for (i in 0 until number) {
      list.add(MockObject(i, Faker().name().name()))
    }
    return list
  }

  override fun all(res: (List<out MockObject>, Any?) -> Unit, err: ((Exception) -> Unit)?, args: Map<String, Any?>?) {
    val numberOfMocks = args!!["numberOfMocks"] as Int
    res(someSearchItems(numberOfMocks), "From faker")
  }
}

class SyncMockRepo : AbstractSyncIDataStore<MockObject>() {
  private fun randomName(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
    return (1..10)
        .map { allowedChars.random() }
        .joinToString("")
  }

  override fun all(args: Map<String, Any?>?): Pair<List<out MockObject>?, Any?> {
    val numberOfMocks = args!!["numberOfMocks"] as Int
    val mocks = List<MockObject>()
    for (i in 0 until numberOfMocks) {
      mocks.add(MockObject(i, randomName()))
    }
    return Pair(mocks, "From random")
  }
}

val mockRepoStore = StoreRepository.of(SyncMockRepo::class, AsyncMockRepo::class)

class MockRepo {

  fun getMockObjects(numberOfMocks: Int): Promise<List<MockObject>> = Promise { resolve, _ ->
    val mocksInfo = ArrayMap<String, Any>().apply {
      put("numberOfMocks", numberOfMocks)
    }
    val (mocks, info1) = mockRepoStore.all(mocksInfo)
    mockRepoStore.all(mocksInfo, { mocks1, info2 ->
      resolve(List(mocks!!.plus(mocks1)).shuffled(), info1 as String + " and " + info2 as String)
    })
  }

}