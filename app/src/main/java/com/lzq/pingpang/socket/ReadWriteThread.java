package com.lzq.pingpang.socket;

import android.util.Log;

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

    public ReadWriteThread(Socket server) {
        this.server = server;
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

    private String lat;
    private String lng;

    private Queue<MsgBean> msgBeanQueue;


    @Override
    public void run() {
        super.run();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

            String readline = null;
            while (true) {
                MsgBean msgBean = msgBeanQueue.poll();
                if(msgBean!=null){
                    bufferedWriter.write(GsonUtils.toJson(msgBean) + "\n");
                    bufferedWriter.flush();
                }else{
                    bufferedWriter.write("\n");
                    bufferedWriter.flush();
                }

                readline = bufferedReader.readLine();
                if (readline != null&&!readline.trim().equals("")) {
                    Log.i("hehe", " 客户端收到： " + readline);
                }else{
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
