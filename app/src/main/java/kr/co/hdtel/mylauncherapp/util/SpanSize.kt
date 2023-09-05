package kr.co.hdtel.mylauncherapp.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.hdtel.mylauncherapp.data.DataInfo

class SpanSize(private val rv: RecyclerView): GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int) =
        when (rv.adapter?.getItemViewType(position)) {
            DataInfo.ITEM_TYPE_LARGE -> 2
            DataInfo.ITEM_TYPE_SMALL -> 1
            else -> 1
        }
}