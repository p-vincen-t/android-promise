package dev4vin.promise

import dev4vin.promise.data.log.LogUtil
import dev4vin.promise.model.List

data class IntermediateResult<RESULT>(val result: RESULT, val any: Any?)

/**
 * promise callback executor chain
 *
 * @property resultConsumer
 */

class PromiseCallback<RESULT>(private val resultConsumer: (resolve: (RESULT, Any?) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
   /**
   * holds the result of then execution
   */
  private var lastResult: IntermediateResult<RESULT>? = null
  /**,
   * last error caught by the system
   */
  private var lastError: Throwable? = null

  /**
   * list of result acceptors
   */
  private val resultAcceptors: List<(RESULT, Any?) -> IntermediateResult<RESULT>?> = List()
  /**
   * list of error acceptors
   */
  private val errorAcceptors: List<(Throwable) -> Throwable?> = List()

  /**
   * registers result acceptors
   *
   * @param resultAcceptor result acceptor
   * @return
   */
  fun then(resultAcceptor: (RESULT, Any?) -> IntermediateResult<RESULT>?): PromiseCallback<RESULT> {
    this.resultAcceptors.add(resultAcceptor)
    return this
  }

  /**
   * registers error acceptors
   *
   * @param errorAcceptor error acceptor
   * @return
   */
  fun error(errorAcceptor: (Throwable) -> Throwable?): PromiseCallback<RESULT> {
    this.errorAcceptors.add(errorAcceptor)
    return this
  }

  /**
   * executes the promise code
   *
   */
   fun execute() {
    try {
      resultConsumer({ result, any ->
        try {
          lastResult = IntermediateResult(result, any)
          cycleResultAccepting()
        } catch (e: Throwable) {
          lastError = e
          cycleErrorAccepting()
        }
      }, { throwable ->
        lastError = throwable
        cycleErrorAccepting()
      })
    } catch(e: Throwable) {
      lastError = e
      cycleErrorAccepting()
    }
  }

  private fun cycleResultAccepting() {
    for (acceptor in resultAcceptors) {
      lastResult = (if (lastResult != null) {
        val stepResponse = acceptor(lastResult!!.result, lastResult!!.any)
        if (stepResponse != null) {
          val (res, args1) = stepResponse
          if (res is Throwable) {
            lastError = res
            cycleErrorAccepting()
            break
          }
          IntermediateResult(res, args1)
        }
        stepResponse
      } else acceptor(lastResult!!.result, lastResult!!.any))
    }
  }

  private fun cycleErrorAccepting() {
    if (errorAcceptors.isEmpty()) {
      LogUtil.e(TAG, "No error acceptors found ")
      return
    }
    for (acceptor in errorAcceptors) {
      lastError = (if (lastError != null) {
        val stepError = acceptor(lastError!!)
        if (stepError != null) {
          lastError = stepError
          cycleErrorAccepting()
          break
        }
        null
      } else acceptor(lastError!!))
    }
  }

  companion object {
    val TAG = LogUtil.makeTag(PromiseCallback::class.java)
  }

}
