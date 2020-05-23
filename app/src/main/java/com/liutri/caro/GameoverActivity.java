package com.liutri.caro;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameoverActivity extends Activity {

    Button buttonPlayagain;
    Button buttonClose;
    Button buttonExit;
    TextView textViewGameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        buttonPlayagain = findViewById(R.id.buttonPlayagain);
        buttonClose = findViewById(R.id.buttonClose);
        buttonExit = findViewById(R.id.buttonExit);
        textViewGameStatus = findViewById(R.id.textViewGameStatus);

        Intent intent = getIntent();
        String value = intent.getStringExtra("gamestatus");
        if (value.equals("BẠN THUA!")) textViewGameStatus.setTextColor(Color.RED);
        else if (value.equals("BẠN THẮNG!")) textViewGameStatus.setTextColor(Color.BLUE);
        textViewGameStatus.setText(value);

        buttonPlayagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), SingleplayerActivity.class);
                startActivity(myIntent);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finish();
                }
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }
        });
    }
}
