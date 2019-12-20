package com.lzq.pingpang;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.JsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.lzq.pingpang.bean.ClientBean;
import com.lzq.pingpang.bean.MsgBean;
import com.lzq.pingpang.service.MyService;
import com.lzq.pingpang.socket.MyReadThread;
import com.lzq.pingpang.socket.MyWriteThread;
import com.lzq.pingpang.socket.ReadWriteThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.List;

public class MainActivity extends BaseActivity implements MyService.MsgReceiveLis {

    private BridgeWebView bridgeWebView;

    private ServiceConnection serviceConnection;

    private MyService myService;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyService.MyBinder binder = (MyService.MyBinder) service;
                myService = binder.getService();
                myService.setMsgReceiveLis(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bridgeWebView = findViewById(R.id.bwv_map);
        WebSettings webSettings = bridgeWebView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptEnabled(true);
        bridgeWebView.setWebChromeClient(new WebChromeClient());
        bridgeWebView.loadUrl("file:///android_asset/mymap.html");
        registerJavascriptHandler();


    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, MyService.class), serviceConnection, Service.BIND_AUTO_CREATE);
    }

    //注冊 javascript handler---------------------------------------------------------------------------------------------------------
    private void registerJavascriptHandler() {
        //地图被点击
        bridgeWebView.registerHandler("onMapClick", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
            }
        });


    }

    //-------------------------------------------------------------------------------------------------------------------------------
    public void zoomToBoundingBoxLeaflet(double south, double west, double north, double east) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("south", south);
            jsonObject.put("west", west);
            jsonObject.put("north", north);
            jsonObject.put("east", east);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bridgeWebView.callHandler("zoomToBoundingBox", jsonObject.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                //
            }
        });
    }

    //在地图上添加新的 client marker
    public void addClientMarkerOnMap(String token, String lat, String lng) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
            jsonObject.put("lat", lat);
            jsonObject.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bridgeWebView.callHandler("addClientMarkerOnMap", jsonObject.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });
    }

    //移除 地图上的 某个client marker
    public void removeClientMarkerFromMap(String token) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bridgeWebView.callHandler("removeClientMarkerFromMap", jsonObject.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });
    }

    //msg receive-----------------------------------------------------------------------------------------------------------------------
    @Override
    public void onMsgReceive(String jsonStr) {

        Log.i("hehe", "客户端收到的消息msg : " + jsonStr + " 当前线程： " + Thread.currentThread().getName());
        MsgBean msgBean = JSON.parseObject(jsonStr, MsgBean.class);
        int type = msgBean.getType();
        if (type == 0) {
            //上线
            List<ClientBean> from = msgBean.getFrom();
            for (ClientBean clientBean : from) {
                addClientMarkerOnMap(clientBean.getToken(), clientBean.getLat(), clientBean.getLng());
            }
        } else if (type == 1) {
            //通信

        } else if (type == 2) {
            //下线
            List<ClientBean> from = msgBean.getFrom();
            for (ClientBean clientBean : from) {
                removeClientMarkerFromMap(clientBean.getToken());
            }

        }

    }

}
