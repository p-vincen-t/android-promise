package promise.app.mock

import com.github.javafaker.Faker
import dev4vin.promise.model.List
import dev4vin.promise.PromiseCallback as Promise

class MockRepo {
  fun getMockObjects(numberOfMocks: Int): Promise<List<Any>> = Promise { resolve, _ ->
  val mocks = List<Any>()
  for (i in 0 until numberOfMocks) {
    mocks.add(MockObject(i, randomName()))
  }
  mocks.addAll(someSearchItems())
  resolve(mocks.shuffled(), "Mock objects")
}

  private fun randomName(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
    return (1..10)
        .map { allowedChars.random() }
        .joinToString("")
  }

  private fun someSearchItems(): List<MockObject> {
    val list = List<MockObject>()
    for (i in 0 until 20) {
      list.add(MockObject(i, Faker().name().name()))
    }
    return list
  }
}