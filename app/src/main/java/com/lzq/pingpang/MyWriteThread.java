package com.lzq.pingpang;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

public class MyWriteThread extends Thread {

    private Socket socket;

    public MyWriteThread(Socket socket) {
        this.socket = socket;
    }


    private BufferedWriter writer;

    @Override
    public void run() {
        super.run();
        try{
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Random random = new Random();

            while(true){
                String msg = "hello server i am client "+random.nextInt()+"\n";
                writer.write(msg);
                writer.flush();
                Thread.sleep(1000);
            }
        }catch (Exception e){

        }

    }
}
