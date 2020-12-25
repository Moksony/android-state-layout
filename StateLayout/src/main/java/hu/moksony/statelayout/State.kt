package hu.moksony.statelayout

import android.content.Context
import android.view.View
import android.view.ViewGroup

open class State(val stateId: Int) {
    var viewId: Int? = null
    var view: View? = null
        set(value) {
            field = value
            viewId = value?.id
        }

    open fun createView(parent: ViewGroup, context: Context): View? {
        return null
    }
}