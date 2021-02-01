package hu.moksony.statelayout.states

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.BaseObservable
import hu.moksony.statelayout.R

open class State(val viewId: Int) : BaseObservable() {
    companion object {
        val ContentState = hu.moksony.statelayout.states.ContentState()
    }

    open class Builder<S> {
        private var _viewId: Int? = null
        private var _view: View? = null
        private var _layoutId: Int? = null
        private var _drawable: Int? = null
        private var _allowOnContent: Boolean = false
        private var _msgString: String? = null
        private var _msgResId: Int? = null

        fun setViewId(@IdRes viewId: Int): Builder<S> {
            this._viewId = viewId
            return this
        }

        fun setLayoutId(@LayoutRes layoutId: Int): Builder<S> {
            this._layoutId = layoutId
            return this
        }

        fun setMessage(msg: String): Builder<S> {
            this._msgString = msg
            return this
        }

        fun setMessage(msg: Int): Builder<S> {
            this._msgResId = msg
            return this
        }


        fun setAllowOnContent(allow: Boolean): Builder<S> {
            this._allowOnContent = allow
            return this
        }

        fun setDrawable(@DrawableRes id: Int): Builder<S> {
            this._drawable = id
            return this
        }

        protected fun requireId(): Int {
            return _viewId ?: _view?.id ?: -1
        }


        protected open fun createState(): State {
            return State(requireId())
        }

        fun build(): State {
            val state = this.createState()
            this.apply(state)
            return state
        }

        open fun apply(state: State) {
            state.layoutId = this._layoutId
            state.allowOnContent = this._allowOnContent
            state.drawableResId = this._drawable
            state.msgResId = this._msgResId
            state.msgString = this._msgString
        }
    }

    var layoutId: Int? = null
    var drawableResId: Int? = null
        set(value) {
            if (field != value) {
                field = value
                updateUI()
            }
        }

    var msgString: String? = null
        set(value) {
            if (field != value) {
                field = value
                updateUI()
            }
        }
    var msgResId: Int? = null
        set(value) {
            if (field != value) {
                field = value
                updateUI()
            }
        }

    var view: View? = null
        set(value) {
            if (field != value) {
                field = value
                if (value != null) {
                    onView(value)
                }
            }
        }

    var stateTextView: TextView? = null
    var stateImageView: ImageView? = null
    var allowOnContent: Boolean = false


    fun updateUI() {
        this.stateTextView?.let {
            it.post {
                var msString = this.msgString
                val msResId = this.msgResId
                if (msResId != null) {
                    msString = it.context.getString(msResId)
                }
                it.text = msString
            }
        }
        this.drawableResId?.let {
            this.stateImageView?.setImageResource(it)
        }

    }

    protected open fun onView(view: View) {
        this.stateImageView = view.findViewById(R.id.state_view_image)
        this.stateTextView = view.findViewById(R.id.state_view_text)
        updateUI()
    }

}