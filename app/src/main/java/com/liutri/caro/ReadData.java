package com.liutri.caro;

import java.io.ObjectInputStream;
import java.net.Socket;

import components.Cell;

public class ReadData extends Thread {
    Socket s;
    public ReadData(Socket s){
        this.s = s;
    }

    @Override
    public void run() {
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(s.getInputStream());
            while (true){
                Cell dataIN = (Cell) inputStream.readObject();
                System.out.println("data: " + dataIN.getRow() + "," + dataIN.getCol());
            }
        }catch (Exception e){
            System.out.println("client read-data error: "+e);
        }
    }
}
