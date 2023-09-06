package kr.co.hdtel.mylauncherapp.view

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.hdtel.mylauncherapp.data.DataInfo
import kr.co.hdtel.mylauncherapp.databinding.ItemEtcBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemLargeBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemNullBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemSmallBinding
import kr.co.hdtel.mylauncherapp.util.ItemTouchListener
import kr.co.hdtel.mylauncherapp.util.RecyclerViewDragAdapter
import java.util.*

class MyAdapter(private val onAdapterListener: OnAdapterListener, override val isSwappable: Boolean) :
    RecyclerViewDragAdapter<DataInfo, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NULL_TYPE -> {
                NullViewHolder(
                    ItemNullBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            SMALL_TYPE -> {
                SmallViewHolder(
                    ItemSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), dragListener
                )
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
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item?.type) {
            DataInfo.ITEM_TYPE_SMALL -> SMALL_TYPE
            DataInfo.ITEM_TYPE_LARGE -> LARGE_TYPE
            DataInfo.ITEM_TYPE_ETC -> ETC_TYPE
            null -> NULL_TYPE
            else -> SMALL_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NullViewHolder -> holder.bind()
            is SmallViewHolder -> holder.bind(getItem(position))
            is LargeViewHolder -> holder.bind(getItem(position))
            is EtcViewHolder -> holder.bind()
        }
    }

    class NullViewHolder(
        private val binding: ItemNullBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    class SmallViewHolder(
        private val binding: ItemSmallBinding,
        private val dragListener: View.OnDragListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataInfo?) {
            binding.nameTv.text = item?.name

            binding.root.setOnLongClickListener { view ->
                view.animate()
                val clipData = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(clipData, shadowBuilder, view, 0)
                false
            }

            binding.root.setOnDragListener(dragListener)
        }
    }

    class LargeViewHolder(
        private val binding: ItemLargeBinding,
        private val dragListener: View.OnDragListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataInfo?) {
            binding.nameTv.text = item?.name

            binding.root.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(clipData, shadowBuilder, view, 0)
                false
            }

            binding.root.setOnDragListener(dragListener)
        }
    }

    class EtcViewHolder(
        private val binding: ItemEtcBinding,
        private val dragListener: View.OnDragListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(clipData, shadowBuilder, view, 0)
                false
            }
            binding.root.setOnDragListener(dragListener)
        }
    }

    interface OnAdapterListener {
        fun addOnViewModel(widgetItemInfo: DataInfo)
        fun removeOnViewModel(widgetItemInfo: DataInfo)
        fun swapOnViewModel(list: List<DataInfo?>,from: Int, to: Int)
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
            ) =
                oldItem == newItem
        }
    }

    override fun onAdd(item: DataInfo) {
        //
    }

//    override fun onItemMove(isDropped: Boolean, from: Int, to: Int): Boolean {
//        if (currentList[to] == null || currentList[from] == null) {
//            return false
//        }
//
//        val newList = currentList.toMutableList()
//        shiftItem(newList,from, to)
//
//        if (isDropped) {
//            onSwap(currentList, from, to)
//            return false
//        }
//
//        submitList(newList)
//        return false
//    }

    private fun shiftItem(list: List<DataInfo>, from: Int, to: Int) {
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

    override fun onSet(index: Int, item: DataInfo) {
        //
    }

    override fun onSwap(from: Int, to: Int) {
        if (currentList[to] == null || currentList[from] == null) {
            return
        }
        onAdapterListener.swapOnViewModel(currentList.toMutableList(),from,to)
    }

    override fun onRemove(item: DataInfo) {
        //
    }
}