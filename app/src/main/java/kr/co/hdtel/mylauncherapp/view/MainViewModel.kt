package kr.co.hdtel.mylauncherapp.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.hdtel.mylauncherapp.data.DataInfo

class MainViewModel: ViewModel() {

    private val _items = MutableLiveData<List<DataInfo?>?>()
    val items: LiveData<List<DataInfo?>?> = _items

    fun setInitData() {
        Log.d("sss","===setInitData===")
        val initData = mutableListOf<DataInfo>().apply {
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "A","30,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "B","130,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "C","7,500"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "D","10,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "E","15,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "F","43,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "G","2,700"))
            add(DataInfo(DataInfo.ITEM_TYPE_ETC, "테스트테스트","-----"))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "AA","30,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "BB","130,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "CC","7,500"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "DD","10,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "EE","15,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "FF","43,000"))
            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "GG","2,700"))
        }
        _items.value = initData
        Log.d("sss","items:${items.value}")
    }

    fun onItemSwap(list: List<DataInfo?>, from: Int, to: Int) {
        _items.value = list.toMutableList()
    }
}