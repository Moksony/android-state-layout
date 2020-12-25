package hu.moksony.statelayout

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.state(createView: () -> View): State {
    return createState(createView)
}

fun Fragment.state(createView: () -> View): State {
    return createState(createView)
}

fun createState(createView: () -> View): State {
    return object : State() {
        override fun createView(parent: ViewGroup, context: Context): View? {
            return createView.invoke()
        }
    }
}