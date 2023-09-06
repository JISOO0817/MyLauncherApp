package kr.co.hdtel.mylauncherapp.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.hdtel.mylauncherapp.data.DataInfo
import java.util.*

class MainViewModel : ViewModel() {

    private val _items = MutableLiveData<List<DataInfo?>?>()
    val items: LiveData<List<DataInfo?>?> = _items

    fun setInitData() {
        Log.d("sss", "===setInitData===")
        val initData = mutableListOf<DataInfo>().apply {
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "A"))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "B"))
            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "C"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "TEST"))
//            add(DataInfo(DataInfo.ITEM_TYPE_ETC, "ETC"))
        }

        _items.value = initData
        Log.d("sss", "items:${items.value}")
    }

//    private fun setItemPosition(list: List<DataInfo?>): List<DataInfo?> {
//        val resultList = arrayListOf<DataInfo?>()
//        var cursorLine = 1
//        var cursor = 0
//        var totalWeightCount = 0
//
//        for (widgetInfo in list) {
//            widgetInfo ?: continue
//            val itemWeight = widgetInfo.type
//            totalWeightCount += itemWeight
//
//            if (cursor > 2) {
//                cursorLine++
//                cursor = 0
//            }
//
//            if (itemWeight > (3 - cursor)) {
//                resultList.add(null)
//                cursorLine++
//                cursor = 0
//            }
//            resultList.add(widgetInfo)
//            cursor += itemWeight
//        }
//
//        val totalCursorCnt = (cursorLine - 1) * 3 + (cursor + 1) - 1
//        if (totalCursorCnt < 6) {
//            repeat(6 - totalCursorCnt) {
//                resultList.add(null)
//            }
//        }
//        return resultList
//    }

    fun onItemSwap(list: List<DataInfo?>, from: Int, to: Int) {
        Log.d("sss", "onItemSwap call(), before:${list}, from:${from},to:${to}")
        val data = list.toMutableList()
        //뒤에있던 아이템을 앞으로 당기면 하나씩 뒤로 이동
        shiftItem(data, from, to)
//        Collections.swap(data, from, to)
        _items.value = data
        Log.d("sss", "after:${data}")
    }

    private fun shiftItem(list: List<DataInfo?>, from: Int, to: Int) {
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

    //            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "D","10,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "E","15,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "F","43,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "G","2,700"))
//            add(DataInfo(DataInfo.ITEM_TYPE_ETC, "테스트테스트","-----"))
//            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "AA","30,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "BB","130,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_SMALL, "CC","7,500"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "DD","10,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "EE","15,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "FF","43,000"))
//            add(DataInfo(DataInfo.ITEM_TYPE_LARGE, "GG","2,700"))
}