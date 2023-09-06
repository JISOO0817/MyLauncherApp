/*
 * Copyright (c) 2016-2023 HYUNDAI HT Co., Ltd. All rights reserved.
 * This software is the confidential and proprietary information of
 * HYUNDAI HT ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with HYUNDAI HT.
 */

package kr.co.hdtel.mylauncherapp.util

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ClickableViewAccessibility")
class ItemTouchCallback(
    private val listener: ItemTouchListener) :
    ItemTouchHelper.Callback() {
    private var draggingItemPosition: Int = -1
    private var moveToPos = 0
    private var isDropped = false
    private val defaultSize = 1.0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val drag =
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(drag, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val itemFrom = viewHolder.adapterPosition
        val actualLocation = target.layoutPosition
        if (itemFrom == RecyclerView.NO_POSITION || actualLocation == RecyclerView.NO_POSITION) {
            return false
        }

        moveToPos = actualLocation
        listener.onItemMove(false, from = itemFrom, to = actualLocation)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        isDropped = false

//        viewHolder?.let { vh ->
//            val emptyView: View? = vh.itemView.findViewById(R.id.item)
//            emptyView?.let {
//                return
//            } ?: run {
//                val childView: View? = vh.itemView.findViewById(R.id.bg_iv)
//                val removeView: View = vh.itemView.findViewById(R.id.remove_or_add)
//
//                childView?.let { view ->
//                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                        view.setBackgroundResource(R.drawable.item_touch_background)
//                        removeView.visibility = View.GONE
//                        draggingItemPosition = vh.bindingAdapterPosition
//                    } else {
//                        draggingItemPosition = -1
//                    }
//                }
//            }
//        }
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//        val emptyView: View? = viewHolder.itemView.findViewById(R.id.item)
//        emptyView?.let {
//            return@let
//        } ?: run {
//            val fullView: View = viewHolder.itemView.findViewById(R.id.all_bg)
//            val childView: View? = viewHolder.itemView.findViewById(R.id.bg_iv)
//
//            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && !isDropped) {
//                val size = 1.04f to 1.1f
//                fullView.apply {
//                    scaleX = size.first
//                    scaleY = size.first
//                    background?.let {
//                        if (it is GradientDrawable) {
//                            it.cornerRadius = resources.getDimension(R.dimen.corner_radius)
//                        }
//                    }
//                }
//                childView?.apply {
//                    scaleX = size.second
//                    scaleY = size.second
//                    setBackgroundResource(R.drawable.item_touch_background)
//                    background?.let {
//                        if (it is GradientDrawable) {
//                            it.cornerRadius = resources.getDimension(R.dimen.corner_radius)
//                        }
//                    }
//                }
//            } else {
//                fullView.apply {
//                    scaleX = defaultSize
//                    scaleY = defaultSize
//                }
//                childView?.apply {
//                    scaleX = defaultSize
//                    scaleY = defaultSize
//                    setBackgroundResource(R.drawable.item_background)
//                    background?.let {
//                        if (it is GradientDrawable) {
//                            it.cornerRadius = resources.getDimension(R.dimen.corner_radius)
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
//
//        val emptyView: View? = viewHolder.itemView.findViewById(R.id.item)
//        emptyView?.let {
//            return@let
//        } ?: run {
//            val fullView: View = viewHolder.itemView.findViewById(R.id.all_bg)
//            val childView: View = viewHolder.itemView.findViewById(R.id.bg_iv)
//            val removeOrAddView: View = viewHolder.itemView.findViewById(R.id.remove_or_add)
//            fullView.apply {
//                scaleX = defaultSize
//                scaleY = defaultSize
//            }
//            childView.apply {
//                setBackgroundResource(R.drawable.item_background)
//                scaleX = defaultSize
//                scaleY = defaultSize
//            }
//            removeOrAddView.visibility = View.VISIBLE
//
            if (draggingItemPosition != -1 && moveToPos != -1) {
                listener.onItemMove(true, draggingItemPosition, moveToPos)
            }
            isDropped = true
            draggingItemPosition = -1
            moveToPos = -1
//        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false
}