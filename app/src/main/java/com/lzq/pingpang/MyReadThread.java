package com.lzq.pingpang;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyReadThread extends Thread {

    private Socket socket;

    public MyReadThread(Socket socket) {
        this.socket = socket;
    }

    private BufferedReader reader;

    @Override
    public void run() {
        super.run();
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String readline = null;
            while (true) {
                readline = reader.readLine();
                if (readline != null && !readline.trim().equals("") && !readline.trim().equals("null")) {
                    Log.i("hehe","客户端收到的数据： "+readline);
                }
            }
        } catch (Exception e) {

        }

    }
}
