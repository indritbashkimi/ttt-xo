package com.ibashkimi.tris.game

import android.os.Bundle
import com.ibashkimi.tris.model.Board
import com.ibashkimi.tris.model.Game
import com.ibashkimi.tris.model.player.*

object GameSessionManager {

    fun saveGame(game: Game, state: Bundle) {
        state.apply {
            putParcelable("board", game.board)

            putString("player1_type", game.players.first::class.java.simpleName)
            putParcelable("player1", game.players.first)
            putString("player2_type", game.players.second::class.java.simpleName)
            putParcelable("player2", game.players.second)

            putInt("turn", game.turn)

            putParcelable("stats", game.stats)
        }
    }

    fun restoreGame(state: Bundle): Game? {
        val board: Board = state.getParcelable("board")
            ?: throw IllegalStateException("Cannot restore board.")

        val player1 = createPlayer(state.getString("player1_type")!!, state, "player1")
        val player2 = createPlayer(state.getString("player2_type")!!, state, "player2")

        val turn: Int = state.getInt("turn")

        val stats: Game.GameStats = state.getParcelable("stats")
            ?: throw IllegalStateException("Cannot restore stats.")

        return Game(board, Pair(player1, player2), turn, stats)
    }

    private fun createPlayer(type: String, state: Bundle, key: String): Player {
        return when (type) {
            EasyPlayer::class.java.simpleName -> state.getParcelable<EasyPlayer>(key)
            HardPlayer::class.java.simpleName -> state.getParcelable<HardPlayer>(key)
            HumanPlayer::class.java.simpleName -> state.getParcelable<HumanPlayer>(key)
            MinimaxPlayer::class.java.simpleName -> state.getParcelable<MinimaxPlayer>(key)
            NormalPlayer::class.java.simpleName -> state.getParcelable<NormalPlayer>(key)
            else -> throw IllegalArgumentException("Unknown player type!")
        } ?: throw IllegalStateException("")
    }
}
