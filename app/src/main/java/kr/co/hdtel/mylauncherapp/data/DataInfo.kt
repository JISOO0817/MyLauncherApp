package kr.co.hdtel.mylauncherapp.data

data class DataInfo(
    var type: Int,
    var name: String,
    var price: String
): Cloneable {
    public override fun clone(): DataInfo {
        return DataInfo(
            type,
            name,
            price
        )
    }

    companion object {
        const val ITEM_TYPE_SMALL = 1
        const val ITEM_TYPE_LARGE = 2
        const val ITEM_TYPE_ETC = 3
    }
}