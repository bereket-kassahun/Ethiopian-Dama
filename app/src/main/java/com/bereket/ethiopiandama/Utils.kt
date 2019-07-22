package com.bereket.ethiopiandama

import android.util.Log
import android.widget.Toast




class alpha{
    var alpha: Int = 0
}
class beta{
    var beta: Int = 0
}


object Utils {
    val dp_height = MainActivity.height.toFloat()
    val dp_width = MainActivity.width.toFloat()
    val VERTICAL_INCREASE = dp_height / 2 - 100
    val HORIZONTAL_INCREASE = dp_width / 2 - 100
    val positions = floatArrayOf(0f,0f,HORIZONTAL_INCREASE,0f,2*HORIZONTAL_INCREASE,0f,
        0f,VERTICAL_INCREASE,HORIZONTAL_INCREASE,VERTICAL_INCREASE,2*HORIZONTAL_INCREASE,VERTICAL_INCREASE,
        0f,2*VERTICAL_INCREASE,HORIZONTAL_INCREASE,2*VERTICAL_INCREASE,2*HORIZONTAL_INCREASE,2*VERTICAL_INCREASE)

    val _AiPlayer = 1
    val _Player1 = 2
    val _player2 = 3

    fun movePlayer(board: MainActivity.Board, index_1: Int, index_2: Int, turn: Int){
        board.positions[index_1] = 0
        board.positions[index_2] = turn
    }
    fun checkMove(board :MainActivity.Board,index_1 :Int,index_2 :Int,turn :Int):Boolean{
//        Log.e("board", board.toString())
        if(board.positions[index_1] == turn && board.positions[index_2] == 0){
            if(((index_1 - index_2 == 3 || index_1 - index_2 == -3) || (index_1 - index_2 == 1 || index_1 - index_2 == -1)) && ((index_1 % 2 == 0 && index_2 % 2 != 0) || (index_2 % 2 == 0 && index_1 % 2 != 0))){
                if((index_1 == 2 && index_2 == 3) || (index_1 == 3 && index_2 == 2) || (index_1 == 5 && index_2 == 6) || (index_1 == 6 && index_2 == 5)){
                    return false
                }
                return true
            }
            if((index_1 == 0 && index_2 == 4) || (index_1 == 4 && index_2 == 0) || (index_1 == 2 && index_2 == 4) || (index_1 == 4 && index_2 == 2) ||
                (index_1 == 4 && index_2 == 6) || (index_1 == 6 && index_2 == 4) || (index_1 == 4 && index_2 == 8) || (index_1 == 8 && index_2 == 4)){
                return true
            }
        }
        return false
    }
    fun checkWin(board :MainActivity.Board, turn: Int): Int{
        if(board.positions[0].equals(board.positions[4]) && board.positions[0].equals(board.positions[8]) && board.positions[0].equals(turn)){
            return turn;
        }
        if(board.positions[2].equals(board.positions[4]) && board.positions[2].equals(board.positions[6]) && board.positions[2].equals(turn)){
            return turn;
        }
        if(board.positions[1].equals(board.positions[4]) && board.positions[1].equals(board.positions[7]) && board.positions[1].equals(turn)){
            return turn;
        }
        if(board.positions[3].equals(board.positions[4]) && board.positions[3].equals(board.positions[5]) && board.positions[3].equals(turn)){
            return turn;
        }
        return 0
    }
    fun getBestMove(board: MainActivity.Board, turn: Int, depth: Int, alpha: Int, beta: Int): Move{
        var alph = alpha
        var bet = beta
        val check = checkWin(board,turn)
        when(check){
            1 -> return Move(10)
            2 -> return Move(-10)
        }
        if(depth == 0){
            return Move(-1)
        }
        var moves = ArrayList<Move>()
        for(i in 0..8){
            for(j in 0..8){
                if(checkMove(board,i,j,turn)){
                    var move = Move()
                    move.index_1 = i
                    move.index_2 = j
                    movePlayer(board, i, j, turn)

                    if(turn == _AiPlayer){
                        var bestVal = -100000
                        move.score = getBestMove(board, _Player1, depth - 1, alph, bet).score
                        bestVal = Math.max(bestVal, move.score)
                        alph = Math.max(alph, bestVal)
                        movePlayer(board, j, i, turn)
                        moves.add(move)
                        if (bet <= alph)
                            break
                    }
                    else{
                        var bestVal = 100000
                        move.score = getBestMove(board, _AiPlayer, depth - 1, alph, bet).score
                        bestVal = Math.min(bestVal, move.score)
                        bet = Math.min(bet, bestVal)
                        movePlayer(board, j, i, turn)
                        moves.add(move)
                        if (bet <= alph)
                            break
                    }

                }
            }
        }
        var _bestMove = 0
        if(moves.size == 0){
            return Move(-100)
        }
        if(turn == _AiPlayer){
            var _bestScore = -1000
            for(i in 0..moves.size-1){
                if(moves.get(i).score > _bestScore){
                    _bestScore = moves.get(i).score
                    _bestMove = i
                }
            }
        }else if(turn == _Player1){
            var _bestScore = 10000
            for(i in 0..moves.size-1){
                if(moves.get(i).score < _bestScore){
                    _bestScore = moves.get(i).score
                    _bestMove = i
                }
            }
        }

        return moves.get(_bestMove)
    }

    fun makeAiMove(board: MainActivity.Board, boxes: Array<Box>,turn: Turn,depth: Int){
        var move = getBestMove(board, turn.turn, depth,-1000000, 10000000)
        animate(board, boxes, move.index_1, move.index_2, turn)
    }

    fun animate(board: MainActivity.Board, boxes: Array<Box>,index_1: Int, index_2: Int, turn: Turn){

        for(i in boxes){
            if(i.index == index_1){
                i.animator(positions[2* index_2], positions[2* index_2 + 1],board,turn,Mode.SINGLE_PLAYER)
            }
        }
    }
    class Move{
        var index_1 = 0
        var index_2 = 0
        var score = 0
        constructor(){

        }
        constructor(score: Int){
            this.score = score
        }

    }
}