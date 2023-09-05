package kr.co.hdtel.mylauncherapp.view

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.hdtel.mylauncherapp.data.DataInfo

abstract class RecyclerviewListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>,
): ListAdapter<T, VH>(diffUtil) {

    abstract fun onAdd(item: T)

    abstract fun onRemove(item: T)

    abstract fun onSwap(list: List<DataInfo?>, from: Int, to: Int)

    abstract fun onError()
}