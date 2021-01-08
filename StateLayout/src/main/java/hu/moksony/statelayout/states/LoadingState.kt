package hu.moksony.statelayout.states

import hu.moksony.statelayout.R

class LoadingState(stateId: Int) : State(stateId) {
    class Builder(): State.Builder<LoadingState>(){
        override fun createState(): LoadingState {
            return LoadingState(requireId())
        }
    }
}