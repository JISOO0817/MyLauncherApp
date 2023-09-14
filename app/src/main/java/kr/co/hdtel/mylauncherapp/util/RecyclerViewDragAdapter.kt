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

abstract class RecyclerViewDragAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, VH>(diffUtil) {
    abstract val isSwappable: Boolean

    val dragListener = View.OnDragListener { view, event ->
        event?.let {
            val originView = it.localState as View
            val originRecyclerView = originView.parent as RecyclerView
            val originAdapter = originRecyclerView.adapter as RecyclerViewDragAdapter<T, VH>
            val originPosition = originRecyclerView.getChildAdapterPosition(originView)

            var targetRecyclerView = view as? RecyclerView
            targetRecyclerView =
                targetRecyclerView.takeIf { false } ?: view.parent as? RecyclerView

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
                    setDefaultViewColor(originView, 0f)
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    if (originRecyclerView.id == targetRecyclerView.id) {
                        val swpList = onSwapItem(originPosition, targetPosition)
                        if (isSwappable && targetPosition >= 0) {
                            onSwap(false, swpList)
//                            originAdapter.onSwap(false, originPosition, targetPosition)
                        } else {
                            onSwapItem(originPosition, originAdapter.currentList.size - 1)
//                            originAdapter.onSwap(
//                                false,
//                                originPosition,
//                                originAdapter.currentList.size - 1
//                            )
                        }
                    } else {
                        Log.d("sss","other recyclerview entered")
                        Log.d("sss","originAdapter.currentList.size:${originAdapter.currentList.size}")
                        Log.d("sss","targetPos:${targetPosition}")
                        targetAdapter.currentList[targetPosition]?.let {
                            // 타겟하는 포지션이 홀딩하는 리사이클러뷰 사이즈보다 큰 경우.
                            if (originAdapter.currentList.size < targetPosition) {
                                val setList = onSetItem(
                                    targetAdapter.currentList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)

                                onSetTest(false,
                                    setList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)

//                                originAdapter.onSet(
//                                    false,
//                                    originPosition,
//                                    targetPosition,
//                                    targetAdapter.currentList[targetPosition]
//                                )
                            } else {
                                // 타겟하는 포지션이 홀딩하는 리사이클러뷰 사이즈보다 작은 경우.
                                // 홀딩 리사이클러뷰 사이즈가 타겟 포지션보다큰 경우
                                // 타겟은 1 2 // origin 리사이클러뷰 사이즈는 3 이상
                                Log.d("sss","other else")
                                val setList = onSetItem(
                                    targetAdapter.currentList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)

                                onSetTest(false,
                                    setList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)
                            }
                        } ?: run {
                            targetAdapter.onAdd(originAdapter.currentList[originPosition])
                        }
                    }
                }

                DragEvent.ACTION_DROP -> {
                    if (originRecyclerView.id == targetRecyclerView.id) {
                        if (isSwappable && targetPosition >= 0) {
                            val swpList = onSwapItem(originPosition, targetPosition)
                            originAdapter.onSwap(true, swpList)
//                            onSwapItem(originPosition, targetPosition)
//                            originAdapter.onSwap(true, originPosition, targetPosition)
                        } else {
                            return@OnDragListener false
                        }
                    } else {
                        targetAdapter.currentList[targetPosition]?.let {
                            if (originAdapter.currentList.size < targetPosition) {
                                val setList = onSetItem(
                                    targetAdapter.currentList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)

                                onSetTest(true,
                                    setList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)
//                                originAdapter.onSet(
//                                    true,
//                                    originPosition,
//                                    targetPosition,
//                                    targetAdapter.currentList[targetPosition]
//                                )
                            } else {
                                val setList = onSetItem(
                                    targetAdapter.currentList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)

                                onSetTest(true,
                                    setList,
                                    originAdapter.currentList[originPosition],
                                    originPosition,
                                    targetPosition)
//                                targetAdapter.onSet(
//                                    true,
//                                    originPosition,
//                                    targetPosition,
//                                    originAdapter.currentList[originPosition]
//                                )
                            }
                        } ?: run {
                            targetAdapter.onAdd(originAdapter.currentList[originPosition])
                        }
                    }
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    setDefaultViewColor(originView, 1f)
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    return@OnDragListener true
                }

                else -> {}
            }
        }
        true
    }

    private fun onSetItem(targetList: List<T>, originItem: T, from: Int, to: Int): List<T> {
        var list = targetList.toMutableList()
        when (exchangeType()) {
            ExchangeType.ONEBYONE -> {
                list = setOneByOne(targetList, originItem, from, to).toMutableList()
            }
            ExchangeType.SHIFT -> {

            }
        }
        Log.d("sss","onSetITem resutl List:${list}")
        return list
    }

    private fun setOneByOne(targetList: List<T>, originItem: T, from: Int, to: Int): List<T> {
        Log.d("sss","setOneByOne...")
        val tempList = targetList.toMutableList()
        val newList = mutableListOf<T>().apply {
            addAll(targetList)
        }

        newList[to] = originItem
        Log.d("sss","newList:${newList}")

        for (i in tempList.indices) {
            if (i > to) {
                Log.d("sss","i:${i}")
                newList[i] = tempList[to]
            }
        }
        return newList
    }

    private fun onSwapItem(from: Int, to: Int): List<T> {
        val swapItemList = currentList.toMutableList()
        when (exchangeType()) {
            ExchangeType.ONEBYONE -> {
                swapOneByOne(swapItemList, from, to)
            }
            ExchangeType.SHIFT -> {
                shiftItem(swapItemList, from, to)
                submitList(swapItemList)
            }
        }
        return swapItemList
    }

    private fun swapOneByOne(list: List<T>, from: Int, to: Int) {
        if (from > currentList.size - 1 || to > currentList.size - 1) {
            return
        }

        return Collections.swap(list,from,to)
    }

    private fun shiftItem(list: List<T>, from: Int, to: Int) {
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
        if (hideShadowMode()) {
            view.alpha = value
        }
    }

    abstract fun exchangeType(): ExchangeType

    abstract fun hideShadowMode(): Boolean

    abstract fun onAdd(item: T)

    abstract fun onRemove(item: T)

    abstract fun onSet(isDrop: Boolean, from: Int, to: Int, item: T)

    abstract fun onSetTest(isDrop: Boolean, targetList: List<T>, originItem: T, from: Int, to: Int)

    abstract fun onSwap(isDrop: Boolean, from: Int, to: Int)

    abstract fun onSwap(isDrop: Boolean, list: List<T>)
}