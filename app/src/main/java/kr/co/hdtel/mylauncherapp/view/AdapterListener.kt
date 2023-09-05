package kr.co.hdtel.mylauncherapp.view

interface AdapterListener<T> {
    fun onClick(item: T) {}
}