package hu.moksony.statelayout.states

import androidx.annotation.StringRes

class EmptyState(viewId: Int) : State(viewId) {

    class Builder : State.Builder<EmptyState>() {
        override fun createState(): EmptyState {
            return EmptyState(requireId())
        }

        override fun apply(state: State) {
            super.apply(state)
            (state as EmptyState)
        }
    }
}