package com.liutri.caro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.io.IOException;
import java.util.ArrayList;

import components.Room;

class MyAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Room> list;

    MyAdapter(Context context, ArrayList<Room> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TwoLineListItem twoLineListItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        final String roomID = list.get(position).getRoomID();
        final int players = list.get(position).getPlayers();
        text1.setText("ID phòng: "+roomID);
        text2.setText(players+" người trong phòng");
        text2.setTextColor(Color.GREEN);

        twoLineListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getPlayers() == 2){
                    Toast.makeText(context, "Phòng đã đầy!", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    Intent intent = new Intent(context, MultiplayerActivity.class);
                    if (list.get(position).getPlayers()==0) Board.curPlayer=1;
                    else if (list.get(position).getPlayers()==1) Board.curPlayer=2;
                    intent.putExtra("ROOMID",roomID);
                    intent.putExtra("PLAYERS",players);
                    context.startActivity(intent);
                }
            }
        });

        return twoLineListItem;
    }
}