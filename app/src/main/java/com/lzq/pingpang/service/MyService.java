package com.lzq.pingpang.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.SPUtils;
import com.lzq.pingpang.bean.ClientBean;
import com.lzq.pingpang.bean.MsgBean;
import com.lzq.pingpang.socket.ReadWriteThread;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class MyService extends Service {
    public MyService() {
    }

    private MyBinder binder = new MyBinder();

    private MsgReceiveLis msgReceiveLis;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        socketConnect();
        Log.i("hehe", "service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("hehe", "service onDestroy");
        disConnect();
    }


    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }


    private void socketConnect() {
        try {
            hehe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Location location;
    ReadWriteThread readWriteThread;

    private void hehe() throws Exception {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        String provider;
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        location = locationManager.getLastKnownLocation(provider);


        if (location == null) {
            readWriteThread = new ReadWriteThread("39.978437", "116.377823");
        } else {
            readWriteThread = new ReadWriteThread(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
        }
        readWriteThread.setHandler(handler);
        readWriteThread.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msgReceiveLis != null) {
                msgReceiveLis.onMsgReceive((String) msg.obj);
            }
        }
    };


    private void disConnect() {
        if (readWriteThread != null) {
            readWriteThread.clearMsg();
            MsgBean msgBean = new MsgBean();
            msgBean.setType(2);
            msgBean.setContent("");
            ClientBean from = new ClientBean();
            from.setToken(SPUtils.getInstance().getString("token"));
            from.setLat("");
            from.setLng("");
            ClientBean to = new ClientBean();
            to.setLng("");
            to.setLat("");
            to.setToken("");
            msgBean.setFrom(Arrays.asList(from));
            msgBean.setTo(Arrays.asList(to));
            readWriteThread.addMsg(msgBean);
        }
    }

    public void sendMsg(String msg) {
        MsgBean msgBean = new MsgBean();
        msgBean.setType(1);
        msgBean.setContent(msg);
        ClientBean from = new ClientBean();
        from.setToken(SPUtils.getInstance().getString("token"));
        from.setLat("");
        from.setLng("");
        ClientBean to = new ClientBean();
        to.setLng("");
        to.setLat("");
        to.setToken("");
        msgBean.setFrom(Arrays.asList(from));
        msgBean.setTo(Arrays.asList(to));
        readWriteThread.addMsg(msgBean);
    }


    public interface MsgReceiveLis {
        void onMsgReceive(String jsonStr);
    }


    public void setMsgReceiveLis(MsgReceiveLis msgReceiveLis) {
        this.msgReceiveLis = msgReceiveLis;
    }


}
