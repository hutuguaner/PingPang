package com.lzq.pingpang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzq.pingpang.BaseActivity;
import com.lzq.pingpang.Const;
import com.lzq.pingpang.MainActivity;
import com.lzq.pingpang.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener {


    private EditText etUsername, etPwd;
    private Button btLogin, btRegist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_login_username);
        etPwd = findViewById(R.id.et_login_pwd);
        btLogin = findViewById(R.id.bt_login_login);
        btRegist = findViewById(R.id.bt_login_regist);

        btLogin.setOnClickListener(this);
        btRegist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_login:
                login();
                break;
            case R.id.bt_login_regist:
                startActivity(new Intent(this, RegistActivity.class));
                break;
        }
    }

    private void login() {
        String username = etUsername.getText().toString();
        String pwd = etPwd.getText().toString();
        if (username == null || username.trim().equals("")) {
            ToastUtils.showLong("请输入用户名");
            return;
        }
        if (pwd == null || pwd.trim().equals("")) {
            ToastUtils.showLong("请输入密码");
            return;
        }
        login(username, pwd);
    }


    private void login(String username, String pwd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("pwd", EncryptUtils.encryptMD5ToString(pwd));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Const.login).upJson(jsonObject.toString()).execute(new StringCallback() {
            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                show();
            }

            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject responsJson = new JSONObject(response.body());
                    int code = responsJson.getInt("code");
                    if (code == 0) {
                        String data = responsJson.getString("data");
                        SPUtils.getInstance().put("token", data);
                        ToastUtils.showLong("登录成功");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        String msg = responsJson.getString("msg");
                        ToastUtils.showLong(msg);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismiss();
            }
        });

    }
}
