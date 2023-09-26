package com.ibashkimi.tris.model.player

import com.ibashkimi.tris.model.Board
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class HardPlayer(
    override val name: String,
    override val seed: Int,
    override var score: Long = 0,
    override val minDelayMillis: Long = DEFAULT_DELAY,
    private val chanceThreshold: Float = DEFAULT_CHANCE_THRESHOLD
) : AiPlayer(name, seed, score, minDelayMillis) {

    @IgnoredOnParcel
    private val normalPlayer = NormalPlayer(name, seed, score, minDelayMillis)

    @IgnoredOnParcel
    private val minimaxPlayer = MinimaxPlayer(name, seed, score, minDelayMillis)

    @IgnoredOnParcel
    private val random = Random()

    override fun makeMoveInternal(board: Board): Int {
        return if (tryChance())
            minimaxPlayer.makeMoveInternal(board)
        else
            normalPlayer.makeMoveInternal(board)
    }

    private fun tryChance(): Boolean = random.nextFloat() <= chanceThreshold

    companion object {
        const val DEFAULT_CHANCE_THRESHOLD = 0.6f
    }
}