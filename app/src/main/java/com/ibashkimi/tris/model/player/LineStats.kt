package com.ibashkimi.tris.model.player

import com.ibashkimi.tris.model.Board


class LineStats(private val playerSeed: Int, private val opponentSeed: Int) {

    var emptyBox: Int = -1

    var playerCount: Int = 0

    var opponentCount: Int = 0

    fun calculate(board: IntArray, line: IntArray) {
        reset()
        for (i in line) {
            when {
                board[i] == Board.EMPTY -> emptyBox = i
                board[i] == opponentSeed -> opponentCount++
                board[i] == playerSeed -> playerCount++
            }
        }
    }

    // Line if full
    fun isFull(): Boolean = playerCount + opponentCount == 3 // or emptyBox != -1

    // Player wins with this move
    fun canPlayerWin(): Boolean = playerCount == 2 && emptyBox != -1

    // Opponent can win, must be stopped!
    fun canOpponentWin(): Boolean = opponentCount == 2 && emptyBox != -1

    private fun reset() {
        emptyBox = -1
        playerCount = 0
        opponentCount = 0
    }
}