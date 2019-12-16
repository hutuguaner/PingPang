package com.lzq.pingpang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private BridgeWebView bridgeWebView;

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
