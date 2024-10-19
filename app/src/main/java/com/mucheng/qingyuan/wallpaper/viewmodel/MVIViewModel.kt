package com.mucheng.qingyuan.wallpaper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

interface UIState

interface UIIntent

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
abstract class MVIViewModel<S : UIState, I : UIIntent> : ViewModel() {

    protected val _state: MutableStateFlow<S> = MutableStateFlow(initialState)

    val state: StateFlow<S> = _state.asStateFlow()

    protected val _intent = Channel<I>(Channel.UNLIMITED)

    val intent: Channel<I> = _intent

    protected abstract val initialState: S

    init {
        viewModelScope.launch {
            _intent.consumeAsFlow().collect {
                it.onReceiveIntent()
            }
        }
    }

    protected abstract suspend fun I.onReceiveIntent()

}