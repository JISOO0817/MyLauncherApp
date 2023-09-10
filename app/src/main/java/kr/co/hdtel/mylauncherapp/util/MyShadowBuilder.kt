package kr.co.hdtel.mylauncherapp.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import kr.co.hdtel.mylauncherapp.R

class MyShadowBuilder(view: View): View.DragShadowBuilder(view) {
    private var shadow: View
    private var width = 0
    private var height = 0

    init {
        shadow = view
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
        val pnt = Paint()
        pnt.alpha = 255
        pnt.color = Color.GREEN
//        view.alpha = 1f
        canvas?.drawRect(0f,0f,(width).toFloat(), (height).toFloat(), pnt)

//        val shadowPaint = Paint()
//        shadow.alpha = 1f
//        val originBac = view.background
//        shadow.background = C/olorDrawable(Color.BLUE)
//        canvas?.let {
//            Log.d("sss", "canvas...")
//            it.drawRect(0f, 0f, view.width.toFloat(), view.height.toFloat(), shadowPaint)
//            view.draw(it)
//        }
//            view.background = originBac
    }
}