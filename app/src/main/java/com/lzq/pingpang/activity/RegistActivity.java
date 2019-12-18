package com.lzq.pingpang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.JsonUtils;
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

public class RegistActivity extends BaseActivity implements View.OnClickListener {

    private EditText etUsername, etPwd, etPwdAgain;
    private Button btRegist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        etUsername = findViewById(R.id.et_regist_username);
        etPwd = findViewById(R.id.et_regist_pwd);
        etPwdAgain = findViewById(R.id.et_regist_pwdagain);
        btRegist = findViewById(R.id.bt_regist_regist);

        btRegist.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_regist_regist:
                regist();
                break;
        }
    }


    private void regist() {
        String username = etUsername.getText().toString();
        String pwd = etPwd.getText().toString();
        String pwdAgain = etPwdAgain.getText().toString();
        if (username == null || username.trim().equals("")) {
            ToastUtils.showLong("请输入用户名");
            return;
        }
        if (pwd == null || pwd.trim().equals("")) {
            ToastUtils.showLong("请输入密码");
            return;
        }
        if (pwdAgain == null || pwdAgain.trim().equals("")) {
            ToastUtils.showLong("请再次输入密码");
            return;
        }

        if (!pwd.equals(pwdAgain)) {
            ToastUtils.showLong("两次输入的密码不一致，请确认");
            return;
        }

        regist(username, pwd);
    }


    private void regist(String username, String pwd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("pwd", EncryptUtils.encryptMD5ToString(pwd));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Const.regist).upJson(jsonObject.toString()).execute(new StringCallback() {
            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                show();
            }

            @Override
            public void onSuccess(Response<String> response) {
                Log.i("hehe", " : " + response.body());
                try {
                    JSONObject resJson = new JSONObject(response.body());
                    int code = resJson.getInt("code");
                    if (code == 0) {
                        ToastUtils.showLong("注册成功");
                        RegistActivity.this.finish();
                    } else {
                        String msg = resJson.getString("msg");
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
