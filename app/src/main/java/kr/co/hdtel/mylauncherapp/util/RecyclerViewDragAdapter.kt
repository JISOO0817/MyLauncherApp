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
import kotlin.properties.Delegates

abstract class RecyclerViewDragAdapter<T, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, VH>(diffUtil) {
    abstract val isSwappable: Boolean
    private var previousList = mutableListOf<T>()
    private var viewHolder: RecyclerView.ViewHolder? = null
    /**
     * 0 -> 초기상태
     * 1 -> 타겟 리사이클러뷰에 들어옴
     * 2 -> 드롭완료
     * 3 -> 성공적으로 드롭되지 않았으므로 추가한 아이템 제거
     * */
    private var returnState = 0
    private var isOut = false

    val dragListener = View.OnDragListener { view, event ->
        event?.let {
            val locationX = it.x
            val locationY = it.y

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

                val targetItem = targetAdapter.currentList[targetPosition]

                when (it.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        setDefaultViewColor(originView, MIN)
                    }

                    DragEvent.ACTION_DRAG_LOCATION -> {
                        Log.d("sss","ACTION_DRAG_LOCATION")
                        val location = IntArray(2)
                        view.getLocationOnScreen(location)

                        val globalX = location[0] + event.x
                        val globalY = location[1] + event.y

                        Log.d("sss","target width:${originRecyclerView.width}, target height:${originRecyclerView.height}" +
                                "lobalX:${globalX}, globalY:${globalY}")

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

                                viewHolder = targetRecyclerView.findViewHolderForAdapterPosition(targetPosition)
                                if (viewHolder?.itemView?.tag == originAdapter.currentList[originPosition]) {
                                    viewHolder?.itemView?.visibility = View.INVISIBLE
                                }

                                returnState = 1

                            } ?: run {
                                targetAdapter.onAdd(originAdapter.currentList[originPosition])
                            }
                        }
                    }

                    DragEvent.ACTION_DROP -> {
                        Log.d("sss","ACTION_DROP action:${event.result}")
                        Log.d("sss","ACTION_DROP")
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

                                viewHolder = targetRecyclerView.findViewHolderForAdapterPosition(targetPosition)
                                if (viewHolder?.itemView?.tag == originAdapter.currentList[originPosition]) {
                                    viewHolder?.itemView?.visibility = View.VISIBLE
                                }

                            } ?: run {
                                targetAdapter.onAdd(originAdapter.currentList[originPosition])
                            }
                        }

                        return@OnDragListener true
                    }

                    DragEvent.ACTION_DRAG_ENDED -> {
                        setDefaultViewColor(originView, MAX)
                        return@OnDragListener true
                    }

                    /**
                     * 영역 나갔을 때, 자기의 rootView 밖으로 나간경우
                     * 아이템 제거처리 후 recyclerview가 아닌 영역에 드롭했을 때
                     * 원상복귀
                     * */
                    DragEvent.ACTION_DRAG_EXITED -> {
                        Log.d("sss", "ACTION_DRAG_EXITED")

                        /**
                         * 그냥 returnState만 따지면 안 되고,
                         * 리사이클러뷰 영역이 아닌지도 따져야함.
                         * */
                        Log.d("ddd","isOut${isOut}")
                        if (returnState == 1 && isOut) {
                            Log.d("ddd","returnSTate1... 유효하지 않은 드롭")
                            val tempList = targetAdapter.currentList.toMutableList()
                            tempList.remove(originAdapter.currentList[originPosition])
                            targetAdapter.submitList(tempList)
                        }

                        return@OnDragListener false
                    }

                    else -> {}
                }
            }

        }
        true
    }

    private fun isWithinRecyclerViewBounds(recyclerView: RecyclerView, x: Float, y: Float): Boolean {
        val location = IntArray(2)
        recyclerView.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + recyclerView.width
        val bottom = top + recyclerView.height
        return x >= left && x <= right && y >= top && y <= bottom
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
        Log.d("sss", "previousList:${previousList}, currentList:${currentList}")
        this.previousList = if (previousList.isEmpty()) mutableListOf() else previousList
    }

//        Log.d("sss","x:${x},y:${y}")
//        val recyclerViewLocation = IntArray(2)
//        recyclerView.getLocationOnScreen(recyclerViewLocation)
//        val xInRecyclerView = x - recyclerViewLocation[0]
//        val yInRecyclerView = y - recyclerViewLocation[1]
//
//        Log.d("sss","xInRecyclerView:${xInRecyclerView}, yInRecyclerView:${yInRecyclerView}")

    // 변환한 좌표를 기반으로 아이템이 리사이클러뷰 밖에 있는지 확인
//        return xInRecyclerView > recyclerView.width || yInRecyclerView > recyclerView.height

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

    private fun setDefaultViewColor(view: View, alpha: Float) {
        view.alpha = when (dragType()) {
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