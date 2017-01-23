package com.telse.chating;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;


public class ConnectionService extends Service {
    private static final String TAG = "ConnectionService";

    public static final String UI_AUTHENTICATED = "com.telse.chating.uiauthenticated";
    public static final String SEND_MESSAGE = "com.telse.chating.sendmessage";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_TO = "b_to";

    public static final String NEW_MESSAGE = "com.telse.chating.newmessage";
    public static final String BUNDLE_FROM_JID = "b_from";

    public static Connection.ConnectionState sConnectionState;
    public static Connection.LoggedInState sLoggedInState;
    private boolean mActive;
    private Thread mThread;
    private Handler mTHandler;
    private Connection mConnection;

    public ConnectionService() {

    }

    public static Connection.ConnectionState getState() {
        if (sConnectionState == null) {
            return Connection.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static Connection.LoggedInState getLoggedInState() {
        if (sLoggedInState == null) {
            return Connection.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    private void initConnection() {
        Log.d(TAG, "initConnection()");
        if (mConnection == null) {
            mConnection = new Connection(this);
        }
        try {
            mConnection.connect();

        } catch (IOException | SmackException | XMPPException e) {
            Log.d(TAG, "Bad connection, try again later");
            e.printStackTrace();
            //Stop the service all together.
            stopSelf();
        }

    }


    public void start() {
        Log.d(TAG, "ConnectService Start() function called.");
        if (!mActive) {
            mActive = true;
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        Looper.loop();

                    }
                });
                mThread.start();
            }


        }

    }

    public void stop() {
        Log.d(TAG, "stop()");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                }
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        stop();
    }
}
