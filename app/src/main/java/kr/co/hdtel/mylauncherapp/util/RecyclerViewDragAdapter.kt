/*
 * Copyright (c) 2016-2023 HYUNDAI HT Co., Ltd. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * HYUNDAI HT ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with HYUNDAI HT.
 */

package kr.co.hdtel.mylauncherapp.util

import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class RecyclerViewDragAdapter<T, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, VH>(diffUtil) {
    abstract val isSwappable: Boolean

    private var globalX = 0f
    private var globalY = 0f
//    private var adjustedX = 0f
//    private var adjustedY = 0f

    val dragListener = View.OnDragListener { view, event ->
        event?.let {

            val originView = it.localState as View
            val originRecyclerView = originView.parent as RecyclerView?
            originRecyclerView?.let { originRV ->
                val originAdapter = originRV.adapter as RecyclerViewDragAdapter<T, VH>
                val originPosition = originRV.getChildAdapterPosition(originView)

                var targetRecyclerView = view as? RecyclerView
                targetRecyclerView =
                    targetRecyclerView.takeIf { false } ?: view.parent as? RecyclerView

                if (targetRecyclerView !is RecyclerView) {
                    return@OnDragListener false
                }

                val sameRecyclerView = originRV.id == targetRecyclerView.id
                val targetAdapter = targetRecyclerView.adapter as RecyclerViewDragAdapter<T, VH>
                val targetPosition = if (view is RecyclerView) {
                    targetAdapter.currentList.size
                } else {
                    targetRecyclerView.getChildAdapterPosition(view)
                }

                when (it.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        originView.setDefaultViewColor(MIN)
                    }

                    DragEvent.ACTION_DRAG_LOCATION -> {
                        Log.d("sss", "ACTION_DRAG_LOCATION")
                        val location = IntArray(2)
                        view.getLocationOnScreen(location)
                        globalX = location[0] + event.x
                        globalY = location[1] + event.y
                    }

                    DragEvent.ACTION_DRAG_ENTERED -> {
                        Log.d("sss", "ACTION_DRAG_ENTERED")
                        if (!isSwappable || targetPosition < 0) {
                            return@OnDragListener false
                        }

                        if (sameRecyclerView) {
                            onSwapAnimation(originPosition, targetPosition)
                        } else {
                            onSetAnimation(
                                targetAdapter.currentList,
                                originAdapter.currentList,
                                originAdapter.currentList[originPosition],
                                originPosition,
                                targetPosition
                            )

                            targetRecyclerView.hideItemIfTagMatches(
                                targetPosition, originAdapter.currentList[originPosition]
                            )
                        }
                    }

                    DragEvent.ACTION_DROP -> {
                        if (!isSwappable) {
                            return@OnDragListener false
                        }

                        if (sameRecyclerView) {
                            if (targetPosition >= 0) {
                                val swpList = onSwapAnimation(originPosition, targetPosition)
                                onSwap(swpList)
                            } else {
                                return@OnDragListener false
                            }
                        } else {
                            val setList = onSetAnimation(
                                targetAdapter.currentList,
                                originAdapter.currentList,
                                originAdapter.currentList[originPosition],
                                originPosition,
                                targetPosition
                            )

                            val tempList = originAdapter.currentList.toMutableList()
                            tempList.removeAt(originPosition)
                            originAdapter.submitList(tempList)

                            onSet(
                                setList.first,
                                tempList,
                                originAdapter.currentList[originPosition],
                                originPosition,
                                targetPosition
                            )

                            val viewHolder = targetRecyclerView.findViewHolderForAdapterPosition(
                                targetPosition
                            )
                            if (viewHolder?.itemView?.tag == originAdapter.currentList[originPosition]) {
                                viewHolder?.itemView?.visibility = View.VISIBLE
                            }

                            targetRecyclerView.showItemIfTagMatches(
                                targetPosition, originAdapter.currentList[originPosition]
                            )
                        }
                        return@OnDragListener true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        Log.d
                        originView.setDefaultViewColor(MAX)
                        return@OnDragListener true
                    }

                    DragEvent.ACTION_DRAG_EXITED -> {
                        Log.d("sss", "ACTION_DRAG_EXITED")
                        if (targetRecyclerView.checkIsOutSide(globalX, globalY) && isSwappable) {
                            Log.d("ata", "is not recyclerview")
                            val tempList = targetAdapter.currentList.toMutableList()
                            tempList.remove(originAdapter.currentList[originPosition])
                            targetAdapter.submitList(tempList)
                        }
                        return@OnDragListener true
                    }

                    else -> {
                        Log.d("sss", "else...")
                        return@OnDragListener false
                    }
                }
            }
        }
        true
    }

    private fun RecyclerView.updateItemVisibility(position: Int, tag: Any?, visible: Boolean) {
        val viewHolder = findViewHolderForAdapterPosition(position)
        viewHolder?.itemView?.visibility = if (viewHolder?.itemView?.tag == tag) {
            if (visible) View.VISIBLE else View.INVISIBLE
        } else {
            if (visible) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun RecyclerView.hideItemIfTagMatches(position: Int, tag: Any?) {
        updateItemVisibility(position, tag, visible = false)
    }

    private fun RecyclerView.showItemIfTagMatches(position: Int, tag: Any?) {
        updateItemVisibility(position, tag, visible = true)
    }

    private fun RecyclerView.checkIsOutSide(x: Float, y: Float): Boolean {
        getLocationOnScreen(IntArray(2))
        val right = IntArray(2)[0] + width
        val bottom = IntArray(2)[1] + height
        return x < left || x > right || y < top || y > bottom
    }

    /**
     * set ( difference recyclerview )
     * */
    private fun onSetAnimation(
        targetList: List<T>,
        originList: List<T>,
        originItem: T,
        from: Int,
        to: Int
    ): Pair<List<T>, List<T>> {
        return when (dragType()) {
            DragType.ONEBYONE -> {
                setOneByOne(targetList, originList, originItem, from, to).run {
                    first to second
                }
            }

            DragType.SHIFT -> {
                setShift(targetList, originList, originItem, to).run {
                    submitList(first)
                    first to second
                }
            }
        }
    }

    private fun setOneByOne(
        targetList: List<T>,
        originList: List<T>,
        originItem: T,
        from: Int,
        to: Int
    ): Pair<List<T>, List<T>> {
        val target = targetList.toMutableList().apply {
            add(to, originItem)
        }

        val origin = originList.toMutableList().apply {
            removeAt(from)
        }

        return Pair(target, origin)
    }

    /**
     * origin ITem -> target[to]. add
     * target[to] view alpha -> 0f
     *
     * */
    private fun setShift(
        targetList: List<T>,
        originList: List<T>,
        originItem: T,
        to: Int
    ): Pair<MutableList<T>, MutableList<T>> {
        val tempList = targetList.toMutableList()
        val bottomList = originList.toMutableList()

        if (!targetList.contains(originItem)) {
            tempList.add(to, originItem)
        } else {
            if (targetList.indexOf(originItem) < to) {
                for (i in targetList.indexOf(originItem) until to) {
                    Collections.swap(tempList, i, i + 1)

                }
            } else {
                for (i in targetList.indexOf(originItem) downTo to + 1) {
                    Collections.swap(tempList, i, i - 1)
                }
            }
        }
        return tempList to bottomList
    }

    /**
     * swap  (same recyclerview)
     * */
    private fun onSwapAnimation(from: Int, to: Int): List<T> {
        val swapItemList = currentList.toMutableList()
        when (dragType()) {
            DragType.ONEBYONE -> {
                swapItemList.swapOneByOne(from, to)
            }
            DragType.SHIFT -> {
                swapItemList.apply {
                    swapShift(from, to)
                    submitList(this)
                }
            }
        }
        return swapItemList
    }

    private fun List<T>.swapOneByOne(from: Int, to: Int) {
        if (from > currentList.size - 1 || to > currentList.size - 1) {
            return
        }

        return Collections.swap(this, from, to)
    }

    private fun MutableList<T>.swapShift(from: Int, to: Int) {
        return if (from < to) {
            for (i in from until to) {
                Collections.swap(this, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(this, i, i - 1)
            }
        }
    }

    private fun View.setDefaultViewColor(alpha: Float) {
        this.alpha = when (dragType()) {
            DragType.SHIFT -> {
                alpha
            }

            DragType.ONEBYONE -> {
                MAX
            }
        }
    }

    abstract fun dragType(): DragType
    abstract fun onAdd(item: T)
    abstract fun onRemove(item: T)
    abstract fun onSet(
        targetList: List<T>,
        originList: List<T>,
        originItem: T,
        from: Int,
        to: Int
    )

    abstract fun onSwap(list: List<T>)

    companion object {
        const val MAX = 1f
        const val MIN = 0f
    }
}