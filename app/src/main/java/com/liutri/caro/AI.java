package com.liutri.caro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import components.Cell;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class AI extends AppCompatImageView {

    Activity activity;
    private int width;
    private int height;
    public int numColumns;
    public int numRows;
    private int column;
    private int row;
    private int cellSize;
    private Paint blackPaint = new Paint();
    private Bitmap bitmapX;
    private Bitmap bitmapO;
    private Cell[][] cells;
    private int curPlayer;
    private boolean ready = false;
    private Stack<Cell> stackMoves;
    private Stack<Cell> stackUndos;
    public int MAX_INT = Integer.MAX_VALUE;
    public int MIN_INT = Integer.MIN_VALUE;
    private ImageButton btnUndo;
    private ImageButton btnRedo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AI(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = getActivity();
        initComponents();
    }
    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initComponents(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        numColumns = 13;
        numRows = 13;
        cellSize = width/numRows;
        width = numColumns*cellSize;
        height = width;

        curPlayer = 2;
        ready=true;
        stackMoves = new Stack<>();
        stackUndos = new Stack<>();

        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        bitmapX = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        bitmapO = BitmapFactory.decodeResource(getResources(), R.drawable.o);

        cells = new Cell[numRows][numColumns];
        for (int i = 0; i < numRows; i++)
            for (int j = 0; j < numColumns; j++)
                cells[i][j] = new Cell();

        for (int i = 0; i < numRows; i++)
            for (int j = 0; j < numColumns; j++) {
                cells[i][j] = new Cell(i, j, j * cellSize, i * cellSize, 0, "");
            }

        int r = ThreadLocalRandom.current().nextInt(4, 7 + 1);
        int c = ThreadLocalRandom.current().nextInt(4, 7 + 1);
        stackMoves.add(new Cell(r,c,c*cellSize,r*cellSize,1,""));
        cells[r][c].setOwner(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (numColumns == 0 || numRows == 0) return;

        for (Cell c: stackMoves) {
            if (c.getOwner() == 1){
                canvas.drawBitmap(bitmapX, c.getX() + 5, c.getY() + 5, blackPaint);
            } else if (c.getOwner() == 2){
                canvas.drawBitmap(bitmapO, c.getX() + 5, c.getY() + 5, blackPaint);
            }
        }

        for (int i = 0; i <= numColumns; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, height, blackPaint);
        }

        for (int i = 0; i <= numRows; i++) {
            canvas.drawLine(0, i * cellSize, width, i * cellSize, blackPaint);
        }

        btnUndo = activity.findViewById(R.id.buttonUndo);
        btnRedo = activity.findViewById(R.id.buttonRedo);

        if (stackMoves.size() == 1) btnUndo.setEnabled(false);
        else btnUndo.setEnabled(true);
        if (stackUndos.isEmpty()) btnRedo.setEnabled(false);
        else btnRedo.setEnabled(true);

        btnUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stackMoves.size() == 1) return;
                Cell c = stackMoves.pop();
                cells[c.getRow()][c.getCol()].setOwner(0);
                stackUndos.add(c);
                c = stackMoves.pop();
                cells[c.getRow()][c.getCol()].setOwner(0);
                stackUndos.add(c);
                invalidate();
                ready = true;
            }
        });
        btnRedo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stackUndos.isEmpty()) return;
                Cell c = stackUndos.pop();
                cells[c.getRow()][c.getCol()].setOwner(2);
                stackMoves.add(c);
                c = stackUndos.pop();
                cells[c.getRow()][c.getCol()].setOwner(1);
                stackMoves.add(c);
                invalidate();
                ready = true;
            }
        });

        @SuppressLint("DrawAllocation") Intent myIntent = new Intent(activity, GameoverActivity.class);
        switch (checkWin(cells)){
            case 0:
                myIntent.putExtra("gamestatus", "HÒA!");
                activity.startActivity(myIntent);
                ready = false;
                break;
            case 1:
                myIntent.putExtra("gamestatus", "BẠN THUA!");
                activity.startActivity(myIntent);
                ready = false;
                break;
            case 2:
                myIntent.putExtra("gamestatus", "BẠN THẮNG!");
                activity.startActivity(myIntent);
                ready = false;
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            column = (int) (event.getX() / cellSize);
            row = (int) (event.getY() / cellSize);

            if (event.getX() > width || event.getY() > height || !ready || row>=numRows || column>=numColumns)
                return false;
            if (cells[row][column].getOwner() == 0 && curPlayer==2) {
                cells[row][column].setOwner(2);
                Cell c = new Cell(row,column,column*cellSize,row*cellSize,2,"");
                stackMoves.add(c);
                invalidate();
                curPlayer = 1;
                try {
                    Move move = findBestMove(cells);
                    System.out.printf("ROW: %d COL: %d\n\n", move.row, move.col);
                    cells[move.row][move.col].setOwner(1);
                    c = new Cell(move.row,move.col,move.col*cellSize,move.row*cellSize,1,"");
                    stackMoves.add(c);
                    invalidate();
                    curPlayer = 2;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    ////////////////////////////////
    static class Move {
        int row, col;
    };

    private int checkWin(Cell[][] board) {

        int count = 0;
        /////Check Ngang/////////////////////////////////////////////////
        for (int indexRow = 0; indexRow < numRows; indexRow++) {
            for (int indexCol = 0; indexCol < numColumns - 4; indexCol++) {
                int curOwner = board[indexRow][indexCol].getOwner();
                for (int i = 1; i < 5; i++) {
                    if (board[indexRow][indexCol + i].getOwner() == curOwner && curOwner != 0) {
                        count++;
                    } else {
                        count = 0;
                        break;
                    }
                }
                if (count == 4 && curOwner == 1) {
                    return 1;
                } else if (count == 4 && curOwner == 2) {
                    return 2;
                }
            }
        }
        /////Check Doc/////////////////////////////////////////////////////////////////////
        for (int indexCol = 0; indexCol < numColumns; indexCol++) {
            for (int indexRow = 0; indexRow < numRows - 4; indexRow++) {
                int curOwner = board[indexRow][indexCol].getOwner();
                for (int i = 1; i < 5; i++) {
                    if (board[indexRow + i][indexCol].getOwner() == curOwner && curOwner != 0) {
                        count++;
                    } else {
                        count = 0;
                        break;
                    }
                }
                if (count == 4 && curOwner == 1) {
                    return 1;
                } else if (count == 4 && curOwner == 2) {
                    return 2;
                }
            }
        }
        /////Check Cheo Sac/////////////////////////////////////////////////////////////////////
        for (int indexRow = 4; indexRow < numRows; indexRow++) {
            for (int indexCol = 0; indexCol < numColumns - 4; indexCol++) {
                int curOwner = board[indexRow][indexCol].getOwner();
                for (int i = 1; i < 5; i++) {
                    if (board[indexRow - i][indexCol + i].getOwner() == curOwner && curOwner != 0) {
                        count++;
                    } else {
                        count = 0;
                        break;
                    }
                }
                if (count == 4 && curOwner == 1) {
                    return 1;
                } else if (count == 4 && curOwner == 2) {
                    return 2;
                }
            }
        }
        /////Check Cheo Huyen/////////////////////////////////////////////////////////////////////
        for (int indexCol = 0; indexCol < numColumns - 4; indexCol++) {
            for (int indexRow = 0; indexRow < numRows - 4; indexRow++) {
                int curOwner = board[indexRow][indexCol].getOwner();
                for (int i = 1; i < 5; i++) {
                    if (board[indexRow + i][indexCol + i].getOwner() == curOwner && curOwner != 0) {
                        count++;
                    } else {
                        count = 0;
                        break;
                    }
                }
                if (count == 4 && curOwner == 1) {
                    return 1;
                } else if (count == 4 && curOwner == 2) {
                    return 2;
                }
            }
        }
        if(!isMovesLeft(board)) return 0;
        return -1;
    }


    private Boolean isMovesLeft(Cell[][] board)
    {
        for (int i = 0; i < numRows; i++)
            for (int j = 0; j < numColumns; j++)
                if (board[i][j].getOwner() == 0)
                    return true;
        return false;
    }

    private int bangdiem(int playerX, int playerO){
        int score = 0;
        switch (playerX){
            case 1 : score += 5; break;
            case 2 : score += 10; break;
            case 3 : score += 20; break;
            case 4 : score += 50; break;
            case 5 : score += 100; break;
            default: score += 0;
        }
        switch (playerO){
            case 1 : score -= 2; break;
            case 2 : score -= 4; break;
            case 3 : score -= 15; break;
            case 4 : score -= 40; break;
            case 5 : score -= 90; break;
            default: score -= 0;
        }
        return score;
    }
    private int evaluate(Cell[][] board){
        int _X = 0; int _O = 0;
        int score = 0; int currentPlayer;

        // cheo sac
        int t=5;
        for (int i = 4; i < numRows; i++){
            for (int k=0; k < t; k++){
                currentPlayer = board[i-k][k].getOwner();
                if (currentPlayer == 1) {
                    _X++; _O=0;
                }  else if (currentPlayer == 2){
                    _O++; _X=0;
                } else {
                    score += bangdiem(_X, _O);
                    _X=0; _O=0;
                }
            }
            score += bangdiem(_X, _O);
            _X=0; _O=0;
            t++;
        }
        t=12;
        for (int j = 1; j < numColumns-4; j++){
            for (int k=0; k < t; k++){
                currentPlayer = board[numRows-1-k][j+k].getOwner();
                if (currentPlayer == 1) {
                    _X++; _O=0;
                }  else if (currentPlayer == 2){
                    _O++; _X=0;
                } else {
                    score += bangdiem(_X, _O);
                    _X=0; _O=0;
                }
            }
            score += bangdiem(_X, _O);
            _X=0; _O=0;
            t--;
        }

        // cheo huyen
        t=5;
        for (int i = numRows-5; i >= 0; i--){
            for (int k=0; k < t; k++){
                currentPlayer = board[i+k][k].getOwner();
                if (currentPlayer == 1) {
                    _X++; _O=0;
                }  else if (currentPlayer == 2){
                    _O++; _X=0;
                } else {
                    score += bangdiem(_X, _O);
                    _X=0; _O=0;
                }
            }
            score += bangdiem(_X, _O);
            _X=0; _O=0;
            t++;
        }
        t=12;
        for (int j = 1; j < numColumns-4; j++){
            for (int k=0; k < t; k++){
                currentPlayer = board[k][j+k].getOwner();
                if (currentPlayer == 1) {
                    _X++; _O=0;
                }  else if (currentPlayer == 2){
                    _O++; _X=0;
                } else {
                    score += bangdiem(_X, _O);
                    _X=0; _O=0;
                }
            }
            score += bangdiem(_X, _O);
            _X=0; _O=0;
            t--;
        }

        // ngang
        for (int i = 0; i < numRows; i++){
            for (int k=0; k < numColumns; k++){
                currentPlayer = board[i][k].getOwner();
                if (currentPlayer == 1) {
                    _X++; _O=0;
                }  else if (currentPlayer == 2){
                    _O++; _X=0;
                } else {
                    score += bangdiem(_X, _O);
                    _X=0; _O=0;
                }
            }
            score += bangdiem(_X, _O);
            _X=0; _O=0;
        }

        // doc
        for (int i = 0; i < numColumns; i++){
            for (int k=0; k < numRows; k++){
                currentPlayer = board[k][i].getOwner();
                if (currentPlayer == 1) {
                    _X++; _O=0;
                }  else if (currentPlayer == 2){
                    _O++; _X=0;
                } else {
                    score += bangdiem(_X, _O);
                    _X=0; _O=0;
                }
            }
            score += bangdiem(_X, _O);
            _X=0; _O=0;
        }

        return score;
    }

    private int minimax(Cell[][] board, int depth, Boolean isMax)
    {
        int score = evaluate(board);

        // If Maximizer has won the game
        // return his/her evaluated score
        if (checkWin(board) == 1)
            return 1000;

        // If Minimizer has won the game
        // return his/her evaluated score
        if (checkWin(board) == 2)
            return -1000;

        // If there are no more moves and
        // no winner then it is a tie
        if (!isMovesLeft(board)) return 0;

        if (depth==1) return score;

        // If this maximizer's move
        if (isMax)
        {
            int best = MIN_INT;

            // Traverse all cells
            for (int i = 0; i < numRows; i++)
            {
                for (int j = 0; j < numColumns; j++)
                {
                    // Check if cell is empty
                    if (board[i][j].getOwner()==0)
                    {
                        // Make the move
                        board[i][j].setOwner(1);

                        // Call minimax recursively and choose
                        // the maximum value
                        best = Math.max(best, minimax(board, depth + 1, false));
                        // Undo the move
                        board[i][j].setOwner(0);
                    }
                }
            }
            return best;
        }

        // If this minimizer's move
        else
        {
            int best = MAX_INT;

            // Traverse all cells
            for (int i = 0; i < numRows; i++)
            {
                for (int j = 0; j < numColumns; j++)
                {
                    // Check if cell is empty
                    if (board[i][j].getOwner() == 0)
                    {
                        // Make the move
                        board[i][j].setOwner(2);

                        // Call minimax recursively and choose
                        // the minimum value
                        best = Math.min(best, minimax(board, depth + 1, true));
                        // Undo the move
                        board[i][j].setOwner(0);
                    }
                }
            }
            return best;
        }
    }

    private Move findBestMove(Cell[][] board)
    {
        int bestVal = MIN_INT;
        Move bestMove = new Move();
        bestMove.row = -1;
        bestMove.col = -1;

        // Traverse all cells, evaluate minimax function
        // for all empty cells. And return the cell
        // with optimal value.
        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numColumns; j++)
            {
                // Check if cell is empty
                if (board[i][j].getOwner() == 0)
                {
                    // Make the move
                    board[i][j].setOwner(1);

                    // compute evaluation function for this
                    // move.
                    int moveVal = minimax(board, 0, false);

                    // Undo the move
                    board[i][j].setOwner(0);

                    // If the value of the current move is
                    // more than the best value, then update
                    // best/
                    if (moveVal > bestVal)
                    {
                        bestMove.row = i;
                        bestMove.col = j;
                        bestVal = moveVal;
                    }
                }
            }
        }

        System.out.printf("The value of the best Move " + "is : %d\n\n", bestVal);

        return bestMove;
    }


    //    private int evaluate(Cell[][] board, int x, int y, int player){
//        int best=0;
//
//        //left right
//        if (y < numColumns-4){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x][y+i].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        //right left
//        if (y > 3){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x][y-i].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        //up down
//        if (x < numRows-4){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x+i][y].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        //down up
//        if (x > 3){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x-i][y].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        //cheo sac - up down
//        if (y > 3 && x < numRows-4){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x+i][y-i].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        //cheo sac - down up
//        if (y < numColumns-4 && x > 3){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x-i][y+i].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        //cheo huyen - up down
//        if (y < numColumns-4 && x < numRows-4){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x+i][y+i].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        //cheo huyen - down up
//        if (y > 3 && x > 3){
//            int count=0;
//            for (int i = 0; i < 5; i++){
//                if (board[x-i][y-i].getOwner() == player) count++;
//            }
//            if (count > best) best = count;
//        }
//
//        System.out.println("BEST = " + best + " | player = " + player);
//
//        return best;
//    }
}
