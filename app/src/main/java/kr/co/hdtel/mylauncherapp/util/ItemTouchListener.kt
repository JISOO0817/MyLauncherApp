package kr.co.hdtel.mylauncherapp.util

interface ItemTouchListener {
    fun onItemMove(isDropped: Boolean, from: Int, to: Int): Boolean
}