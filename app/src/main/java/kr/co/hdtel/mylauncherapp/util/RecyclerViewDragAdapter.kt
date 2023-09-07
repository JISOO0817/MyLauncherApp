/*
 * Copyright (c) 2016-2023 HYUNDAI HT Co., Ltd. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * HYUNDAI HT ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with HYUNDAI HT.
 */

package kr.co.hdtel.mylauncherapp.util

import android.annotation.SuppressLint
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


abstract class RecyclerViewDragAdapter<T, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, VH>(diffUtil) {
    abstract val isSwappable: Boolean
    private val dragDropType: DragDropType = DragDropType.SHADOW
    private var originDX = 0f
    private var originDY = 0f
    val dragListener = View.OnDragListener { view, event ->
        event?.let {
            //시작 위치
            when (it.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d("sss", "***** drag event started *****")
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.d("sss", "*** drag event entered *****")
                    val holdingView = it.localState as View
                    view.scaleX
                    val holdingRecyclerView = holdingView.parent as RecyclerView
                    val holdingAdapter =
                        holdingRecyclerView.adapter as RecyclerViewDragAdapter<*, *>
                    val holdingPosition = holdingRecyclerView.getChildAdapterPosition(holdingView)
//                    holdingView.visibility = View.INVISIBLE
                    view?.let { targetView ->
                        var targetRecyclerView: RecyclerView? = targetView as? RecyclerView
                        if (targetRecyclerView == null) {
                            targetRecyclerView = targetView.parent as? RecyclerView
                        }
                        if (targetRecyclerView !is RecyclerView) {
                            return@OnDragListener false
                        }
                        val targetAdapter =
                            targetRecyclerView.adapter as RecyclerViewDragAdapter<*, *>
                        val targetPosition = if (targetView is RecyclerView) {
                            targetAdapter.currentList.size
                        } else {
                            targetRecyclerView.getChildAdapterPosition(targetView)
                        }

                        if (holdingRecyclerView.id == targetRecyclerView.id) {
                            if (targetPosition >= 0) {
                                if (currentList.size-1 < targetPosition) {
                                    return@OnDragListener false
                                }
                                Log.d("sss","holdingPos:${holdingPosition}, target:${targetPosition}")
                                holdingAdapter.onSwap(holdingPosition, targetPosition)
                            }
                        }
//
                    } ?: run {
                        return@OnDragListener false
                    }
                }

                DragEvent.ACTION_DROP -> {
                    val holdingView = it.localState as View
                    val holdingRecyclerView = holdingView.parent as RecyclerView
                    val holdingAdapter =
                        holdingRecyclerView.adapter as RecyclerViewDragAdapter<*, *>
                    val holdingPosition = holdingRecyclerView.getChildAdapterPosition(holdingView)
                    holdingView.visibility = View.VISIBLE
//                        val sourceView = it.localState as View
//                        val sourceRecyclerView = sourceView.parent as RecyclerView
//                        val sourceAdapter = sourceRecyclerView.adapter as RecyclerViewDragAdapter<T, VH>
//                        val sourcePosition = sourceRecyclerView.getChildAdapterPosition(sourceView)
//                        view?.let { targetView ->
//                            var targetRecyclerView: RecyclerView? = targetView as? RecyclerView
//                            if (targetRecyclerView == null) {
//                                targetRecyclerView = targetView.parent as? RecyclerView
//                            }
//                            if (targetRecyclerView !is RecyclerView) {
//                                return false
//                            }
//                            val targetAdapter =
//                                targetRecyclerView!!.adapter as RecyclerViewDragAdapter<T, VH>
//                            val targetPosition = if (targetView is RecyclerView) {
//                                targetAdapter.currentList.size
//                            } else {
//                                targetRecyclerView!!.getChildAdapterPosition(targetView)
//                            }
//                            if (sourceRecyclerView.id == targetRecyclerView!!.id) {
//                                if (isSwappable) {
//            //                                if (targetPosition >= 0 && sourceAdapter.currentList[targetPosition] != null) {
//                                    if (targetPosition >= 0) {
//                                        if (targetPosition >= 0) {
//                                            sourceAdapter.onSwap(sourcePosition, targetPosition)
//                                        } else {
//                                            sourceAdapter.onSwap(
//                                                sourcePosition,
//                                                sourceAdapter.currentList.size - 1
//                                            )
//                                        }
//                                    }
//                                }
//                            } else {
//                                try {
//                                    targetAdapter.currentList[targetPosition]?.let {
//                                        if (sourceAdapter.currentList.size < targetPosition) {
//                                            sourceAdapter.onSet(
//                                                sourcePosition,
//                                                targetAdapter.currentList[targetPosition]
//                                            )
//                                        } else {
//                                            targetAdapter.onSet(
//                                                targetPosition,
//                                                sourceAdapter.currentList[sourcePosition]
//                                            )
//                                        }
//                                    } ?: run {
//                                        targetAdapter.onAdd(sourceAdapter.currentList[sourcePosition])
//                                    }
//                                } catch (e: IndexOutOfBoundsException) {
//                                    sourceAdapter.onRemove(sourceAdapter.currentList[sourcePosition])
//                                }
//                            }
//                        } ?: run {
//                            return false
//                        }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.d("sss", "***** drag event ended *****")
                }
                else -> {

                }
            }
        }
        true
    }

//    private fun animateDragToStart(initialView: View, fromX: Float, fromY: Float) {
//        val topMargin = fromY - initialView.top
//        val leftMargin = fromX - initialView.left
//        val translateAnimation: Animation = TranslateAnimation(
//            leftMargin - initialView.width / 2,
//            0,
//            topMargin - initialView.height / 2,
//            0
//        )
//        translateAnimation.duration = 500
//        translateAnimation.interpolator = AccelerateInterpolator()
//        initialView.startAnimation(translateAnimation)
//        initialView.visibility = View.VISIBLE
//    }


    enum class DragDropType {
        SHADOW, ACTUAL
    }

    abstract fun dragDropType(type: DragDropType)

    abstract fun onAdd(item: T)

    abstract fun onRemove(item: T)

    abstract fun onSet(index: Int, item: T)

    abstract fun onSwap(from: Int, to: Int)
}