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
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kr.co.hdtel.mylauncherapp.R
import java.util.*
import kotlin.collections.HashMap

abstract class RecyclerViewDragAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, VH>(diffUtil) {
    abstract val isSwappable: Boolean
    abstract val itemViews : HashMap<Int, ViewHolder>

    val dragListener = View.OnDragListener { view, event ->
        event?.let {
            val originView = it.localState as View
            val originRecyclerView = originView.parent as RecyclerView?
            originRecyclerView?.let { rv ->
                val originAdapter = rv.adapter as RecyclerViewDragAdapter<T, VH>
                val originPosition = rv.getChildAdapterPosition(originView)

                var targetRecyclerView = view as? RecyclerView
                targetRecyclerView =
                    targetRecyclerView.takeIf { false } ?: view.parent as? RecyclerView

                val sameRecyclerView = rv.id == targetRecyclerView?.id

                if (targetRecyclerView !is RecyclerView) {
                    return@OnDragListener false
                }

                val targetAdapter = targetRecyclerView.adapter as RecyclerViewDragAdapter<T, VH>
                val targetPosition = if (view is RecyclerView) {
                    targetAdapter.currentList.size
                } else {
                    targetRecyclerView.getChildAdapterPosition(view)
                }

                when (it.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        setDefaultViewColor(originView, MIN)
                    }

                    DragEvent.ACTION_DRAG_ENTERED -> {
                        Log.d("sss", "ACTION_DRAG_ENTERED")
                        if (sameRecyclerView) {
                            if (!isSwappable) {
                                return@OnDragListener false
                            }

                            val swpList = onSwapItem(originPosition, targetPosition)
                            if (targetPosition >= 0) {
                                onSwap(false, swpList)
                            } else {
                                return@OnDragListener false
                            }
                        } else {
                            targetAdapter.currentList[targetPosition]?.let {
                                    val setList = onSetItem(
                                        targetAdapter.currentList,
                                        originAdapter.currentList,
                                        originAdapter.currentList[originPosition],
                                        originPosition,
                                        targetPosition
                                    )


                                onSetTest(
                                        false,
                                        setList.first,
                                        originAdapter.currentList[originPosition],
                                        originPosition,
                                        targetPosition
                                    )

                                itemViews[targetPosition]?.itemView?.alpha = MIN
                                originAdapter.submitList(setList.second)
                            } ?: run {
                                targetAdapter.onAdd(originAdapter.currentList[originPosition])
                            }
                        }
                    }

                    DragEvent.ACTION_DROP -> {
                        if (sameRecyclerView) {
                            if (isSwappable && targetPosition >= 0) {
                                val swpList = onSwapItem(originPosition, targetPosition)
                                originAdapter.onSwap(true, swpList)
                            } else {
                                return@OnDragListener false
                            }
                        } else {
                            targetAdapter.currentList[targetPosition]?.let {
                                val setList = onSetItem(
                                    targetAdapter.currentList,
                                    originAdapter.currentList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition
                                )

                                onSetTest(
                                    true,
                                    setList.first,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition
                                )
                                originAdapter.submitList(setList.second)
                            } ?: run {
                                targetAdapter.onAdd(originAdapter.currentList[originPosition])
                            }
                        }
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        setDefaultViewColor(originView, MAX)
                    }

                    DragEvent.ACTION_DRAG_EXITED -> {
                        Log.d("sss","ACTION_DRAG_EXITED")
                        return@OnDragListener true
                    }

                    else -> {}
                }
            }

        }
        true
    }

    /**
     * set
     * */
    private fun onSetItem(targetList: List<T>, originList: List<T>, originItem: T, from: Int, to: Int): Pair<List<T>,List<T>> {
        return when (dragType()) {
            DragType.ONEBYONE -> {
                val (top,bottom) = setOneByOne(targetList, originList, originItem, from, to).apply {
                    this.first to this.second
                }
                top to bottom
            }

            DragType.SHIFT -> {
                val temp = shiftSetItem(targetList, originList, originItem, from, to)
                submitList(temp.first)
                temp.first to temp.second
            }
        }
    }

    private fun setOneByOne(targetList: List<T>, originList: List<T>, originItem: T, from: Int, to: Int): Pair<List<T>,List<T>> {
        val target = targetList.toMutableList()
        target.add(to, originItem)

        val origin = originList.toMutableList()
        origin.removeAt(from)

        return Pair(target,origin)
    }


    /**
     * origin ITem -> target[to]. add
     * target[to] view alpha -> 0f
     *
     * */
    private fun shiftSetItem(
        targetList: List<T>,
        originList: List<T>,
        originItem: T,
        from: Int,
        to: Int
    ): Pair<MutableList<T>, MutableList<T>> {

        val tempList = targetList.toMutableList()
        val bottomList = originList.toMutableList()
        if (!targetList.contains(originItem)) {
            tempList.add(to, originItem)
//            bottomList.remove(originItem)
        } else {
            if (targetList.indexOf(originItem) < to) {
                for (i in targetList.indexOf(originItem) until to) {
                    Collections.swap(tempList, i, i + 1)
                }
            } else {
                for (i in targetList.indexOf(originItem) downTo  to + 1) {
                    Collections.swap(tempList, i, i - 1)
                }
            }
        }
        Log.d("sss","tempList:${tempList}")
        return tempList to bottomList
    }

    /**
     * swap
     * */
    private fun onSwapItem(from: Int, to: Int): List<T> {
        val swapItemList = currentList.toMutableList()
        when (dragType()) {
            DragType.ONEBYONE -> {
                swapOneByOne(swapItemList, from, to)
            }
            DragType.SHIFT -> {
                shiftSwapItem(swapItemList, from, to)
                submitList(swapItemList)
            }
        }
        return swapItemList
    }

    private fun swapOneByOne(list: List<T>, from: Int, to: Int) {
        if (from > currentList.size - 1 || to > currentList.size - 1) {
            return
        }

        return Collections.swap(list, from, to)
    }

    private fun shiftSwapItem(list: MutableList<T?>, from: Int, to: Int) {
        return if (from < to) {
            for (i in from until to) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
    }

    private fun setDefaultViewColor(view: View, value: Float) {
        when (originViewType()) {
            ViewType.TRANSPARENT -> {
                view.alpha = value
            }
            ViewType.CLEARLY -> {
                return
            }
        }
    }

    abstract fun dragType(): DragType

    abstract fun originViewType(): ViewType

    abstract fun onAdd(item: T)

    abstract fun onRemove(item: T)

    abstract fun onSet(isDrop: Boolean, from: Int, to: Int, item: T)

    abstract fun onSetTest(isDrop: Boolean, targetList: List<T>, originItem: T, from: Int, to: Int)

    abstract fun onSwap(isDrop: Boolean, from: Int, to: Int)

    abstract fun onSwap(isDrop: Boolean, list: List<T>)

    companion object {
        const val MAX = 1f
        const val MIN = 0f
    }
}