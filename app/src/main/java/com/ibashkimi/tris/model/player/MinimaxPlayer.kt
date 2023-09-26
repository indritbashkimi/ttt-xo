package com.ibashkimi.tris.model.player

import com.ibashkimi.tris.model.Board
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MinimaxPlayer(
    override val name: String,
    override val seed: Int,
    override var score: Long = 0,
    override val minDelayMillis: Long = DEFAULT_DELAY
) : AiPlayer(name, seed, score, minDelayMillis) {

    override fun makeMoveInternal(board: Board): Int {
        var move: Int = -1
        if (board.movesCount == 0) {
            move = Random().nextInt(9)
        } else if (board.movesCount == 1) {
            for (i in board.array.indices) {
                if (board.array[i] == opponentSeed) {
                    move = when (i) {
                        0 -> 4
                        1 -> randomMove(0, 2, 7)
                        2 -> 4
                        3 -> randomMove(0, 6, 5)
                        4 -> randomMove(0, 2, 6, 8)
                        5 -> randomMove(2, 8, 3)
                        6 -> 4
                        7 -> randomMove(6, 8, 1)
                        8 -> 4
                        else -> -1
                    }
                    break
                }
            }
        } else {
            // Pure minimax
            var score = -2
            val array = board.array
            for (i in 0..8) {
                if (array[i] == 0) {
                    array[i] = seed
                    val tempScore = -minimax(array, opponentSeed)
                    array[i] = 0
                    if (tempScore > score) {
                        score = tempScore
                        move = i
                    }
                }
            }
            // returns a score based on minimax tree at a given node.
        }
        return move
    }

    private fun win(board: IntArray): Int {
        //determines if a player has won, returns 0 otherwise.
        val wins = listOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )
        for (i in 0..7) {
            if (board[wins[i][0]] != 0 &&
                board[wins[i][0]] == board[wins[i][1]] &&
                board[wins[i][0]] == board[wins[i][2]]
            )
                return board[wins[i][2]]
        }
        return 0
    }

    private fun minimax(board: IntArray, player: Int): Int {
        // How is the position like for player (their player) on board?
        val winner = win(board)
        if (winner != 0) return if (winner != player) -1 else 1//winner * player

        var move = -1
        var score = -2 // Losing moves are preferred to no move
        for (i in 0..8) {
            if (board[i] == Board.EMPTY) { //If legal,
                board[i] = player // Try the move
                val thisScore = -minimax(board, (player % 2) + 1)
                if (thisScore > score) {
                    score = thisScore
                    move = i
                } // Pick the one that's worst for the opponent
                board[i] = Board.EMPTY // Reset board after try
            }
        }

        if (move == -1) return 0
        return score
    }

    private fun randomMove(vararg moves: Int) = moves[Random().nextInt(moves.size)]
}
