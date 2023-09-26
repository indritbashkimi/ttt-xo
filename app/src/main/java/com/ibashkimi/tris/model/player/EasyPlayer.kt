package com.ibashkimi.tris.model.player

import com.ibashkimi.tris.model.Board
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class EasyPlayer(
    override val name: String,
    override val seed: Int,
    override var score: Long = 0,
    override val minDelayMillis: Long = DEFAULT_DELAY
) : AiPlayer(name, seed, score, minDelayMillis) {

    override fun makeMoveInternal(board: Board): Int {
        var move: Int
        if (board.movesCount < 3)
            move = randomMove(board.availableMoves)
        else {
            move = getInevitableMove(board.array)
            if (move == -1 || !tryChance()) {
                move = randomMove(board.availableMoves)
            }
        }
        return move
    }

    @IgnoredOnParcel
    private val stats = LineStats(seed, opponentSeed)

    @IgnoredOnParcel
    private val winningMoves = ArrayList<Int>(2)

    @IgnoredOnParcel
    private val chanceThreshold: Float = 0.8f

    private fun getInevitableMove(board: IntArray): Int {
        winningMoves.clear()
        for (line in Board.lines) {
            stats.calculate(board, line)
            if (stats.isFull()) {
                continue
            }
            if (stats.canPlayerWin()) {
                winningMoves.add(stats.emptyBox)
            }
        }
        if (winningMoves.size > 0) {
            return winningMoves[random.nextInt(winningMoves.size)]
        }
        return -1
    }

    private fun tryChance(): Boolean = random.nextFloat() <= chanceThreshold

    @IgnoredOnParcel
    private val random = Random(System.currentTimeMillis())

    private fun randomMove(moves: List<Int>) = moves[random.nextInt(moves.size)]
}