package com.liutri.caro;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import components.Room;

public class RoomActivity extends ListActivity {
    @SuppressLint("StaticFieldLeak")
    static Context context;
    ArrayList<Room> list;
    static GetData task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        task = new GetData();
        task.execute();
        try {
            list=task.get();
            if (list.isEmpty()) finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setListAdapter(new MyAdapter(this, list));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GetData.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        task.cancel(true);
        finish();
    }
}

@SuppressLint("NewApi")
class GetData extends AsyncTask<Void, ArrayList<Room>, ArrayList<Room>>{
    private ArrayList<Room> listRoom;
    static Socket socket=null;
    private String status = "";
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected ArrayList<Room> doInBackground(Void... voids) {
        ObjectInputStream inputStream;
        listRoom = new ArrayList<Room>();
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.SERVER_IP, MainActivity.PORT), 2000);
            status = "Đã kết nối đến " + socket.getRemoteSocketAddress();
            publishProgress();
            inputStream = new ObjectInputStream(socket.getInputStream());
            listRoom = (ArrayList<Room>) inputStream.readObject();
        } catch (Exception e) {
            System.out.println("Loi: " + e);
            status = "Lỗi: Không thể kết nối đến server!";
            publishProgress();
        }
        return listRoom;
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(ArrayList<Room>... values) {
        super.onProgressUpdate(values);
        Toast.makeText(RoomActivity.context, status, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(ArrayList<Room> list) {
        Toast.makeText(RoomActivity.context, status,Toast.LENGTH_LONG).show();
        listRoom = list;
    }
}

