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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*


abstract class RecyclerViewDragAdapter<T, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, VH>(diffUtil) {
    abstract val isSwappable: Boolean

    //    private var returnState = 0
    private var isOut = false
    private var globalX = 0f
    private var globalY = 0f
    private var adjustedX = 0f
    private var adjustedY = 0f

    val dragListener = View.OnDragListener { view, event ->
        event?.let {
//            Log.d("sss","else... x:${globalX}, y:${globalY}")

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

                        Log.d("sss", "X:${globalX}, Y:${globalY}")

                        val x = event.x
                        val y = event.y

                        // 아이템 간격을 고려하여 좌표 보정

                        // 아이템 간격을 고려하여 좌표 보정
                        val layoutManager = originRV.layoutManager as GridLayoutManager
                        val spanCount = layoutManager.spanCount
                        val itemWidth: Int = originRV.width / spanCount
                        val itemHeight: Int = originRV.height / spanCount
                        val column = (x / itemWidth).toInt()
                        val row = (y / itemHeight).toInt()


                        adjustedX = (column * itemWidth + itemWidth / 2).toFloat()
                        adjustedY = (row * itemHeight + itemHeight / 2).toFloat()

                        Log.d("sss","adjustedX:${adjustedX}, adjustedY:${adjustedY}")
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
//                                returnState = 1
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

                            /**
                             * 삭제될아이템 (origin)
                             * */
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
                        originView.setDefaultViewColor(MAX)
                        return@OnDragListener true
                    }

                    DragEvent.ACTION_DRAG_EXITED -> {
                        Log.d("sss", "ACTION_DRAG_EXITED")
                        // x와y좌표가 리사이클러뷰 내부에 있지 않은 경우만 원상복귀
                        // false이면 리스트 새로고침
                        if (targetRecyclerView.checkIsOutSide(globalX, globalY)) {
                            Log.d("sss", "recyclerview가 아닙니다.")
                            val tempList = targetAdapter.currentList.toMutableList()
                            tempList.remove(originAdapter.currentList[originPosition])
                            Log.d("sss", "제거할 아이템:${originAdapter.currentList[originPosition]}")
                            targetAdapter.submitList(tempList)
                        }

                        return@OnDragListener false
                    }

                    else -> {
                        Log.d("sss","else...")
                        return@OnDragListener false
                    }
                }
            }
        }
        true
    }

    private fun RecyclerView.hideItemIfTagMatches(position: Int, tag: Any?) {
        val viewHolder = findViewHolderForAdapterPosition(position)
        viewHolder?.itemView?.visibility =
            if (viewHolder?.itemView?.tag == tag) View.INVISIBLE else View.VISIBLE
    }

    private fun RecyclerView.showItemIfTagMatches(position: Int, tag: Any?) {
        val viewHolder = findViewHolderForAdapterPosition(position)
        viewHolder?.itemView?.visibility =
            if (viewHolder?.itemView?.tag == tag) View.VISIBLE else View.INVISIBLE
    }

    private fun RecyclerView.checkIsOutSide(x: Float, y: Float): Boolean {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + this.width
        val bottom = top + this.height

        Log.d("sss","left:${left}, right:${right}, top:${top}, bottom:${bottom}")
        //아래 return값에 대해서 하나라도 만족하면 리사이클러뷰 밖에 있는 것
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