package com.ibashkimi.tris.model.player

import android.os.Parcelable
import com.ibashkimi.tris.model.Board
import kotlinx.coroutines.flow.Flow


interface Player : Parcelable {

    val name: String

    val seed: Int

    var score: Long

    fun makeMove(board: Board): Flow<Int>
}
