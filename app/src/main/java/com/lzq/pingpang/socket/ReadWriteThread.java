package com.lzq.pingpang.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ReadWriteThread extends Thread {

    private Socket server;

    public ReadWriteThread(Socket server) {
        this.server = server;
    }

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    @Override
    public void run() {
        super.run();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

            String readline = null;
            while (true) {
                bufferedWriter.write("from/assssdfasdfasdfasdfsdfadfasfasdfasdfasfasdfasdfasdfasfasdfasdfasdfsadfasdfasdfasdfasdfasdfsadfsdfasfsdfasdfasdfsadfasdfasdfasdfasdfasdfasdfsafasdfsadfasdfasdfasdfsaf/to\n");
                bufferedWriter.flush();
                readline = bufferedReader.readLine();
                if(readline!=null){
                    Log.i("hehe"," 客户端收到： "+readline);
                }
                Thread.sleep(1000);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
