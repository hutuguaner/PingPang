package com.lzq.pingpang.socket;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzq.pingpang.bean.ClientBean;
import com.lzq.pingpang.bean.MsgBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ReadWriteThread extends Thread {

    private Socket server;


    public ReadWriteThread(String lat,String lng) {
        msgBeanQueue = new LinkedList<>();

        MsgBean msgBean = new MsgBean();
        msgBean.setType(0);
        msgBean.setContent("");
        ClientBean clientBean = new ClientBean();
        clientBean.setToken(SPUtils.getInstance().getString("token"));
        clientBean.setLat(lat);
        clientBean.setLng(lng);
        List<ClientBean> from = Arrays.asList(clientBean);
        ClientBean toClientBean = new ClientBean();
        toClientBean.setLng("");
        toClientBean.setLat("");
        toClientBean.setToken("");
        List<ClientBean> to = Arrays.asList(toClientBean);
        msgBean.setFrom(from);
        msgBean.setTo(to);
        msgBeanQueue.offer(msgBean);
    }

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;


    private Queue<MsgBean> msgBeanQueue;

    private Handler handler;


    @Override
    public void run() {
        try {
            this.server = new Socket("129.28.189.31", 8091);
            //this.server = new Socket("192.168.1.4", 8091);
            bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

            String readline = null;

            while (true) {
                MsgBean msgBean = msgBeanQueue.poll();
                if (msgBean != null) {
                    bufferedWriter.write(GsonUtils.toJson(msgBean) + "\n");
                    bufferedWriter.flush();
                } else {
                    bufferedWriter.write("\n");
                    bufferedWriter.flush();
                }

                readline = bufferedReader.readLine();
                if (readline != null && !readline.trim().equals("")) {
                    if (handler != null) {
                        Message message = Message.obtain();
                        message.obj = readline;
                        handler.sendMessage(message);
                    } else {
                    }
                } else {
                    //Log.i("hehe", " 客户端收到： null");
                }
                Thread.sleep(1000);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void addMsg(MsgBean msgBean) {
        if (msgBeanQueue != null)
            msgBeanQueue.offer(msgBean);
    }

    public void clearMsg() {
        if (msgBeanQueue != null)
            msgBeanQueue.clear();
    }

}
