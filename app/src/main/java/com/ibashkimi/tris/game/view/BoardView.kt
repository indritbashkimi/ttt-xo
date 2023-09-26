package com.ibashkimi.tris.game.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.ibashkimi.tris.databinding.BoardViewBinding
import com.ibashkimi.tris.model.Board
import com.ibashkimi.tris.model.ButtonFieldObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val observers = ArrayList<ButtonFieldObserver>()

    private val symbols = arrayOf(" ", "X", "O")
    private val boxes: List<TextView>
    private val normalColor: Int
    private val negativeHighlightColor: Int

    init {
        BoardViewBinding.inflate(LayoutInflater.from(context), this).apply {
            boxes = listOf(box1, box2, box3, box4, box5, box6, box7, box8, box9)
        }
        normalColor = getColorFromAttribute(context, android.R.attr.textColorPrimary)
        negativeHighlightColor = ColorUtils.setAlphaComponent(normalColor, 100)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        boxes.forEach { it.setOnClickListener(this) }
    }

    fun clicks(): Flow<Int> = callbackFlow {
        val observer = ButtonFieldObserver {
            trySend(it)
        }
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
        awaitClose { observers.remove(observer) }
    }

    fun disable() {
        for (box in boxes)
            box.isClickable = false
    }

    fun reset(array: IntArray = IntArray(9)) {
        for (i in boxes.indices) {
            boxes[i].setTextColor(normalColor)
            boxes[i].text = symbols[array[i]]
            boxes[i].isClickable = array[i] == 0
        }
    }

    fun highlight(boxSlots: List<Int>) {
        boxes.indices.filter { boxSlots.contains(it) }
            .forEach { boxes[it].setTextColor(negativeHighlightColor) }
    }

    fun setOwner(boxIndex: Int, owner: Int) {
        boxes[boxIndex].apply {
            text = symbols[when (owner) {
                Board.EMPTY -> 0
                Board.PLAYER_1 -> 1
                Board.PLAYER_2 -> 2
                else -> throw Exception("Invalid box owner.")
            }]
            isClickable = false
        }
    }

    override fun onClick(v: View) {
        observers.forEach { it.boardClicked(boxes.indexOf(v)) }
    }

    companion object {

        @ColorInt
        fun getColorFromAttribute(context: Context, attr: Int): Int {
            val typedValue = TypedValue()
            val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr))
            val color = a.getColor(0, 0)
            a.recycle()
            return color
        }
    }
}
