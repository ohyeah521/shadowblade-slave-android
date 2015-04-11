package com.android.sys.activity;

import android.app.Activity;
import android.os.Bundle;

import com.android.sys.service.SystemService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemService.start(this);
        finish();
    }
}