package attv.toolkit;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * author:lvboyang
 * date:20190102
 */
public abstract class JsonChannelService extends Service {

    static final String TAG = "JsonChannelService";

    private final JsonChannelServiceBinder mBinder = new JsonChannelServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private static class JsonChannelServiceBinder extends IJsonChannelService.Stub {

        @Override
        public IJsonChannelSession createSession(String name, IJsonChannelCallback cb, Bundle bundle) throws RemoteException {
            final InternalIJsonChannelSession internalSession = new InternalIJsonChannelSession(cb);
            return internalSession;
        }
    }

    private static class InternalIJsonChannelSession extends IJsonChannelSession.Stub {

        private final IJsonChannelCallback mCallback;

        public InternalIJsonChannelSession(IJsonChannelCallback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public String transmit(int code, String json, JsonParcelable p, Bundle b, int nv) throws RemoteException {
            return null;
        }

        @Override
        public void atransmit(int code, String json, JsonParcelable p, Bundle b) throws RemoteException {
            mCallback.onCallback(code, null, null, null, true);
        }

        @Override
        public void close() throws RemoteException {

        }
    }
}
