package com.bereket.ethiopiandama

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import java.lang.StringBuilder
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.Image
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bereket.ethiopiandama.Utils.positions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds

import java.util.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




enum class Mode{
    SINGLE_PLAYER,
    MULTI_PLAYER
}
class Turn{
    var turn: Int = 0
}
class MainActivity : Activity(){
    lateinit var mode:Mode
    var turn:Turn = Turn()
    lateinit var temp: Box
    lateinit var other: Box
    var movementX: Float = 0f
    var movementY: Float = 0f
    var pointX: Float = 0f
    var pointY: Float = 0f

    var point2X: Float = 0f
    var point2Y: Float = 0f

    var h = 0
    var w = 0

    var gameOverTitle:TextView? = null

    public fun changeTitle(text: String){
        gameOverTitle?.text = text
    }


    companion object{
        var counters:Int = 0
        var boards = ArrayList<Board>()
        lateinit var box: Array<Box>
        var board: Board = Board()

        var height = 0
        var width = 0
        var difficulty:Int = 0


        var builder: AlertDialog.Builder? = null

        fun changeTurn(mode: Mode, turn: Turn, boxes: Array<Box> = box, b: Board = board){
            if(0 != Utils.checkWin(board,turn.turn)){
                val winner = Utils.checkWin(board, turn.turn)
                val alertDialog = builder?.create()
                alertDialog?.setCancelable(false)
                alertDialog?.show()

                return
            }
            if(mode == Mode.SINGLE_PLAYER){
                if(turn.turn == 1){
                    turn.turn = 2
                }else if(turn.turn == 2){
                    turn.turn = 1
                    Utils.makeAiMove(b,boxes,turn, difficulty)
                    Log.e("difficulty", " "+difficulty)
                }
            }
            if(mode == Mode.MULTI_PLAYER){
                turn.turn = if(turn.turn == 2) 3 else 2
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {


            val action: Int? = event?.action

            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    //determine which box was touched
                    if(turn.turn == 1){
                        temp = other
                        return false
                    }

                    if(turn.turn == 2){
//                        if()
                        for(i in 3..5){
                            if(((box[i].x < event.x &&  box[i].width.plus(box[i].x) > event.x)) && (box[i].y < event.y &&  box[i].width.plus(box[i].y) > event.y)){
                                temp = box[i]

//                            Toast.makeText(this, " " + temp.x, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    if(turn.turn == 3){
                        for(i in 0..2){
                            if(((box[i].x < event.x &&  box[i].width.plus(box[i].x) > event.x)) && (box[i].y < event.y &&  box[i].width.plus(box[i].y) > event.y)){
                                temp = box[i]
//                            Toast.makeText(this, " " + temp.x, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
//                    for(b: Box in box) {
//                        if(((b.x < event.x &&  b.width.plus(b.x) > event.x)) && (b.y < event.y &&  b.width.plus(b.y) > event.y)){
//                            temp = b
////                            Toast.makeText(this, " " + temp.x, Toast.LENGTH_SHORT).show()
//                        }


//                    }
                    pointX = event.x
                    pointY = event.y

//                Toast.makeText(context, event.x.toString() + ' ' + event.y.toString(), Toast.LENGTH_SHORT).show()
                }
                MotionEvent.ACTION_UP -> {

//                    Toast.makeText(this, " " + temp.x, Toast.LENGTH_SHORT).show()
                    point2X = event.x
                    point2Y = event.y

                    var proceed = false
                    for(i in 0..5){
//                        Toast.makeText(this,"heloo", Toast.LENGTH_SHORT).show()
                        try {
                            if(temp.equals(box[i]))
                                proceed = true
                        }catch (e: UninitializedPropertyAccessException){
                            proceed = false
                            e.printStackTrace()
                        }

                    }
                    if(proceed){
//                        Toast.makeText(this,"heloo", Toast.LENGTH_SHORT).show()

                        temp.customAnimator(temp.calculateAngle((point2X - pointX).toDouble(), (point2Y - pointY).toDouble()), board, turn, mode)
                        temp = other
                    }

                }
            }
                return false
        }
//        Toast.makeText(this, "i was called", Toast.LENGTH_SHORT).show()
//        return super.onTouchEvent(event)

    private val mAppUnitId: String = "ca-app-pub-6470629710309760~6150625555"


    private val mInterstitialAdUnitId: String  = "ca-app-pub-6470629710309760/1080958245"

    private lateinit var mInterstitialAd: InterstitialAd


    private fun initializeInterstitialAd(appUnitId: String) {

        MobileAds.initialize(this, appUnitId)

    }

    private fun loadInterstitialAd(interstitialAdUnitId: String) {

        mInterstitialAd.adUnitId = interstitialAdUnitId
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun runAdEvents() {

        mInterstitialAd.adListener = object : AdListener() {

            // If user clicks on the ad and then presses the back, s/he is directed to DetailActivity.
            override fun onAdClicked() {
                super.onAdOpened()
                mInterstitialAd.adListener.onAdClosed()
            }

            // If user closes the ad, s/he is directed to DetailActivity.
            override fun onAdClosed() {
                startActivity(Intent(this@MainActivity, ManuActivity::class.java))
                finish()
            }
        }
    }

    var m = 1
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.board)

        //ad section


        mInterstitialAd = InterstitialAd(this)

        initializeInterstitialAd(mAppUnitId)

        loadInterstitialAd(mInterstitialAdUnitId)

        runAdEvents()





        m = intent.getIntExtra("mode", 1);
        difficulty = intent.getIntExtra("difficulty", 1)

        if(m == 1)
            mode = Mode.SINGLE_PLAYER
        else
            mode = Mode.MULTI_PLAYER
        
        init()

//        ObjectAnimator.ofFloat(box, View.X, box.x, box.x + 100f).apply {
//            duration = 1000
//            start()
//        }


        // drawing the lines

        //this is where the ai code should go (using the minimax, remember to )



    }
    class Board{
        var positions = intArrayOf(0,0,0,0,0,0,0,0,0)
        override fun toString(): String {
            val builder: StringBuilder = StringBuilder()
            for(i in 0..8){
                builder.append(positions[i])
                builder.append(" ")
            }
            return builder.toString()
        }

    }

    var mBackPressed:Long = 0
    val TIME_INTERVAL = 300
    override fun onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            return
        } else {
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.alert_dialog, null)
            val undo = dialogView.findViewById<Button>(R.id.undo)

            val replay = dialogView.findViewById<Button>(R.id.replay)
            val menu = dialogView.findViewById<Button>(R.id.mainMenu)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
            undo.setOnClickListener{
                counters += 1
                undoGame()
                alertDialog.cancel()
            }
            replay.setOnClickListener{
                replayGame()
                alertDialog.cancel()

            }
            menu.setOnClickListener{
                alertDialog.cancel()
                goToMenu()
            }

        }
        mBackPressed = System.currentTimeMillis()
    }

    private fun init(){
//        mode = Mode.MULTI_PLAYER


        builder = AlertDialog.Builder(this)
        val inflater = layoutInflater.inflate(R.layout.game_over,null)
        gameOverTitle = inflater.findViewById<TextView>(R.id.title)
//        winnerIndicator = inflater.findViewById<TextView>(R.id.title)
        val replay = inflater.findViewById<ImageView>(R.id.replay)
        val menu = inflater.findViewById<ImageView>(R.id.menu)
        replay.setOnClickListener {
            replayGame()
        }
        menu.setOnClickListener {
            goToMenu()
        }

        builder?.setView(inflater)



        if(mode == Mode.SINGLE_PLAYER){
            turn.turn = 2
            for(i in 0..8){
                if(i < 3){
                    board.positions[i] = 1
                }else if(i < 6){
                    board.positions[i] = 0
                }else{
                    board.positions[i] = 2
                }
            }
        }
        else{
            turn.turn = 2
            for(i in 0..8){
                if(i < 3){
                    board.positions[i] = 3
                }else if(i < 6){
                    board.positions[i] = 0
                }else{
                    board.positions[i] = 2
                }
            }
        }
        val temporary = Board()
        for(i in 0..8){
            temporary.positions[i] = board.positions[i]
        }
        boards.add(temporary)

        var temp: Box = Box(baseContext, 1000f, 1000f)
        other = Box(baseContext, 1000000f,10000f)
        box = Array(6,init = {Box(this, 1000f, 1000f)})
        val display = windowManager.defaultDisplay

        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)



        val dpHeight = outMetrics.heightPixels
        val dpWidth = outMetrics.widthPixels

        height = dpHeight
        width = dpWidth

        w = dpWidth
        h = dpHeight

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint)




        val rect1 = Rect(50, 50, 150, 150)
        val rect2 = Rect(dpWidth/2-50, 50, dpWidth+50, 150)
        val rect3 = Rect(dpWidth-150, 50, dpWidth-50, 150)
        val rect4 = Rect(50, dpHeight-150, 150, dpHeight-50)
        val rect5 = Rect(dpWidth/2-50, dpHeight-150, dpWidth/2+50, dpHeight-50)
        val rect6 = Rect(dpWidth-150, dpHeight-150, dpWidth-50, dpHeight-50)
        val paint = Paint()
        paint.setARGB(255, 255, 0, 0)

        val paint1 = Paint()
        paint1.setARGB(255,0,0,255)

        val line_1 = Line(baseContext,100f,100f,(dpWidth - 100).toFloat(), (dpHeight-100).toFloat())
        val line_2 = Line(baseContext,(dpWidth/2).toFloat(),100f,(dpWidth/2).toFloat(), (dpHeight-100).toFloat())
        val line_3 = Line(baseContext,(dpWidth-100).toFloat(),100f,100f, (dpHeight-100).toFloat())
        val line_4 = Line(baseContext, 50f, (dpHeight/2).toFloat(),(dpWidth-50).toFloat(),(dpHeight/2).toFloat())
        constraintLayout.addView(line_1)
        constraintLayout.addView(line_2)
        constraintLayout.addView(line_3)
        constraintLayout.addView(line_4)

        box[0] = Box(this, rect1, paint, dpWidth.toFloat(), dpHeight.toFloat(),0f, 0f,0)
        box[1] = Box(this, rect1, paint, dpWidth.toFloat(), dpHeight.toFloat(),dpWidth/2-100f, 0f,1)
        box[2] = Box(this, rect1, paint, dpWidth.toFloat(), dpHeight.toFloat(),dpWidth-200f, 0f,2)
        box[3] = Box(this, rect1, paint1, dpWidth.toFloat(), dpHeight.toFloat(), 0f, dpHeight-200f,6)
        box[4] = Box(this, rect1, paint1, dpWidth.toFloat(), dpHeight.toFloat(),dpWidth/2-100f, dpHeight-200f,7)
        box[5] = Box(this, rect1, paint1, dpWidth.toFloat(), dpHeight.toFloat(),dpWidth-200f, dpHeight-200f,8)


//        val lp = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//        lp.setMargins(10,20,0,0)
//        box.layoutParams = lp
        val parameter = ViewGroup.LayoutParams(200,200)
        constraintLayout.addView(box[0],parameter)
        constraintLayout.addView(box[1],parameter)
        constraintLayout.addView(box[2],parameter)
        constraintLayout.addView(box[3],parameter)
        constraintLayout.addView(box[4],parameter)
        constraintLayout.addView(box[5],parameter)

        Box.setInterface(object : Box.FromMainAcitivity {
            override fun changeGameOverTitle(turn: Turn) {
                var myText = ""
                if(mode == Mode.SINGLE_PLAYER){
                    if(turn.turn == 1){
                        myText = "You Lost"
                        gameOverTitle?.setTextColor(Color.RED)
                    }else{
                        myText = "You Won"
                        gameOverTitle?.setTextColor(Color.BLUE)
                    }
                }else{
                    if(turn.turn == 2){
                        myText = "Blue Team Won"
                        gameOverTitle?.setTextColor(Color.BLUE)
                    }
                    else{
                        myText = "Red Team Won"
                        gameOverTitle?.setTextColor(Color.RED)
                    }

                }
                gameOverTitle?.text = myText
            }
        })

    }
    private fun replayGame() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("difficulty", difficulty )
        intent.putExtra("mode", m)
        startActivity(intent)
        finish()
    }

    private fun goToMenu() {
        if(mInterstitialAd.isLoaded)
            mInterstitialAd.show()
        else{
            val intent = Intent(this, ManuActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun undoGame() {
//        Toast.makeText(this, "undo", Toast.LENGTH_SHORT).show()
   Log.e("size", boards.size.toString())
        if((boards.size - counters) <= 0 || counters < 0)
            return
        if(turn.turn == 1)
            return
        val b = Board()
        for(i in 0..8){
            b.positions[i] = boards.get(boards.size- counters-1).positions[i]
        }
        board = b
        var lastBoard = boards.get(boards.size - counters)
        for(i in 1..counters){
            boards.removeAt(boards.size-i)
        }
        counters = 0
        moveBoxes(b,lastBoard)

    }




    private fun positionBox(box: Box?, e: Int){
        box?.x =positions[e*2]
        box?.y = positions[e*2+1]
        box?.index = e

//        box!!.animator(positions[e*2],positions[e*2+1],board,turn,mode)
//        Log.e("------------",  " "+ positions[e*2] )
//        Log.e("------------",  " "+ positions[e*2+1] )
    }
    private fun moveBoxes(b: Board, lastBoard: Board){
        var init1:Int = 0
        var init2:Int = 0
        var final1:Int = 0
        var final2:Int = 0

        if(mode == Mode.MULTI_PLAYER){
            turn.turn = if(turn.turn == 2) 3 else 2
        }
        for(i in 0..8){
            if(lastBoard.positions[i] != b.positions[i] ){
                if(lastBoard.positions[i] == 1 || lastBoard.positions[i] == 3)
                    init1 = i
                else if(lastBoard.positions[i] == 2)
                    init2 = i
                if(b.positions[i] == 1 || b.positions[i] == 3)
                    final1 = i
                else if(b.positions[i] == 2)
                    final2 = i
            }

        }

        for(i in 0..boards.size-1){
            Log.e("eeeeee", boards.get(i).toString())
        }

        positionBox(findBox(init2), final2)

        positionBox(findBox(init1), final1)


    }
    private fun findBox(index: Int):Box?{
        for(b in box){
            if (b.index == index)
                return b
        }
        return null
    }
}

