package com.lzq.pingpang;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.LayoutInflaterCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Stack;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private Stack<Integer> integerStack;

    private AlertDialog loadingDialog = null;

    protected void show() {
        if (loadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            loadingDialog = builder.create();
            loadingDialog.setCancelable(false);
            loadingDialog.setView(LayoutInflater.from(this).inflate(R.layout.layout_loading, null));
        }

        if (loadingDialog.isShowing()) {
            Log.i("hehe"," isshowing");
            if (integerStack == null) {
                integerStack = new Stack<>();
            }
            integerStack.push(0);
        } else {
            Log.i("hehe"," is not showing");
            loadingDialog.show();
        }

    }


    protected void dismiss() {
        if (integerStack == null) {
            integerStack = new Stack<>();
        }
        if (!integerStack.empty()) {
            Log.i("hehe","stack not null");
            integerStack.pop();
        } else {
            Log.i("hehe","stack null");
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

}
