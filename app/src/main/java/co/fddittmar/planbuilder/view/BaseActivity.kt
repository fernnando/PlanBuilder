package co.fddittmar.planbuilder.view

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 * Base Activity to store all common variables and methods to be visible to all activities
 */

open class BaseActivity : AppCompatActivity(){
    /**
     * This method works like the binder of the ButterKnife library.
     *
     * @param res id resource of the view (ex.: R.id.my_text_view)
     * @return a View with type casting
     */
    fun <T : View> Activity.bind(@IdRes res : Int) : T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res) as T
    }
}
