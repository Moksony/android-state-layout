package hu.moksony.statelayout.states

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.IdRes
import hu.moksony.statelayout.R

class ProgressState(stateId: Int) : State(stateId) {

    private var max: Int = 0
        set(value) {
            if (field != value) {
                field = value
                updateView()
            }
        }

    private var progress: Int = 0
        set(value) {
            if (field != value) {
                field = value
                updateView()
            }
        }

    var progressView: ProgressBar? = null

    fun updateView() {
        this.progressView?.let { pb ->
            pb.post {
                pb.max = this.max
                pb.progress = this.progress
            }
        }
    }

    fun progressAdvice(num: Int = 1) {
        this.progress += num
        Log.d("ProgressState", "${this.progress}/${this.max}")
    }

    class Builder : State.Builder<ProgressState>() {
        private var _max: Int = 0
        private var _progressId: Int = R.id.state_progress_view_progress

        fun setMax(max: Int): Builder {
            this._max = max
            return this
        }

        fun setProgressViewId(@IdRes progressId: Int): Builder {
            this._progressId = progressId
            return this
        }

        override fun createState(): ProgressState {
            return ProgressState(this.requireId())
        }

        override fun apply(state: State) {
            super.apply(state)
            (state as ProgressState).apply {
                this.max = _max
            }
        }
    }

    override fun onView(view: View) {
        super.onView(view)
        this.progressView = view.findViewById(R.id.state_progress_view_progress)
        updateView()
    }
}