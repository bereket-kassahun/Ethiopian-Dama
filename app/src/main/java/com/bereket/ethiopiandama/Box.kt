package com.bereket.ethiopiandama


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log


import android.view.View


import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import java.util.*


fun View.spring(property: DynamicAnimation.ViewProperty): SpringAnimation{
    val key: Int = getKey(property)
    var springAnim = getTag(key) as? SpringAnimation
    springAnim = SpringAnimation(this, property)
    return springAnim
}
fun getKey(property: DynamicAnimation.ViewProperty): Int{
    when(property){
        DynamicAnimation.TRANSLATION_Y -> return 12345
        DynamicAnimation.TRANSLATION_X -> return 23456
    }
    return 0
}
class Box(context: Context?, dp_width: Float, dp_height: Float) : View(context) {

    var isRunning = false


    val VERTICAL_INCREASE = dp_height / 2 - 100
    val HORIZONTAL_INCREASE = dp_width / 2 - 100
    val ANIMATION_DURATION: Long = 500
    public var index = 0


    val positions = floatArrayOf(0f,0f,HORIZONTAL_INCREASE,0f,2*HORIZONTAL_INCREASE,0f,
        0f,VERTICAL_INCREASE,HORIZONTAL_INCREASE,VERTICAL_INCREASE,2*HORIZONTAL_INCREASE,VERTICAL_INCREASE,
        0f,2*VERTICAL_INCREASE,HORIZONTAL_INCREASE,2*VERTICAL_INCREASE,2*HORIZONTAL_INCREASE,2*VERTICAL_INCREASE)

    lateinit var rect: Rect
    lateinit var paint: Paint

    companion object{
        lateinit var fromMainAcitivity: FromMainAcitivity
        public fun setInterface(fromMainAcitivity: FromMainAcitivity){
            this.fromMainAcitivity = fromMainAcitivity
        }
    }

    interface FromMainAcitivity{
        public  fun changeGameOverTitle(turn: Turn)
    }

    constructor(
        context: Context?,
        rect: Rect,
        paint: Paint,
        dp_width: Float,
        dp_height: Float,
        position_x: Float,
        position_y: Float,
        index: Int
    ) : this(context, dp_width, dp_height) {
        this.rect = rect
        this.paint = paint
        this.x = position_x
        this.y = position_y
        this.index = index
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(rect.left.toFloat(),rect.top.toFloat(),rect.right.toFloat(),rect.bottom.toFloat(),paint)
    }



    public  fun calculateAngle(x: Double, y: Double): Double {

        return Math.toDegrees(Math.atan2(y, x))
    }

    class Move{
        var x: Float = 0f
        var y: Float = 0f
        var distance: Double = 0.0
    }


    public fun customAnimator(angle: Double, board: MainActivity.Board, turn: Turn, mode: Mode) {

        val position_x = this.x
        val position_y = this.y
        var moves: ArrayList<Move> = ArrayList<Move>()
        if(isRunning)
            return

        //top right
        if (20.00 < angle && angle < 70.00) {
            animator(position_x + HORIZONTAL_INCREASE,position_y + VERTICAL_INCREASE,board, turn, mode)
        }
        //top
        else if ( 70 < angle && angle < 110){
            animator(finalPoint2 = position_y + VERTICAL_INCREASE,board=board, turn= turn, mode = mode)
        }
        //top left
        else if (110 < angle && angle < 160){
            animator(position_x - HORIZONTAL_INCREASE , position_y + VERTICAL_INCREASE,board, turn, mode)
        }
        //left corner
        else if (angle < -160 || angle > 160){
            animator( position_x - HORIZONTAL_INCREASE,board=board, turn= turn, mode = mode)
        }
        //bottom left
        else if (-160 < angle && angle < -110){
            animator(position_x - HORIZONTAL_INCREASE, position_y - VERTICAL_INCREASE,board=board, turn= turn, mode = mode)
        }
        //bottom
        else if (-110 < angle && angle < -70){
            animator(finalPoint2 = position_y - VERTICAL_INCREASE,board=board, turn= turn, mode = mode)
        }
        //bottom right
        else if (-70 < angle && angle < -20){
            animator(position_x + HORIZONTAL_INCREASE, position_y - VERTICAL_INCREASE,board=board, turn= turn, mode = mode)
        }
        //right
        else if (-20 < angle && angle < 20) {
            animator(position_x + HORIZONTAL_INCREASE,board=board, turn= turn, mode = mode)
        }
    }
    public fun animator(finalPoint1: Float = this.x, finalPoint2: Float = this.y, board: MainActivity.Board, turn: Turn, mode: Mode){
        val lastx = this.x
        val lasty = this.y
        isRunning = true
        var changed = false
        var t = turn.turn

        //checking for boundaries of the screen
        if(finalPoint1 > 2*HORIZONTAL_INCREASE ||  finalPoint1 < 0 || finalPoint2 > 2*VERTICAL_INCREASE ||  finalPoint2 < 0) {
            isRunning = false
            return
        }

        fromMainAcitivity.changeGameOverTitle(turn)

        //checking if already the place is occupied
        for(i in 0..8) {
            if (finalPoint1 == positions[2 * i] && finalPoint2 == positions[2 * i + 1]) {
                if(board.positions[i] != 0){
                    isRunning = false
                    return
                }
            }
        }
        var go = false

        for(i in 1..7 step 2){
            if(lastx == positions[2*i] && lasty == positions[2*i+1]){
                for(j in 1..7 step 2){
                    if(finalPoint1 == positions[2*j] && finalPoint2 == positions[2*j+1]){
                        isRunning = false
                        return
                    }

                }
            }
        }

        //modifying board
        for(i in 0..17 step 2){
            if(finalPoint1 == positions[i] && finalPoint2 == positions[i+1]){
                board.positions[i/2] = t
                index = i/2
            }
        }

        for(i in 0..17 step 2){
            if(lastx == positions[i] && lasty == positions[i+1])
                board.positions[i/2] = 0
        }

        val tempo: MainActivity.Board = MainActivity.Board()
        for(i in 0..8){
            tempo.positions[i] = board.positions[i]
        }


        MainActivity.counters = 0


        if(turn.turn == 1 )
            MainActivity.boards.add(tempo)

        if(mode == Mode.MULTI_PLAYER){
            MainActivity.boards.add(tempo)
        }

        for(i in 0..MainActivity.boards.size-1){
            Log.e("rrrrrrrr", MainActivity.boards.get(i).toString())
        }


        val animation_1 = this.spring(SpringAnimation.TRANSLATION_X)
        animation_1.setStartVelocity(4f)

        animation_1.addEndListener{
                p0: DynamicAnimation<out DynamicAnimation<*>>?,
                p1: Boolean,
                p2: Float,
                p3: Float ->
            if(finalPoint1 != lastx)
                isRunning = false

            if(lastx != this.x && !changed){
                MainActivity.changeTurn(mode, turn)
                changed = true
            }

        }

        animation_1.animateToFinalPosition(finalPoint1)
        val animation_2 = this.spring(SpringAnimation.TRANSLATION_Y)
        animation_2.setStartVelocity(4f)
        animation_2.addEndListener{
                p0: DynamicAnimation<out DynamicAnimation<*>>?,
                p1: Boolean,
                p2: Float,
                p3: Float ->
            if(finalPoint2 != lasty)
                isRunning = false

            if(lasty != this.y && !changed){
                MainActivity.changeTurn(mode, turn)
                changed = true
            }
        }
        animation_2.animateToFinalPosition(finalPoint2)
    }
}
