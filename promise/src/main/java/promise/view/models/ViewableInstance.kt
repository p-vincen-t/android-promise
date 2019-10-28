package promise.view.models


import android.view.View
import promise.makeInstance
import promise.model.Viewable
import kotlin.reflect.KClass

class ViewableInstance<T>(val t: T) {

  private var viewable: Viewable? = null

  var viewClass: KClass<*>? = null

  var viewClassObject: Any? = null

  private fun convert() {
    this.viewable = when {
      viewClass != null -> {
        viewClassObject =  makeInstance(viewClass!!, arrayOf(t as Any)) as Viewable
        viewClassObject as Viewable
      }
      t is Viewable -> t
      (t as Any).javaClass.isAnnotationPresent(promise.view.scopes.Viewable::class.java) -> {
        val annotation = t.javaClass.getAnnotation(promise.view.scopes.Viewable::class.java)!!
        viewClass = annotation.viewHolderClass
        viewClassObject = makeInstance(viewClass!!, arrayOf(t as Any)) as ViewHolder
        object : Viewable {
          var index =  0
          override fun layout(): Int = annotation.layoutResource

          override fun index(): Int = index

          override fun index(index: Int) {
            this.index = index
          }

          override fun init(view: View?) {
            (viewClassObject!! as ViewHolder).init(view)
          }

          override fun bind(view: View?) {
            (viewClassObject!! as ViewHolder).bind(view)
          }
        }
      }
      else -> throw IllegalStateException("$t must be an instance on Viewable or have Viewable annotation")
    }

  }

  fun viewable(): Viewable {
    if (viewable != null) return viewable!!
    convert()
    return viewable!!
  }
}
