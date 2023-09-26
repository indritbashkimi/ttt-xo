package com.ibashkimi.tris.model

import android.os.Parcelable
import com.ibashkimi.tris.model.player.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.parcelize.Parcelize

class Game(
    val board: Board,
    var players: Pair<Player, Player>,
    var turn: Int = 0,
    var stats: GameStats
) {

    val activePlayer: Player
        get() = if (turn == 0) players.first else players.second

    fun play(): Flow<Event> = flow {
        android.util.Log.d("Game", "play() called. Flow collecting...")
        emit(Event.GameCreated(this@Game))
        emit( // restore event if game is resumed
            when (val initState = board.state) {
                is Board.State.Ended -> {
                    when (initState) {
                        is Board.State.Ended.Draw -> {
                            Event.GameOver.Draw(this@Game)
                        }
                        is Board.State.Ended.WithWinner -> {
                            Event.GameOver.WithWinner(this@Game, activePlayer, initState.moves)
                        }
                    }
                }
                else -> Event.GameStarted(this@Game)
            }
        )
        while (!board.isGameOver) {
            activePlayer.makeMove(board).collect {
                board.occupy(it, activePlayer.seed)
                emit(Event.MoveMade(this@Game, activePlayer, it))
                when (val state = board.state) {
                    is Board.State.Going -> {
                        nextTurn()
                        emit(Event.TurnChanged(this@Game, activePlayer))
                    }
                    is Board.State.Ended -> {
                        stats.totalGames += 1
                        emit(
                            when (state) {
                                is Board.State.Ended.Draw -> {
                                    stats.drawGames += 1
                                    Event.GameOver.Draw(this@Game)
                                }
                                is Board.State.Ended.WithWinner -> {
                                    activePlayer.score += 1
                                    Event.GameOver.WithWinner(this@Game, activePlayer, state.moves)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun nextTurn() {
        turn = (turn + 1) % 2
    }

    sealed class Event(val game: Game) {
        class GameCreated(game: Game) : Event(game)
        class GameStarted(game: Game) : Event(game)
        class MoveMade(game: Game, val player: Player, val move: Int) : Event(game)
        class TurnChanged(game: Game, val player: Player) : Event(game)
        sealed class GameOver(game: Game) : Event(game) {
            class Draw(game: Game) : GameOver(game)
            class WithWinner(game: Game, val player: Player, val moves: List<Int>) : GameOver(game)
        }
    }

    @Parcelize
    data class GameStats(
        var xScore: Long = 0,
        var oScore: Long = 0,
        var totalGames: Long = 0,
        var drawGames: Long = 0
    ) : Parcelable
}