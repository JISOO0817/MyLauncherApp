package kr.co.hdtel.mylauncherapp.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.hdtel.mylauncherapp.data.DataInfo
import kr.co.hdtel.mylauncherapp.databinding.ItemEtcBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemLargeBinding
import kr.co.hdtel.mylauncherapp.databinding.ItemSmallBinding
import kr.co.hdtel.mylauncherapp.util.ItemTouchListener
import java.util.*

class MyAdapter(private val onAdapterListener: OnAdapterListener) :
    RecyclerviewListAdapter<DataInfo, RecyclerView.ViewHolder>(diffUtil),
    ItemTouchListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SMALL_TYPE -> {
                SmallViewHolder(
                    ItemSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onAdapterListener
                )
            }

            LARGE_TYPE -> {
                LargeViewHolder(
                    ItemLargeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onAdapterListener
                )
            }

            ETC_TYPE -> {
                EtcViewHolder(
                    ItemEtcBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                SmallViewHolder(
                    ItemSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onAdapterListener
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
            else -> SMALL_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SmallViewHolder -> holder.bind(getItem(position))
            is LargeViewHolder -> holder.bind(getItem(position))
            is EtcViewHolder -> holder.bind(getItem(position))
        }
    }

    class SmallViewHolder(
        private val binding: ItemSmallBinding,
        private val onAdapterListener: OnAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataInfo) {
            binding.nameTv.text = item.name
            binding.priceTv.text = item.price


        }
    }

    class LargeViewHolder(
        private val binding: ItemLargeBinding,
        onAdapterListener: OnAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataInfo) {
            binding.nameTv.text = item.name
            binding.priceTv.text = item.price
        }
    }

    class EtcViewHolder(
        private val binding: ItemEtcBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataInfo) {

        }
    }

    interface OnAdapterListener : AdapterListener<DataInfo> {
        fun addOnViewModel(widgetItemInfo: DataInfo)
        fun removeOnViewModel(widgetItemInfo: DataInfo)
        fun swapOnViewModel(list: List<DataInfo?>, from: Int, to: Int)
        fun errorOnViewModel()
    }

    override fun onRemove(item: DataInfo) {
        //
    }

    override fun onSwap(list: List<DataInfo?>, from: Int, to: Int) {
        Log.d("sss","onSwap")
        onAdapterListener.swapOnViewModel(list, from, to)
    }

    override fun onError() {
       //
    }

    companion object {
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

    override fun onItemMove(isDropped: Boolean, from: Int, to: Int): Boolean {
        if (currentList[to] == null || currentList[from] == null) {
            return false
        }

        val newList = currentList.toMutableList()
        shiftItem(newList,from, to)

        if (isDropped) {
            onSwap(currentList, from, to)
            return false
        }

        submitList(newList)
        return false
    }

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
}