package com.ibashkimi.tris.model.player

import com.ibashkimi.tris.model.Board
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class NormalPlayer(
    override val name: String,
    override val seed: Int,
    override var score: Long = 0,
    override val minDelayMillis: Long = DEFAULT_DELAY
) : AiPlayer(name, seed, score, minDelayMillis) {

    @IgnoredOnParcel
    private val random = Random()

    @IgnoredOnParcel
    private val stats = LineStats(seed, opponentSeed)

    @IgnoredOnParcel
    private val possibleMoves = ArrayList<Int>(2)

    override fun makeMoveInternal(board: Board): Int {
        var move: Int
        if (board.movesCount < 3)
            move = randomMove(board.availableMoves)
        else {
            move = getInevitableMove(board.array)
            if (move == -1)
                move = randomMove(board.availableMoves)
        }
        return move
    }

    private fun getInevitableMove(board: IntArray): Int {
        possibleMoves.clear()
        for (line in Board.lines) {
            stats.calculate(board, line)
            if (stats.isFull()) {
                continue
            }
            if (stats.canPlayerWin()) {
                return stats.emptyBox
            }
            if (stats.canOpponentWin()) {
                possibleMoves.add(stats.emptyBox)
            }
        }
        if (possibleMoves.size > 0) {
            return possibleMoves[random.nextInt(possibleMoves.size)]
        }
        return -1
    }

    private fun randomMove(moves: List<Int>) = moves[random.nextInt(moves.size)]
}