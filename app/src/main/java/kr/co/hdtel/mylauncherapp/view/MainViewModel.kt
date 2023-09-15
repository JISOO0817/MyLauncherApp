package kr.co.hdtel.mylauncherapp.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.hdtel.mylauncherapp.data.DataInfo

class MainViewModel : ViewModel() {

    private val _topItems = MutableLiveData<List<DataInfo?>?>()
    val topItems: LiveData<List<DataInfo?>?> = _topItems

    private val _bottomItems = MutableLiveData<List<DataInfo?>?>()
    val bottomItems: LiveData<List<DataInfo?>?> = _bottomItems

    fun setInitData() {
        val initTopData = mutableListOf<DataInfo>().apply {
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "1", DataInfo.CONTAINER_TOP))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "2", DataInfo.CONTAINER_TOP))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "3", DataInfo.CONTAINER_TOP))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL,"4",DataInfo.CONTAINER_TOP))
        }

        val initData = mutableListOf<DataInfo>().apply {
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "A", DataInfo.CONTAINER_BOTTOM))
//            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "B", DataInfo.CONTAINER_BOTTOM))
//            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "C", DataInfo.CONTAINER_BOTTOM))
        }

        _topItems.value = initTopData
        _bottomItems.value = initData
    }

    fun onItemSwap(list: List<DataInfo?>) {
        _topItems.value = list

        Log.d("sss","swap ......list:${list}")
    }

    fun onItemSet(targetList: List<DataInfo>, originItem: DataInfo, from: Int, to: Int) {
        Log.d("sss", "viewModel onItemSet targetList:${targetList}")
        val topList = targetList.apply {
            this[to].container = DataInfo.CONTAINER_TOP
        }
//
        _topItems.value = topList
//
        val bottomItems = _bottomItems.value?.toMutableList()
        bottomItems?.let {
            it.removeAt(from)
        }
//
        _bottomItems.value = bottomItems
    }
}