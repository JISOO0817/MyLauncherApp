package kr.co.hdtel.mylauncherapp.view

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kr.co.hdtel.mylauncherapp.data.DataInfo
import kr.co.hdtel.mylauncherapp.databinding.ItemEtcBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemLargeBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemNullBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemSmallBinding
import kr.co.hdtel.mylauncherapp.util.DragType
import kr.co.hdtel.mylauncherapp.util.MyShadowBuilder
import kr.co.hdtel.mylauncherapp.util.RecyclerViewDragAdapter
import kr.co.hdtel.mylauncherapp.util.ViewType

class MyAdapter(
    private val onAdapterListener: OnAdapterListener,
    override val isSwappable: Boolean
) : RecyclerViewDragAdapter<DataInfo, ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = when (viewType) {
            NULL_TYPE -> {
                NullViewHolder(
                    ItemNullBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), dragListener
                ).apply {
                    nullBuilder = MyShadowBuilder(this.itemView)
                }
            }

            SMALL_TYPE -> {
                SmallViewHolder(
                    ItemSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), dragListener
                ).apply {
                    builder = MyShadowBuilder(this.itemView)
                }
            }

            LARGE_TYPE -> {
                LargeViewHolder(
                    ItemLargeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), dragListener
                )
            }

            ETC_TYPE -> {
                EtcViewHolder(
                    ItemEtcBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), dragListener
                )
            }

            else -> {
                SmallViewHolder(
                    ItemSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), dragListener
                )
            }
        }
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item?.type) {
            DataInfo.ITEM_TYPE_SMALL -> SMALL_TYPE
            DataInfo.ITEM_TYPE_LARGE -> LARGE_TYPE
            DataInfo.ITEM_TYPE_ETC -> ETC_TYPE
            DataInfo.ITEM_TYPE_NULL -> NULL_TYPE
            else -> SMALL_TYPE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is NullViewHolder -> holder.bind()
            is SmallViewHolder -> holder.bind(getItem(position))
            is LargeViewHolder -> holder.bind(getItem(position))
            is EtcViewHolder -> holder.bind()
        }
    }

    class NullViewHolder(
        private val binding: ItemNullBinding,
        private val dragListener: View.OnDragListener
    ) : ViewHolder(binding.root) {
        lateinit var nullBuilder: MyShadowBuilder
        fun bind() {
            binding.root.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                view?.startDragAndDrop(clipData, nullBuilder, view, 0)
                true
            }
            binding.root.setOnDragListener(dragListener)
        }
    }

    class SmallViewHolder(
        private val binding: ItemSmallBinding,
        private val dragListener: View.OnDragListener
    ) : ViewHolder(binding.root) {
        lateinit var builder: MyShadowBuilder
        fun bind(item: DataInfo?) {
            binding.nameTv.text = item?.name
            binding.root.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                view?.startDragAndDrop(clipData, builder, view, 0)
                true
            }

            binding.root.setOnDragListener(dragListener)
        }
    }

    class LargeViewHolder(
        private val binding: ItemLargeBinding,
        private val dragListener: View.OnDragListener
    ) : ViewHolder(binding.root) {
        fun bind(item: DataInfo?) {
            binding.nameTv.text = item?.name
            binding.root.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder()
                view?.startDragAndDrop(clipData, shadowBuilder, view, 0)
                false
            }

            binding.root.setOnDragListener(dragListener)
        }
    }

    class EtcViewHolder(
        private val binding: ItemEtcBinding,
        private val dragListener: View.OnDragListener
    ) : ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(clipData, shadowBuilder, null, 0)
                false
            }
            binding.root.setOnDragListener(dragListener)
        }
    }


    interface OnAdapterListener {
        fun addOnViewModel(widgetItemInfo: DataInfo)
        fun removeOnViewModel(widgetItemInfo: DataInfo)
        fun swapOnViewModel(list: List<DataInfo>)
        fun setOnViewModel(targetList: List<DataInfo>, originItem: DataInfo, from: Int, to: Int)
        fun errorOnViewModel()
    }

//    override fun onRemove(item: DataInfo) {
    //
//    }

//    override fun onSwap(list: List<DataInfo?>, from: Int, to: Int) {
//        Log.d("sss","onSwap")
//        onAdapterListener.swapOnViewModel(list, from, to)
//    }
//
//    override fun onError() {
//       //
//    }

    companion object {
        const val NULL_TYPE = 0
        const val SMALL_TYPE = 1
        const val LARGE_TYPE = 2
        const val ETC_TYPE = 3

        val diffUtil = object : DiffUtil.ItemCallback<DataInfo>() {
            override fun areItemsTheSame(
                oldItem: DataInfo,
                newItem: DataInfo
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: DataInfo,
                newItem: DataInfo
            ) = oldItem.name == newItem.name
        }
    }

    override fun onSwap(isDrop: Boolean, from: Int, to: Int) {
//        if (from > currentList.size - 1 || to > currentList.size - 1) {
//            return
//        }
//
//        val newList = currentList.toMutableList()
//        shiftItem(newList, from, to)
//
//        if (isDrop) {
//            onAdapterListener.swapOnViewModel(newList, from, to)
//        }
//
//        submitList(newList)
    }

    override fun onSet(isDrop: Boolean, from: Int, to: Int, item: DataInfo) {
//        Log.d("sss", "onSet call()")
//        val newList = currentList.toMutableList()
//        shiftItem(newList, from, to)
//        newList.add(newLis/t.size-1,item)

//        if (isDrop) {
//            onAdapterListener.setOnViewModel(from,to, item)
//        }

//        submitList(newList)
    }

    override fun onSetTest(isDrop: Boolean, targetList: List<DataInfo>, originItem: DataInfo, from: Int, to: Int) {
        if (isDrop) {
            Log.d("sss","onSetTest drop... list:${targetList}")
            onAdapterListener.setOnViewModel(targetList, originItem, from, to)
        }
    }

    override fun dragType(): DragType {
        return DragType.ONEBYONE
    }

    override fun originViewType(): ViewType {
        return ViewType.TRANSPARENT
    }

    override fun onSwap(isDrop: Boolean, list: List<DataInfo>) {
        if (isDrop) {
            onAdapterListener.swapOnViewModel(list)
        }
    }

//    private fun shiftItem(list: List<DataInfo>, from: Int, to: Int) {
//        return if (from < to) {
//            for (i in from until to) {
//                Collections.swap(list, i, i + 1)
//            }
//        } else {
//            for (i in from downTo to + 1) {
//                Collections.swap(list, i, i - 1)
//            }
//        }
//    }

    private fun moveItem(index: Int, item: DataInfo) {
//        var tempList = mutableListOf<DataInfo>()
//        tempList.addAll(currentList)
//        tempList.add(index,item)
//        tempList
        Log.d("sss","moveItem index:${index},item:${item}")
    }

    override fun onRemove(item: DataInfo) {

    }

    override fun onAdd(item: DataInfo) {

    }
}