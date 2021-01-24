package hu.moksony.statelayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import hu.moksony.statelayout.states.ContentState
import hu.moksony.statelayout.states.State

class StateLayout : FrameLayout {

    companion object {
        const val TAG = "StateLayout"
    }

    var showAnimation: Animation? = null
    var hideAnimation: Animation? = null


    private var contentState: ContentState? = null

    var currentState: State? = contentState
    var currentStateId: Int? = null


    var listener: StateLayoutEvents? = null

    interface StateLayoutEvents {
        fun onStateViewCreated(view: View, state: State)
        fun onStateViewActivated(state: State)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize(context, attrs)
    }


    fun initialize(ctx: Context, attrs: AttributeSet?) {
        val ta = ctx.obtainStyledAttributes(attrs, R.styleable.StateLayout)
        val modeIndex = ta.getInt(R.styleable.StateLayout_sl_mode, 0)
        this.currentStateId = ta.getResourceId(R.styleable.StateLayout_state, -1)
        ta.recycle()
    }

    fun updateVisibility(currentState: State?, nextState: State) {
        Log.d(
            TAG,
            "Update View [${currentState?.viewId}] ${currentState?.javaClass?.simpleName} -> [${nextState.viewId}] ${nextState.javaClass.simpleName}"
        )

        if (currentState != null && currentState != nextState) {
            if (!(nextState.allowOnContent && currentState is ContentState)) {
                hideState(currentState)
            }
        }

        nextState.view?.let { stateView ->
            stateView.visibility = View.VISIBLE

            showAnimation?.let { anim ->
                currentState?.view?.clearAnimation()
                anim.fillAfter = true
                stateView.animation = anim
                anim.start()
            }
        }
        this.currentState = nextState
        this.currentStateId = this.currentState?.viewId
    }


    fun setState(state: State) {
        var nextState: State? = null
        if (state is ContentState) {
            nextState = this.contentState
        } else if (currentState?.viewId != state.viewId) {
            nextState = state
        }

        if (nextState != null) {
            showState(nextState)
        }
    }

    private fun showState(state: State) {
        var stateView = state.view
        val viewId = state.viewId
        if (stateView == null) {
            Log.d(TAG, "View is null.")
            Log.d(TAG, "Create View by id")
            stateView = LayoutInflater.from(context).inflate(viewId, this, false)
            state.view = stateView
            if (stateView != null) {
                listener?.onStateViewCreated(stateView, state)
            }
        }

        if (stateView != null && stateView.parent == null) {
            addView(stateView)
        }
        updateVisibility(currentState, state)
        listener?.onStateViewActivated(state)
    }

    fun hideState(state: State) {
        if (state.allowOnContent) {
            Log.d(TAG, "Skip hide content view")
        } else {
            state.view?.let { it.visibility = View.GONE }
        }
    }


    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child != null) {
            var cs = this.contentState
            if (cs == null) {
                val id = child.id
                if (id <= 0) {
                    child.id = R.id.state_content
                }
                cs = ContentState(child.id)
                cs.view = child
                this.contentState = cs
                this.currentState = cs
            }
        }
    }
}