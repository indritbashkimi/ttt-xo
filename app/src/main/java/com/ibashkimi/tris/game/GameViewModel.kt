package com.ibashkimi.tris.game

import androidx.lifecycle.*
import com.ibashkimi.tris.model.ButtonFieldChannel
import com.ibashkimi.tris.model.Game

class GameViewModel : ViewModel() {

    val game = MutableLiveData<Game>()

    val gameEvents: LiveData<Game.Event> = Transformations.switchMap(game) {
        ButtonFieldChannel.unregisterAll()
        it.play().asLiveData()
    }
}