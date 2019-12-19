package com.lzq.pingpang;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.lzq.pingpang.socket.MyReadThread;
import com.lzq.pingpang.socket.MyWriteThread;
import com.lzq.pingpang.socket.ReadWriteThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.List;

public class MainActivity extends BaseActivity {

    private BridgeWebView bridgeWebView;
    Location location;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bridgeWebView = findViewById(R.id.bwv_map);
        WebSettings webSettings = bridgeWebView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptEnabled(true);
        bridgeWebView.setWebChromeClient(new WebChromeClient());
        bridgeWebView.loadUrl("file:///android_asset/mymap.html");
        registerJavascriptHandler();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        String provider;
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        location = locationManager.getLastKnownLocation(provider);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hehe();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    private void hehe() throws Exception {
        Socket socket = new Socket("192.168.1.174", 8091);
        ReadWriteThread readWriteThread = new ReadWriteThread(socket);
        if(location==null){
            readWriteThread.setLat("39.978437");
            readWriteThread.setLng("116.377823");
        }else{
            readWriteThread.setLat(Double.toString(location.getLatitude()));
            readWriteThread.setLng(Double.toString(location.getLongitude()));
        }

        readWriteThread.start();
    }


    //注冊 javascript handler
    private void registerJavascriptHandler() {
        //地图被点击
        bridgeWebView.registerHandler("onMapClick", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
            }
        });
    }

    //
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
}
