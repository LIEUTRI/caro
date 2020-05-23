package com.liutri.caro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    EditText editServerIP;
    EditText editPort;
    Button buttonChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        editServerIP = findViewById(R.id.editServerIP);
        editPort = findViewById(R.id.editPort);
        buttonChange = findViewById(R.id.buttonChange);

        loadGameSetting();

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SERVER_IP = editServerIP.getText().toString();
                MainActivity.PORT = Integer.parseInt(editPort.getText().toString());
                doSave();
                Toast.makeText(SettingActivity.this, "SAVED", Toast.LENGTH_LONG).show();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void loadGameSetting()  {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSetting", Context.MODE_PRIVATE);

        if(sharedPreferences!= null) {
            MainActivity.SERVER_IP = sharedPreferences.getString("SERVERIP",MainActivity.SERVER_IP);
            MainActivity.PORT = sharedPreferences.getInt("PORT",MainActivity.PORT);
            this.editServerIP.setText(MainActivity.SERVER_IP);
            this.editPort.setText(MainActivity.PORT + "");
        } else {
            editServerIP.setText(MainActivity.SERVER_IP);
            editPort.setText(MainActivity.PORT + "");
        }
    }

    public void doSave()  {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SERVERIP", MainActivity.SERVER_IP);
        editor.putInt("PORT", MainActivity.PORT);
        editor.apply();
    }
}
