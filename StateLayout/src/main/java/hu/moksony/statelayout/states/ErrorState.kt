package hu.moksony.statelayout.states

import android.view.View
import androidx.annotation.DrawableRes
import hu.moksony.statelayout.R

class ErrorState(stateId: Int, val retryButtonId: Int) : State(stateId) {
    class Builder : State.Builder<ErrorState>() {
        private var _retryCallback: View.OnClickListener? = null
        private var _buttonId: Int = R.id.state_error_view_retry

        fun setRetryCallback(listener: View.OnClickListener): Builder {
            this._retryCallback = listener
            return this
        }

        override fun createState(): ErrorState {
            return ErrorState(this.requireId(), _buttonId)
        }

        override fun apply(state: State) {
            super.apply(state)
            (state as ErrorState).apply {
                retryCallback = _retryCallback
            }
        }

    }

    var retryCallback: View.OnClickListener? = null

    override fun onView(view: View) {
        super.onView(view)
        view.findViewById<View>(retryButtonId)?.setOnClickListener(retryCallback)
    }
}