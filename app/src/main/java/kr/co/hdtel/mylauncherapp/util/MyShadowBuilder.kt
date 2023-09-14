package kr.co.hdtel.mylauncherapp.util

import android.graphics.Canvas
import android.graphics.Point
import android.view.View

open class MyShadowBuilder(view: View): View.DragShadowBuilder(view) {
    private var width = 0
    private var height = 0

    fun width(size: Float): MyShadowBuilder {
        this.width = (view.width * size).toInt()
        return this@MyShadowBuilder
    }

    fun height(size: Float): MyShadowBuilder {
        this.height = (view.height * size).toInt()
        return this@MyShadowBuilder
    }

    fun build(): MyShadowBuilder {
        return MyShadowBuilder(view)
    }

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val originWidth = view.width
        val originHeight = view.height
        width = (view.width*1.2f).toInt()
        height = (view.height*1.2f).toInt()
        size.set(width, height)
        touch.set(originWidth/2, originHeight/2)
    }

    override fun onDrawShadow(canvas: Canvas?) {
        super.onDrawShadow(canvas)
//        val pnt = Paint()
//        pnt.alpha = 255
//        pnt.color = Color.GREEN
//        canvas?.drawRect(0f,0f,(width).toFloat(), (height).toFloat(), pnt)
    }
}