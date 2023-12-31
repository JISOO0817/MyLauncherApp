package kr.co.hdtel.mylauncherapp.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.hdtel.mylauncherapp.data.DataInfo
import kr.co.hdtel.mylauncherapp.view.MyAdapter

object LauncherBindingAdapter {

    @JvmStatic
    @BindingAdapter(
        value = ["data", "spanCount", "onADapterListener", "spanSizeLookup","swappable"],
        requireAll = false
    )
    fun RecyclerView.bindRecyclerView(
        data: List<DataInfo?>?,
        spanCount: Int,
        onAdapterListener: MyAdapter.OnAdapterListener,
        spanSizeLookup: SpanSize,
        swappable: Boolean
    ) {
        this.setHasFixedSize(true)
        val editLayoutManager = GridLayoutManager(this.context, spanCount)
        editLayoutManager.spanSizeLookup = spanSizeLookup
        this.layoutManager = editLayoutManager
        val adapter = (this.adapter as? MyAdapter) ?: MyAdapter(onAdapterListener,swappable)
            .also {
                this.adapter = it
                this.setOnDragListener(it.dragListener)
//                val helper = this@bindRecyclerView.tag as? ItemTouchHelper ?: run {
//                    val newHelper = ItemTouchHelper(ItemTouchCallback(it))
//                    this@bindRecyclerView.tag = newHelper
//                    newHelper
//                }
//
//                helper.attachToRecyclerView(this@bindRecyclerView)
            }
        data?.let {
            adapter.submitList(it)
        }
    }
}