package com.liutri.caro;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import components.Cell;
import components.Room;

public class Board extends AppCompatImageView {

    private int cellSize;
    private int numColumns, numRows;
    private Paint blackPaint = new Paint();
    private Paint paint = new Paint();
    private Bitmap bitmapX;
    private Bitmap bitmapO;
    private Cell[][] cells;
    public static int curPlayer=0;
    private boolean ready = false;
    private int width, height;
    private int column, row;
    private String status="waiting....";
    private boolean gameStarted = false;
    static Cell cellOUT;
    Boolean Joined=false;
    private int currentRow = -1;
    private int currentCol = -1;

    public Activity currentActivity;
    TextView txt;
    Button btnPlayagain;
    Button btnExit;
    TextView textViewTurn;

    public Socket s;
    private String ROOMID;
    private int PLAYERS;
    Room room;

    public Board(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        currentActivity = getActivity();
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
    private void initComponents(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        currentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = width;
        numColumns = 13;
        numRows = 13;
        cellSize = width/numRows;
        width = 13*cellSize;
        height = width;

        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        bitmapX = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        bitmapO = BitmapFactory.decodeResource(getResources(), R.drawable.o);

        cellOUT = new Cell();

        ROOMID = currentActivity.getIntent().getStringExtra("ROOMID");
        PLAYERS= currentActivity.getIntent().getIntExtra("PLAYERS", -1);
        if (PLAYERS==0) {
            curPlayer=1;
        }
        else if (PLAYERS==1) {
            curPlayer=2; ready = true;
        }

        cells = new Cell[numRows][numColumns];
        for (int i = 0; i < numRows; i++)
            for (int j = 0; j < numColumns; j++)
                cells[i][j] = new Cell();

        for (int i = 0; i < numRows; i++)
            for (int j = 0; j < numColumns; j++)
                cells[i][j] = new Cell(i, j, j * cellSize, i * cellSize, 0, "");

            new ClientTask().execute();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (cells[i][j].getOwner() == 1) {
                    canvas.drawBitmap(bitmapX, cells[i][j].getX() + 4, cells[i][j].getY() + 4, blackPaint);
                } else if (cells[i][j].getOwner() == 2) {
                    canvas.drawBitmap(bitmapO, cells[i][j].getX() + 4, cells[i][j].getY() + 4, blackPaint);
                }
            }
        }

