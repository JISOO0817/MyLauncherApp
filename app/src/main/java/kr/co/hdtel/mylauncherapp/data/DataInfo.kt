package kr.co.hdtel.mylauncherapp.data

data class DataInfo(
    var type: Int?,
    var name: String?,
    var container: Int?
): Cloneable {
    public override fun clone(): DataInfo {
        return DataInfo(
            type,
            name,
            container
        )
    }

    companion object {
        const val CONTAINER_TOP = 0
        const val CONTAINER_BOTTOM = 1

        const val ITEM_TYPE_NULL = 0
        const val ITEM_TYPE_SMALL = 1
        const val ITEM_TYPE_LARGE = 2
        const val ITEM_TYPE_ETC = 3
    }
}