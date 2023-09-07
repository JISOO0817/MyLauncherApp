package kr.co.hdtel.mylauncherapp.util

import android.graphics.Canvas
import android.graphics.Point
import android.view.View

class MyShadowBuilder(view: View): View.DragShadowBuilder(view) {
    private lateinit var shadow: View

    init {
        setViewpoint()
    }

    private fun setViewpoint() {
        shadow = view
//        shadow.background = R.
//        shadow.background =
    }

    // Defines a callback that sends the drag shadow dimensions and touch point
    // back to the system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        touch.x = shadow.x.toInt()
        touch.y = shadow.y.toInt()
        // Set the width of the shadow to half the width of the original View.
        val width: Int = (view.width * 1.4).toInt()

        // Set the height of the shadow to half the height of the original View.
        val height: Int = (view.height * 1.4).toInt()

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the
        // same as the Canvas that the system provides. As a result, the drag shadow
        // fills the Canvas.
//        shadow.setBounds(0, 0, width, height)

        // Set the size parameter's width and height values. These get back to
        // the system through the size parameter.
        size.set(width, height)

        // Set the touch point's position to be in the middle of the drag shadow.
        touch.set(width / 2, height / 2)
    }

    override fun onDrawShadow(canvas: Canvas?) {
        // Draw the ColorDrawable on the Canvas passed in from the system.
        canvas?.let { cv ->
            shadow.draw(cv)
        }
    }
}