package com.ibashkimi.tris.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class Board(val array: IntArray = IntArray(9)) : Parcelable {

    val availableMoves: List<Int>
        get() = array.filterEmpty()

    @IgnoredOnParcel
    var movesCount: Int
        private set

    val isGameOver: Boolean
        get() = winningMoves != null || movesCount == 9

    @IgnoredOnParcel
    val state: State
        get() {
            return if (!isGameOver)
                State.Going
            else {
                if (winningSeed == null)
                    State.Ended.Draw
                else State.Ended.WithWinner(winningSeed!!, winningMoves!!)
            }
        }

    sealed class State {
        object Going : State()
        sealed class Ended : State() {
            object Draw : Ended()
            data class WithWinner(val seed: Int, val moves: List<Int>) : Ended()
        }
    }

    @IgnoredOnParcel
    var winningSeed: Int? = null

    @IgnoredOnParcel
    var winningMoves: List<Int>? = null

    init {
        movesCount = array.filterNotEmpty().size
        checkWinner()
    }

    fun occupy(position: Int, seed: Int) {
        when {
            isGameOver -> {
                throw Exception("Game is over. Cannot occupy cell. Reset board first.")
            }
            array[position] != EMPTY -> {
                throw Exception("Position $position is not free. board[$position] = ${array[position]}")
            }
            seed == EMPTY -> {
                throw Exception("Invalid playerSeed ${seed}. It's reserved for empty cells.")
            }
            else -> {
                array[position] = seed
                movesCount++

                checkWinner()

                if (winningMoves != null) {
                    winningSeed = seed
                } else if (movesCount == 9) {
                    winningSeed = null
                }
            }
        }
    }

    fun reset() {
        for (i in array.indices)
            array[i] = EMPTY
        winningSeed = null
        winningMoves = null
        movesCount = 0
    }

    private fun checkWinner() {
        var moves: ArrayList<Int>? = null
        for (line in lines) {
            if (array[line[0]] != EMPTY && array[line[0]] == array[line[1]] && array[line[1]] == array[line[2]]) {
                if (moves == null)
                    moves = ArrayList(6)
                moves.add(line[0])
                moves.add(line[1])
                moves.add(line[2])
                winningSeed = array[line[0]]
            }
        }
        winningMoves = moves
    }

    private fun IntArray.filterEmpty(): List<Int> = indices.filter { this[it] == EMPTY }

    private fun IntArray.filterNotEmpty(): List<Int> = indices.filter { this[it] != EMPTY }

    companion object {
        @Transient
        val lines = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        const val EMPTY = 0

        const val PLAYER_1 = 1

        const val PLAYER_2 = 2
    }
}