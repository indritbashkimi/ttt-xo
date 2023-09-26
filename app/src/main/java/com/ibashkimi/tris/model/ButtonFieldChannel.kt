package com.ibashkimi.tris.model

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object ButtonFieldChannel {

    private val observers = ArrayList<ButtonFieldObserver>()

    fun clicks(): Flow<Int> = callbackFlow {
        val observer = ButtonFieldObserver { trySend(it) }
        register(observer)
        awaitClose { unregister(observer) }
    }

    fun register(observer: ButtonFieldObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    fun unregister(observer: ButtonFieldObserver) {
        observers.remove(observer)
    }

    fun unregisterAll() = observers.clear()

    fun onButtonPressed(button: Int) {
        observers.forEach { it.boardClicked(button) }
    }
}