package com.kugou.redwarring;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    /**
     * 设备策略服务
     */
    private DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
    }


    /**
     * 用代码去开启管理员
     */
    public void openAdmin(View view) {
        // 创建一个Intent
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        // 我要激活谁
        ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        // 劝说用户开启管理员权限
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "哥们开启我可以一键锁屏，让你丢掉手机");
        startActivity(intent);
    }

    private volatile boolean isDone = false;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isDone) {
                return;
            } else {
                lockscreen(getWindow().getDecorView());
                handler.postDelayed(runnable, 5 * 1000);
            }
        }
    };

    public void onLock(View view) {
        isDone = false;
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                isDone = true;
            }
        };
        timer.schedule(task, 1000 * 60 * 60);

        handler.post(runnable);
    }

    /**
     * 一键锁屏
     */
    public void lockscreen(View view) {
        ComponentName who = new ComponentName(this, MyAdmin.class);
        if (dpm.isAdminActive(who)) {
            dpm.lockNow();// 锁屏
//            dpm.resetPassword("", 0);// 设置屏蔽密码
            // 清除Sdcard上的数据
            // dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            // 恢复出厂设置
            // dpm.wipeData(0);
        } else {
            Toast.makeText(this, "还没有打开管理员权限", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
