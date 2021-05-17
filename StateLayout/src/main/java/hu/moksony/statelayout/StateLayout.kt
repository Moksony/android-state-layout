package hu.moksony.statelayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.view.children
import hu.moksony.statelayout.states.ContentState
import hu.moksony.statelayout.states.State

class StateLayout : FrameLayout {

    companion object {
        const val TAG = "StateLayout"
    }

    var showAnimation: Animation? = null
    var hideAnimation: Animation? = null

    //map to LayoutId -> ViewId
    val layoutToIdMap = mutableMapOf<Int, Int>()

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
            val showAsOverlay = !(nextState.allowOnContent && currentState is ContentState)
            if (showAsOverlay || nextState is ContentState) {
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

    fun showContentState() {
        val cState = this.contentState
        if (cState != null) {
            this.setState(cState)
        }
    }

    fun setState(state: State) {
        var nextState: State? = null
        if (state is ContentState) {
            nextState = this.contentState
        } else if ((currentState?.viewId != state.viewId) || currentState?.layoutId != state.layoutId) {
            nextState = state
        } else {
            Log.d(TAG, "setState: State is same")
        }

        if (nextState != null) {
            showState(nextState)
        }
    }

    private fun findChild(@IdRes viewId: Int): View? {
        if (viewId != View.NO_ID) {
            return children.find { it.id == viewId }
        }
        return null
    }

    private fun inflate(@LayoutRes layoutId: Int): View? {
        Log.d(TAG, "inflate: $layoutId")
        val viewId = layoutToIdMap[layoutId] ?: View.NO_ID
        var view: View? = null
        if (viewId != View.NO_ID) {
            Log.d(TAG, "inflate: Found view in mapping")
            view = findChild(viewId)
        }
        if (view == null) {
            Log.d(TAG, "inflate: create from layoutId")
            view = LayoutInflater.from(context).inflate(layoutId, this, false)
            if (view.id > 0) {
                layoutToIdMap[layoutId] = view.id
            }
        }
        return view
    }

    private fun showState(state: State) {
        var stateView = state.view
        val viewId = state.viewId
        val layoutIdRes = state.layoutId
        if (stateView == null) {
            Log.d(TAG, "View is null.")
            stateView = findChild(viewId)

            val stillNull = stateView == null
            if (stillNull && layoutIdRes != null) {
                stateView = inflate(layoutIdRes)
            }
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
        state.view?.let { it.visibility = View.GONE }
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