package com.bereket.ethiopiandama

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class Line(context: Context?): View(context) {
    val points = floatArrayOf(0f,0f,0f,0f)
    constructor(context: Context?,start_x :Float,start_y: Float, end_x: Float, end_y :Float ):this(context){
        points[0] = start_x
        points[1] = start_y
        points[2] = end_x
        points[3] = end_y
    }
    val paint: Paint = Paint().apply {
        this.strokeWidth = 10.0f
        this.color = Color.rgb(34,177,76)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawLine(points[0],points[1],points[2],points[3],paint)
    }
}