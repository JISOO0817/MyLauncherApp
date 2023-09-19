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

class MyAdapter(
    private val onAdapterListener: OnAdapterListener,
    override val isSwappable: Boolean
) : RecyclerViewDragAdapter<DataInfo, ViewHolder>(diffUtil) {
//    override var itemViews = 0

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
                ).apply {
                    largeBuilder = MyShadowBuilder(this.itemView)
                }
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
            is SmallViewHolder -> {
                holder.bind(getItem(position))
//                itemViews[position] = holder
            }
            is LargeViewHolder -> holder.bind(getItem(position))
            is EtcViewHolder -> holder.bind()
        }

        val item = getItem(position)
        holder.itemView.tag = item
        Log.d("ddd","tag:${holder.itemView.tag}")
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
        lateinit var largeBuilder: MyShadowBuilder
        fun bind(item: DataInfo?) {
            binding.nameTv.text = item?.name
            binding.root.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                view?.startDragAndDrop(clipData, largeBuilder, view, 0)
                true
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
        fun setOnViewModel(
            targetList: List<DataInfo>,
            originList: List<DataInfo>,
            originItem: DataInfo,
            from: Int,
            to: Int
        )

        fun errorOnViewModel()
    }

    override fun onSet(
        targetList: List<DataInfo>,
        originList: List<DataInfo>,
        originItem: DataInfo,
        from: Int,
        to: Int
    ) {
        onAdapterListener.setOnViewModel(targetList, originList, originItem, from, to)
    }

    override fun dragType(): DragType {
        return DragType.SHIFT
    }

    override fun onSwap(list: List<DataInfo>) {
        onAdapterListener.swapOnViewModel(list)
    }

    override fun onRemove(item: DataInfo) {

    }

    override fun onAdd(item: DataInfo) {

    }

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
}