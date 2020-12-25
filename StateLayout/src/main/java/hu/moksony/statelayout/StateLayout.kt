package hu.moksony.statelayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children

class StateLayout : FrameLayout {

    enum class LayoutMode {
        SINGLE_LAYER,
        OVERLAY_ON_CONTENT,
    }

    var mode = LayoutMode.SINGLE_LAYER
    private val contentState = ContentState()

    var currentState: State = contentState
    var currentStateId: Int = -1

    private val states = HashMap<Int, State>()

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
        this.mode = LayoutMode.values()[modeIndex]
        ta.recycle()
    }

    fun updateVisibility(currentState: State, nextState: State) {
        Log.d("StateLayout", "Update View ${currentState.viewId} -> ${nextState.viewId}")
        if (currentState != nextState) {
            hideState(currentState)
        }
        nextState.view?.apply { visibility = View.VISIBLE }
        this.currentState = nextState
    }


    fun setState(state: State) {
        if (currentState != state) {
            showState(state)
        }
    }

    private fun showState(state: State) {
        var view = state.view
        val viewId = state.viewId
        if (view == null) {
            Log.d("StateLayout", "View is null.")
            if (viewId != null) {
                Log.d("StateLayout", "Create View by id")
                view = inflate(context, viewId, this)
                state.view = view
                if (view != null) {
                    listener?.onStateViewCreated(view, state)
                }
            } else {
                Log.d("StateLayout", "Create View by State::createView function")
                view = state.createView(this, context)
                if (view != null) {
                    state.view = view
                    listener?.onStateViewCreated(view, state)
                    if (view.parent == null) {
                        addView(view)
                    }
                }
            }
        }
        updateVisibility(currentState, state)
        listener?.onStateViewActivated(state)
    }

    fun setState(state: Int) {
        val s = this.states[state] ?: throw Exception("State not found $state")
        setState(s)
    }

    fun hideState(state: State) {
        if (this.mode == LayoutMode.OVERLAY_ON_CONTENT && state.viewId == contentState.viewId) {

        } else {
            state.view?.let { it.visibility = View.GONE }
        }
    }

    fun addState(state: State, stateName: Int) {
        if (!states.containsKey(stateName)) {
            this.states[stateName] = state
            Log.d("StateLayout", "$stateName added to list.")
            if (stateName == this.currentStateId) { //we are waiting this view....
                updateVisibility(this.currentState, state)
            } else if (this.currentState != state) { //just hide view
                hideState(state)
            }
        } else {
            Log.d("StateLayout", "State($stateName) already added.")
        }
    }

    private fun addState(view: View, stateName: Int = view.id) {
        val state = states[stateName] ?: State().apply {
            this.view = view
        }
        addState(state, stateName)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child != null) {
            if (this.contentState.view == null) {
                this.contentState.view = child
                this.contentState.viewId = child.id
            }
            addState(child)
        }
    }
}