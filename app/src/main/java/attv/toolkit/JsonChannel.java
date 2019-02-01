package attv.toolkit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author:lvboyang
 * date:20190102
 * aim:to control a client to communication with service
 */
public abstract class JsonChannel {

    private static final String TAG = "JsonChannel";

    static HashMap<String, Service> services = new HashMap<>();

    private Context ctx;
    private Service service;
    private String sessionName, serviceName;
    private Bundle args;
    private IJsonChannelSession mSession;
    private boolean shotted = false, connected = false;

    private Object mutex = new Object();

    static class Service implements ServiceConnection, IBinder.DeathRecipient{

        String serviceName;
        boolean connected = false;
        IJsonChannelService mService;

        List<WeakReference<JsonChannel>> sesssions = new ArrayList<WeakReference<JsonChannel>>();
        List<JsonChannel> sessionsWaiting = new ArrayList<JsonChannel>();

        static Service getService(Context context, String serviceName, boolean create){

            synchronized (services) {
                Service s = services.get(serviceName);
                if (s == null && create){
                    try {
                        if ((s = new Service(context, serviceName)) != null){
                            services.put(serviceName, s);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return s;
            }
        }

        public Service(Context context, String serviceName) throws IOException {
            this.serviceName = serviceName;
            Intent i = new Intent(serviceName);
            i.setPackage(context.getPackageName());
            if (!context.bindService(i,this, Context.BIND_AUTO_CREATE)){
                throw new IOException("Binderservice failed! serviceName=" + serviceName);
            }
            Log.d("haio", "ready to connect pid=" + Process.myPid() + ";tid=" + Process.myTid());
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d("haio", "onServiceConnected pid=" + Process.myPid() + ";tid=" + Process.myTid());
            try {
                mService = IJsonChannelService.Stub.asInterface(service);
                service.linkToDeath(this, 0);
                connected = true;
                for (JsonChannel ch : sessionsWaiting) {
                    if ((ch.mSession = mService.createSession(ch.sessionName, ch.mCallback, ch.args)) != null){
                        Log.d(TAG, "onServiceConnected ch.sessionName=" + ch.sessionName);
                        sesssions.add(new WeakReference<JsonChannel>(ch));
                        ch.onConnection(true);
                    }
                }
                sessionsWaiting.clear();
                Log.d("haio", "onServiceConnected end connect:" + connected);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

        @Override
        public void binderDied() {
            onServiceDisconnected(null);
        }

        boolean scheduleChannel(JsonChannel ch){
            Log.d(TAG, "scheduleChannel connected=" + connected);
            synchronized (sesssions) {
                try {
                    if (connected){
                        if ((ch.mSession = mService.createSession(ch.sessionName, ch.mCallback, ch.args)) != null){
                            Log.d(TAG, "onServiceConnected ch.sessionName=" + ch.sessionName);
                            sesssions.add(new WeakReference<JsonChannel>(ch));
                            ch.onConnection(true);
                            return true;
                        }
                    }else {
                        sessionsWaiting.add(ch);
                        return true;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
    }

    private IJsonChannelCallback mCallback = new IJsonChannelCallback() {
        private volatile int callbackVersion = 0;

        /**
         * 这里涉及是为了避免过期session，有时效性
         * @param v
         * @throws RemoteException
         */
        @Override
        public void onVersion(int v) throws RemoteException {
            callbackVersion = v;
        }

        @Override
        public void onCallback(int cmd, String json, JsonParcelable p, Bundle b, boolean nv) throws RemoteException {
            Log.d(TAG, "onCallback cmd = " + cmd + ";json = " + json + ";nv = " + nv);
            try {
                JsonChannel.this.onCallback(cmd, json, p, b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    public JsonChannel(Context ctx, String serviceName) {
        this(ctx.getApplicationContext(), serviceName, null);
    }

    public JsonChannel(Context ctx, String serviceName, String sessionName) {
        this.ctx = ctx;
        this.serviceName = serviceName;
        this.sessionName = sessionName;
        Log.d(TAG, "JsonChannel serviceName:" + serviceName + ",sessionName=" + sessionName);
    }

    void onConnection(boolean b) {
        Log.e(TAG, "onConnection" + (b ? "Connected" : "Disconnectted"));
        onChannelConnected();
    }

    public void setArguments(Bundle b) {
        args = b;
    }

    public Bundle getArguments() {
        return args;
    }

    public Context getContext() {
        return ctx;
    }

    public boolean isShotted() {
        return shotted;
    }

    public boolean connect(){
        Log.d(TAG, "client to connect");
        synchronized (mutex) {
            if (!shotted){
                final Service s = Service.getService(ctx, serviceName, true);
                Log.d(TAG, "after service creaete");
                if (s != null){
                    shotted = true;
                    if ((shotted = s.scheduleChannel(JsonChannel.this))){
                        service = s;
                    }
                }

            }
        }
        return shotted;
    }

    public void disconnect(){
        Log.d(TAG, "disconnect");

    }

    public void transmitAsync(int code) {
        transmitAsync(code, null, null, null);
    }

    public void transmitAsync(int code, String json) {
        transmitAsync(code, json, null, null);
    }

    public void transmitAsync(int code, String json, JsonParcelable p, Bundle b) {
        synchronized (mutex) {
            if (mSession != null) {
                try {
                    Log.d(TAG, "transmitAsync code=" + code + ",json=" + json);
                    mSession.atransmit(code, json, p, b);
                    Log.d(TAG, "transmitAsync end code=" + code);
                } catch (RemoteException e) { Log.e(TAG, "atransmit error:" + e);
                }
            }
        }
    }

    public void onChannelConnected() {
    }

    public void onChannelDisconnectted() {
    }

    public abstract void onCallback(int code, String json, JsonParcelable p, Bundle b)
            throws JSONException;
}
