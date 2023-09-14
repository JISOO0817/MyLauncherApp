package kr.co.hdtel.mylauncherapp.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.hdtel.mylauncherapp.data.DataInfo
import java.util.*

class MainViewModel : ViewModel() {

    private val _topItems = MutableLiveData<List<DataInfo?>?>()
    val topItems: LiveData<List<DataInfo?>?> = _topItems

    private val _bottomItems = MutableLiveData<List<DataInfo?>?>()
    val bottomItems: LiveData<List<DataInfo?>?> = _bottomItems

    fun setInitData() {
        Log.d("sss", "===setInitData===")
        val initTopData = mutableListOf<DataInfo>().apply {
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "1", DataInfo.CONTAINER_TOP))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "2", DataInfo.CONTAINER_TOP))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "3", DataInfo.CONTAINER_TOP))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL,"4",DataInfo.CONTAINER_TOP))
        }

        val initData = mutableListOf<DataInfo>().apply {
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "A", DataInfo.CONTAINER_BOTTOM))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "B", DataInfo.CONTAINER_BOTTOM))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "C", DataInfo.CONTAINER_BOTTOM))
        }

        _topItems.value = initTopData
        _bottomItems.value = initData
    }

    fun onItemSwap(list: List<DataInfo?>) {
//        shiftItem(list, from, to)
        _topItems.value = list

        Log.d("sss","swap ......list:${list}")
    }

//    private fun shiftItem(list: List<DataInfo?>, from: Int, to: Int) {
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

    fun onItemSet(targetList: List<DataInfo>) {
        Log.d("sss", "viewModel onItemSet targetList:${targetList}")
        _topItems.value = targetList
//        _topItems.value?.let { fav ->
////            val favoriteItems = fav.toMutableList()
//            val newList = mutableListOf<DataInfo?>().apply {
//                addAll(fav.toMutableList())
//            }
//
//            Log.d("sss","onItemSet item:${item}")
//            val originItem = item.copy().apply {
//                this.container = DataInfo.CONTAINER_TOP
//            }
//            newList.add(newList.size,originItem)
//            _topItems.value = newList
//
//            _bottomItems.value?.let { all ->
//                val allApps = mutableListOf<DataInfo?>().apply {
//                    addAll(all)
//                }
//                allApps.remove(item)
//                _bottomItems.value = allApps
//            }
//        }
    }
}