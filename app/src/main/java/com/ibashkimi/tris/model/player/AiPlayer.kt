package com.ibashkimi.tris.model.player

import android.os.SystemClock
import com.ibashkimi.tris.model.Board
import com.ibashkimi.tris.model.Board.Companion.PLAYER_1
import com.ibashkimi.tris.model.Board.Companion.PLAYER_2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


abstract class AiPlayer(
    override val name: String,
    override val seed: Int,
    override var score: Long = 0,
    open val minDelayMillis: Long = DEFAULT_DELAY
) : Player {

    val opponentSeed: Int
        get() = if (seed == PLAYER_1) PLAYER_2 else PLAYER_1

    internal abstract fun makeMoveInternal(board: Board): Int

    override fun makeMove(board: Board): Flow<Int> = flow {
        val requestTime = SystemClock.elapsedRealtime()
        val move = makeMoveInternal(board)
        val remainingTime = minDelayMillis - SystemClock.elapsedRealtime() + requestTime
        if (remainingTime > 0) {
            delay(remainingTime)
        }
        emit(move)
    }.flowOn(Dispatchers.Default)

    companion object {
        const val DEFAULT_DELAY: Long = 120
    }
}