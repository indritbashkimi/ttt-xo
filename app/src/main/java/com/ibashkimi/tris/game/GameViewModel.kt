package com.ibashkimi.tris.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.ibashkimi.tris.model.ButtonFieldChannel
import com.ibashkimi.tris.model.Game

class GameViewModel : ViewModel() {

    val game = MutableLiveData<Game>()

    val gameEvents: LiveData<Game.Event> = game.switchMap {
        ButtonFieldChannel.unregisterAll()
        it.play().asLiveData()
    }
}