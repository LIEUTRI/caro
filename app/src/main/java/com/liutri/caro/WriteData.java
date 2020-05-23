package com.liutri.caro;

import java.io.ObjectOutputStream;
import java.net.Socket;

import components.Cell;

public class WriteData extends Thread {
    private Socket s;
    private Cell cellOUT;
    WriteData(Socket s, Cell cellOUT){
        this.s = s;
        this.cellOUT=cellOUT;
    }

    @Override
    public void run() {
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(s.getOutputStream());
            Cell cell = cellOUT;
            outputStream.writeObject(cell);
        }catch (Exception e){
            System.out.println("client write-data error: " + e);
        }
    }
}