        for (int i = 0; i <= numColumns; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, height, blackPaint);
        }

        for (int i = 0; i <= numRows; i++) {
            canvas.drawLine(0, i * cellSize, width, i * cellSize, blackPaint);
        }

        if (currentRow != -1){
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            int x = cells[currentRow][currentCol].getX();
            int y = cells[currentRow][currentCol].getY();
            canvas.drawRect(x, y, x+cellSize, y+cellSize, paint);
        }

        switch (checkWin()){
            case 0:
                currentActivity.setContentView(R.layout.gameover);
                txt = currentActivity.findViewById(R.id.playerWon);
                btnPlayagain = currentActivity.findViewById(R.id.btnPlayagain);
                btnExit = currentActivity.findViewById(R.id.btnExit);
                txt.setText("Hòa!");
                btnPlayagain.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initComponents();
                        Intent intent = new Intent(currentActivity.getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        currentActivity.startActivity(intent);
                        currentActivity.finish();
                    }
                });
                btnExit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            currentActivity.finishAffinity();
                        }
                        System.exit(0);
                    }
                });
                break;
            case 1:
                currentActivity.setContentView(R.layout.gameover);
                txt = currentActivity.findViewById(R.id.playerWon);
                btnPlayagain = currentActivity.findViewById(R.id.btnPlayagain);
                btnExit = currentActivity.findViewById(R.id.btnExit);
                if (curPlayer==1) txt.setText("Bạn thắng!");
                if (curPlayer==2) txt.setText("Bạn thua!");
                btnPlayagain.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initComponents();
                        Intent intent = new Intent(currentActivity.getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        currentActivity.startActivity(intent);
                        currentActivity.finish();
                    }
                });
                btnExit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            currentActivity.finishAffinity();
                        }
                        System.exit(0);
                    }
                });
                break;
            case 2:
                currentActivity.setContentView(R.layout.gameover);
                txt = currentActivity.findViewById(R.id.playerWon);
                btnPlayagain = currentActivity.findViewById(R.id.btnPlayagain);
                btnExit = currentActivity.findViewById(R.id.btnExit);
                if (curPlayer==1) txt.setText("Bạn thua!");
                if (curPlayer==2) txt.setText("Bạn thắng!");
                btnPlayagain.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initComponents();
                        Intent intent = new Intent(currentActivity.getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        currentActivity.startActivity(intent);
                        currentActivity.finish();
                    }
                });
                btnExit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            currentActivity.finishAffinity();
                        }
                        System.exit(0);
                    }
                });
                break;
        }

        if (!gameStarted){
            textViewTurn = currentActivity.findViewById(R.id.textViewTurn);
            if (curPlayer==1){
                textViewTurn.setText("Đang chờ đối thủ...");
            } else if (curPlayer==2){
                textViewTurn.setText("Đến lượt bạn");
                textViewTurn.setTextColor(Color.RED);
            }
            gameStarted = true;
        }

        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            column = (int) (event.getX() / cellSize);
            row = (int) (event.getY() / cellSize);

            if (event.getX() > numColumns * cellSize || event.getY() > numRows * cellSize || column >= numColumns || !ready)
                return false;

            if (cells[row][column].getOwner() == 0) {
                cells[row][column].setOwner(curPlayer);
                cells[row][column].setRow(row);
                cells[row][column].setCol(column);
                cells[row][column].setX(column*cellSize);
                cells[row][column].setY(row*cellSize);
                cells[row][column].setRoom(ROOMID);
                cellOUT = cells[row][column];

                currentRow = row;
                currentCol = column;

                textViewTurn = currentActivity.findViewById(R.id.textViewTurn);
                textViewTurn.setText("Đến lượt đối thủ");
                if (curPlayer == 1) textViewTurn.setTextColor(Color.RED);
                else if (curPlayer == 2) textViewTurn.setTextColor(Color.BLUE);
                invalidate();
                ready=false;
                WriteData writeData = new WriteData(s,cellOUT);
                writeData.start();
            }
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    private class ClientTask extends AsyncTask<Void, Cell, Cell> {
        @Override
        protected Cell doInBackground(Void... voids) {
            Cell cellIN = new Cell(0,0,0,0,0,"");
            room = new Room(ROOMID, curPlayer);
            try{
                s = GetData.socket;
                WriteData writeData = new WriteData(s,new Cell(0,0,0,0,-1, ROOMID));
                writeData.start();
                status = "Đã kết nối đến " + s;
                System.out.println(status);
                ObjectInputStream inputStream;
                while (s.isBound()){
                    inputStream = new ObjectInputStream(s.getInputStream());
                    Cell dataIN = (Cell) inputStream.readObject();
                    if (dataIN.getOwner() == 2) Joined = true;
                    if (dataIN.getRoom().equals(ROOMID) && Joined)
                        publishProgress(dataIN);
                }
            }
            catch(Exception e){
                status = e.getMessage();
                System.out.println("ERROR (Board): "+status);
            }
            return cellIN;
        }

        @Override
        protected void onProgressUpdate(Cell... values) {
            super.onProgressUpdate(values);

            int r = values[0].getRow();
            int c = values[0].getCol();
            cells[r][c].setOwner(values[0].getOwner());
            currentRow = r;
            currentCol = c;
            System.out.println(values[0].getRow()+","+values[0].getCol()+"|"+values[0].getOwner());
            textViewTurn = currentActivity.findViewById(R.id.textViewTurn);
            System.out.println(curPlayer+" | "+values[0].getOwner());
            if (curPlayer != values[0].getOwner()) {
                ready=true;
                textViewTurn.setText("Đến lượt bạn");
                if (curPlayer == 1) textViewTurn.setTextColor(Color.BLUE);
                else if (curPlayer == 2) textViewTurn.setTextColor(Color.RED);
            }
            invalidate();
        }
    }

    private int checkWin(){
        int count = 0;
        /////Check Ngang/////////////////////////////////////////////////
        for(int indexRow = 0; indexRow < numRows; indexRow++){
            for(int indexCol = 0; indexCol < numColumns-4; indexCol++){
                int curOwner = cells[indexRow][indexCol].getOwner();
                for(int i=1; i<5; i++){
                    if(cells[indexRow][indexCol+i].getOwner() == curOwner && curOwner != 0){
                        count++;
                    }
                    else{
                        count = 0;
                        break;
                    }
                }
                if(count==4 && curOwner==1){
                    return 1;
                }
                else if(count==4 && curOwner==2){
                    return 2;
                }
            }
        }
        /////Check Doc/////////////////////////////////////////////////////////////////////
        for(int indexCol = 0; indexCol < numColumns; indexCol++){
            for(int indexRow = 0; indexRow < numRows-4; indexRow++){
                int curOwner = cells[indexRow][indexCol].getOwner();
                for(int i=1; i<5; i++){
                    if(cells[indexRow+i][indexCol].getOwner() == curOwner && curOwner != 0){
                        count++;
                    }
                    else{
                        count = 0;
                        break;
                    }
                }
                if(count==4 && curOwner==1){
                    return 1;
                }
                else if(count==4 && curOwner==2){
                    return 2;
                }
            }
        }
        /////Check Cheo Sac/////////////////////////////////////////////////////////////////////
        for(int indexRow = 4; indexRow < numRows; indexRow++){
            for(int indexCol = 0; indexCol < numColumns-4; indexCol++){
                int curOwner = cells[indexRow][indexCol].getOwner();
                for(int i=1; i<5; i++){
                    if(cells[indexRow-i][indexCol+i].getOwner() == curOwner && curOwner != 0){
                        count++;
                    }
                    else{
                        count = 0;
                        break;
                    }
                }
                if(count==4 && curOwner==1){
                    return 1;
                }
                else if(count==4 && curOwner==2){
                    return 2;
                }
            }
        }
        /////Check Cheo Huyen/////////////////////////////////////////////////////////////////////
        for(int indexCol = 0; indexCol < numColumns-4; indexCol++){
            for(int indexRow = 0; indexRow < numRows-4; indexRow++){
                int curOwner = cells[indexRow][indexCol].getOwner();
                for(int i=1; i<5; i++){
                    if(cells[indexRow+i][indexCol+i].getOwner() == curOwner && curOwner != 0){
                        count++;
                    }
                    else{
                        count = 0;
                        break;
                    }
                }
                if(count==4 && curOwner==1){
                    return 1;
                }
                else if(count==4 && curOwner==2){
                    return 2;
                }
            }
        }
        /////Check Draw////////////////////////////////////////////////////////////////////
        int check = 0;
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numColumns; j++){
                if(cells[i][j].getOwner()==1 || cells[i][j].getOwner()==2)
                    check++;
            }
        }
        if(check == numRows*numColumns) return 0;
        return -1;
    }
}
