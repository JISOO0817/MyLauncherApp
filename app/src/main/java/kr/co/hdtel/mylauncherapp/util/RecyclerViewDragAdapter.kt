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
            originRecyclerView?.let { originRV ->
                val originAdapter = originRV.adapter as RecyclerViewDragAdapter<T, VH>
                val originPosition = originRV.getChildAdapterPosition(originView)

                var targetRecyclerView = view as? RecyclerView
                targetRecyclerView = targetRecyclerView.takeIf { false } ?: view.parent as? RecyclerView

                val sameRecyclerView = targetRecyclerView?.let { targetRV ->
                    originRV.id == targetRV.id
                } ?: run {
                    false
                }

                val targetAdapter = targetRecyclerView.adapter as RecyclerViewDragAdapter<T, VH>
                val targetPosition = if (view is RecyclerView) {
                    targetAdapter.currentList.size
                } else {
                    targetRecyclerView.getChildAdapterPosition(view)
                }

                if (targetRecyclerView !is RecyclerView) {
                    return@OnDragListener false
                }

                val targetItem = targetAdapter.currentList[targetPosition]

                when (it.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        setDefaultViewColor(originView)
                    }

                    DragEvent.ACTION_DRAG_ENTERED -> {
                        Log.d("sss", "ACTION_DRAG_ENTERED")
                        if (!isSwappable || targetPosition < 0) {
                            return@OnDragListener false
                        }

                        if (sameRecyclerView) {
                            onSwapAnimation(originPosition, targetPosition)
                        } else {
                            targetItem?.let {
                                onSetAnimation(
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
                            targetItem?.let {
                                val setList = onSetAnimation(
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
                        setDefaultViewColor(originView)
                    }

                    /**
                     * 영역 나갔을 때, 자기의 rootView 밖으로 나간경우
                     * 아이템 제거처리 후 recyclerview가 아닌 영역에 드롭했을 때
                     * 원상복귀
                     * */
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
                    this.first to this.second
                }
            }

            DragType.SHIFT -> {
                setShift(targetList, originList, originItem, to).run {
                    submitList(this.first)
                    this.first to this.second
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
        val target = targetList.toMutableList().also {
            it.add(to, originItem)
        }

        val origin = originList.toMutableList().also {
            it.removeAt(from)
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

    override fun onCurrentListChanged(previousList: MutableList<T>, currentList: MutableList<T>) {
        Log.d("sss","previousList:${previousList}, currentList:${currentList}")
        this.previousList = if (previousList.isEmpty()) mutableListOf() else previousList
    }

    /**
     * check the item out the parent (recyclerview area
     *
     * */
    private fun checkTheItemOutArea() {
        if (dragType() == DragType.ONEBYONE) {
            return
        }

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

    private fun setDefaultViewColor(view: View) {
        view.alpha = when (dragType()) {
            DragType.SHIFT -> {
                MIN
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

    //    private fun test(
//        callback: ((dragEvent: Int) -> Unit)? = null
//    ) {
//        callback?.invoke(DragEvent.ACTION_DRAG_STARTED)?: run {
//
//        }
//    }

}