package com.android.sys.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataPack {
    public static final short SIGNATURE = -8531; // 0XDEAD
    public static boolean sendDataPack(OutputStream os, byte[] data) {
        long len = data.length;
        DataOutputStream dos = new DataOutputStream(os);
        try {
            dos.writeShort(SIGNATURE);
            dos.writeLong(len);
            dos.write(data);
            dos.flush();
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    public static byte[] receiveDataPack(InputStream is)
    {
        DataInputStream dis = new DataInputStream(is);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            short Sign = dis.readShort();
            if(Sign!=SIGNATURE) {
                return null;
            }
            long len = dis.readLong();
            int bufLen = 1024;
            byte[] data = new byte[bufLen];
            long i = 0;
            while(i<len) {
                long nRead = bufLen;
                if(nRead + i > len)
                {
                    nRead = len - i;
                }
                nRead = dis.read(data, 0, (int) nRead);
                if(nRead <=0)
                {
                    continue;
                }
                i+= nRead;
                byteArrayOutputStream.write(data, 0, (int) nRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }
}

