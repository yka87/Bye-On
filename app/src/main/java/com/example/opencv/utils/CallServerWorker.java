package com.example.opencv.utils;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.net.MalformedURLException;
import java.net.URL;

/*
    This class defines worker that sends requests to XmlServer in python, to receive and schedule notification.
    Code based on [https://developer.android.com/topic/libraries/architecture/workmanager]
 */
public class CallServerWorker extends Worker {
    private static final String TAG = "server testing";

    public CallServerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            callXmlServer();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    private void callXmlServer() throws MalformedURLException, XmlRpcException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    config.setServerURL( new URL( "http://10.0.2.2:8000/RPC2"));
                    config.setBasicEncoding("utf-8");
                    config.setEncoding("utf-8");

                    XmlRpcClient client = new XmlRpcClient();
                    client.setConfig(config);
                    Log.e(TAG, "client set?");

                    Object [] param1 = {"mini"};
                    String result2 = (String) client.execute("cap", param1);
                    Log.e(TAG, result2);

                    Object[] params = new Object[0];
                    String result = (String) client.execute("notify", params);
                    Log.e(TAG, result);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
