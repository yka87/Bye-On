package com.example.opencv.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.opencv.NotifyActivity;

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
        String token = NotifyActivity.token;
        try {
            callXmlServer(token);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    private void callXmlServer(final String token) throws MalformedURLException, XmlRpcException {
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

                Object[] paramToken = {token};
                String succ = (String) client.execute("notify", paramToken);
                Log.e(TAG, succ);

            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
        thread.start();
    }
}
