package com.liutri.caro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String SERVER_IP = "127.0.0.1";
    public static int PORT = 1998;
    Button buttonSingleplayer;
    Button buttonMultiplayer;
    Button buttonSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSingleplayer = findViewById(R.id.buttonSingleplayer);
        buttonMultiplayer = findViewById(R.id.buttonMultiplayer);
        buttonSetting = findViewById(R.id.buttonSetting);

        loadGameSetting();

        buttonSingleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SingleplayerActivity.class);
                startActivity ( intent );
            }
        });
        buttonMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                startActivity ( intent );
            }
        });
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity ( intent );
            }
        });
    }

    private void loadGameSetting()  {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSetting", Context.MODE_PRIVATE);

        if(sharedPreferences!= null) {
            SERVER_IP = sharedPreferences.getString("SERVERIP",MainActivity.SERVER_IP);
            PORT = sharedPreferences.getInt("PORT",MainActivity.PORT);
        } else {
            Toast.makeText(this,"use default settings",Toast.LENGTH_SHORT).show();
        }
    }
}