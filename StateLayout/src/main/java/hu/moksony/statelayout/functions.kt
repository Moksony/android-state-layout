package hu.moksony.statelayout

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.state(@IdRes id: Int, createView: () -> View): State {
    return createState(id,createView)
}

fun Fragment.state(@IdRes id: Int, createView: () -> View): State {
    return createState(id,createView)
}

fun createState(@IdRes id: Int, createView: () -> View): State {
    return object : State(id) {
        override fun createView(parent: ViewGroup, context: Context): View? {
            return createView.invoke()
        }
    }
}