package com.example.testble_multiple;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ServiceBleMultiple mBle;
    private Intent BLEServerIntent;
    private TextView textView1;
    private String getMsg;
    private Handler mHandler;
    //新增移除想要連接的ble device name
    private List<String> DEVICE_NAMES = new ArrayList<>(Arrays.asList("FeiZhiX8/X8Pro"));

    public ServiceConnection BLEConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("Mark1", "onServiceConnected");
            //取得service的實體
            mBle = ((ServiceBleMultiple.LocalBinder) iBinder).getService();
            //設定BLE Device name
            mBle.setBleDeviceNames(DEVICE_NAMES);
            //取得service的callback，在這邊是顯示接收BLE的資訊
            mBle.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareBLE();
        mHandler = new MyHandler(this);
        textView1 = findViewById(R.id.textview);

        //region 模擬ble發送訊號，格式為"ble device name,String"
        Button bt = findViewById(R.id.button1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendBleMsg(DEVICE_NAMES.get(0)+",C");
                SendBleMsg(DEVICE_NAMES.get(1)+",C");
                SendBleMsg(DEVICE_NAMES.get(2)+",C");
                SendBleMsg(DEVICE_NAMES.get(3)+",C");
            }
        });

        Button bt1 = findViewById(R.id.button2);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendBleMsg(DEVICE_NAMES.get(0)+",O");
                SendBleMsg(DEVICE_NAMES.get(1)+",O");
                SendBleMsg(DEVICE_NAMES.get(2)+",O");
                SendBleMsg(DEVICE_NAMES.get(3)+",O");
            }
        });
        //endregion
    }

    private void prepareBLE() {
        //region 請求權限 android 6.0+
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            }
        }
        //endregion

        //region 綁定service
        BLEServerIntent = new Intent(this, ServiceBleMultiple.class);
        bindService(BLEServerIntent, BLEConnection, Context.BIND_AUTO_CREATE);
        //endregion
    }

    private void SendBleMsg(String _msg){
        String[] msgs = _msg.split(",");
        if(mBle!=null)
            mBle.writeCharacteristic(msgs[0],msgs[1]);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String BleString = (String) msg.obj;
                    mActivity.get().textView1.setText(BleString);
                    break;
                default:
                    break;
            }
        }
    }
}
