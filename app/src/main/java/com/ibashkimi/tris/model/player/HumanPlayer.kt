package com.ibashkimi.tris.model.player

import com.ibashkimi.tris.model.Board
import com.ibashkimi.tris.model.ButtonFieldChannel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take

@Parcelize
data class HumanPlayer(
    override val name: String,
    override val seed: Int,
    override var score: Long = 0
) : Player {

    override fun makeMove(board: Board): Flow<Int> = ButtonFieldChannel.clicks().take(1)

}
