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
    private var previousList = mutableListOf<T>()

    val dragListener = View.OnDragListener { view, event ->
        event?.let {
            val originView = it.localState as View
            val originRecyclerView = originView.parent as RecyclerView?
            originRecyclerView?.let { rv ->
                val originAdapter = rv.adapter as RecyclerViewDragAdapter<T, VH>
                val originPosition = rv.getChildAdapterPosition(originView)

                var targetRecyclerView = view as? RecyclerView
                targetRecyclerView = targetRecyclerView.takeIf { false } ?: view.parent as? RecyclerView

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
                            if (!isSwappable || targetPosition < 0) {
                                return@OnDragListener false
                            }

                            onSwapAnimation(originPosition, targetPosition)

                        } else {
                            if (!isSwappable) {
                                return@OnDragListener false
                            }

                            targetAdapter.currentList[targetPosition]?.let {
                                onSetAnimation(
                                    originAdapter,
                                    targetAdapter,
                                    targetRecyclerView,
                                    targetAdapter.currentList,
                                    originAdapter.currentList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition
                                )
                            } ?: run {
                                targetAdapter.onAdd(originAdapter.currentList[originPosition])
                            }
                        }
                    }

                    DragEvent.ACTION_DROP -> {
                        if (sameRecyclerView) {
                            if (isSwappable && targetPosition >= 0) {
                                val swpList = onSwapAnimation(originPosition, targetPosition)
                                onSwap(swpList)
                            } else {
                                return@OnDragListener false
                            }
                        } else {
                            if (!isSwappable) {
                                return@OnDragListener false
                            }

                            targetAdapter.currentList[targetPosition]?.let {
                                val setList = onSetAnimation(
                                    originAdapter,
                                    targetAdapter,
                                    targetRecyclerView,
                                    targetAdapter.currentList,
                                    originAdapter.currentList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition
                                )

                                onSet(
                                    setList.first,
                                    setList.second,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition
                                )

                            } ?: run {
                                targetAdapter.onAdd(originAdapter.currentList[originPosition])
                            }
                        }
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        setDefaultViewColor(originView, MAX)
                    }

                    DragEvent.ACTION_DRAG_EXITED -> {
                        Log.d("sss", "ACTION_DRAG_EXITED")
                        checkTheItemOutArea()
                        return@OnDragListener false
                    }

                    else -> {}
                }
            }

        }
        true
    }

//    private fun test(
//        callback: ((dragEvent: Int) -> Unit)? = null
//    ) {
//        callback?.invoke(DragEvent.ACTION_DRAG_STARTED)?: run {
//
//        }
//    }

    private fun setTest(originAdapter: RecyclerViewDragAdapter<T, VH>, originPos: Int) {
        val list = originAdapter.currentList.toMutableList()
        list.removeAt(originPos)
        originAdapter.submitList(list)
    }

    /**
     * set ( difference recyclerview )
     * */
    private fun onSetAnimation(
        originAdapter: RecyclerViewDragAdapter<T, VH>,
        targetAdapter: RecyclerViewDragAdapter<T, VH>,
        targetRecyclerView: RecyclerView,
        targetList: List<T>,
        originList: List<T>,
        originItem: T,
        from: Int,
        to: Int
    ): Pair<List<T>, List<T>> {
        return when (dragType()) {
            DragType.ONEBYONE -> {
                val temp = setOneByOne(originAdapter, targetList, originList, originItem, from, to)
                originAdapter.submitList(temp.second)
                temp.first to temp.second
            }

            DragType.SHIFT -> {
                val temp = setShift(targetAdapter, targetRecyclerView, targetList, originList, originItem, from, to)
                submitList(temp.first)
                temp.first to temp.second
            }
        }
    }

    private fun setOneByOne(
        originAdapter: RecyclerViewDragAdapter<T, VH>,
        targetList: List<T>,
        originList: List<T>,
        originItem: T,
        from: Int,
        to: Int
    ): Pair<List<T>, List<T>> {
        val target = targetList.toMutableList()
        target.add(to, originItem)

        val origin = originList.toMutableList()
        origin.removeAt(from)

        Log.d("sss","originItem:${originItem}")
        return Pair(target, origin)
    }

    /**
     * origin ITem -> target[to]. add
     * target[to] view alpha -> 0f
     *
     * */
    private fun setShift(
        targetAdapter: RecyclerViewDragAdapter<T,VH>,
        targetRecyclerView: RecyclerView,
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

    override fun onCurrentListChanged(previousList: MutableList<T>, currentList: MutableList<T>) {
        Log.d("sss","previousList:${previousList}, currentList:${currentList}")
        this.previousList = previousList
    }



    private fun checkTheItemOutArea() {

    }

    /**
     * swap  (same recyclerview)
     * */
    private fun onSwapAnimation(from: Int, to: Int): List<T> {
        val swapItemList = currentList.toMutableList()
        when (dragType()) {
            DragType.ONEBYONE -> {
                swapOneByOne(swapItemList, from, to)
            }
            DragType.SHIFT -> {
                swapShift(swapItemList, from, to)
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

    private fun swapShift(list: MutableList<T?>, from: Int, to: Int) {
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
//        when (originViewType()) {
//            ViewType.TRANSPARENT -> {
//                view.alpha = value
//                view.invalidate()
//            }
//            ViewType.CLEARLY -> {
//                return
//            }
//        }

        when (dragType()) {
            DragType.SHIFT -> {
                view.alpha = MIN
            }
            DragType.ONEBYONE -> {
                view.alpha = MAX
            }
        }
    }

    abstract fun dragType(): DragType

    abstract fun originViewType(): ViewType

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