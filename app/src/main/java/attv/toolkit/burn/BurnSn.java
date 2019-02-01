package attv.toolkit.burn;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;

import attv.toolkit.JsonChannel;
import attv.toolkit.JsonParcelable;

public class BurnSn {

    JsonChannel jsonChannel = null;

    public BurnSn(Context context, String serviceName) {
        jsonChannel = new JsonChannel(context, serviceName) {

            @Override
            public void onChannelConnected() {
                onServiceConnected();
            }

            @Override
            public void onCallback(int cmd, String json, JsonParcelable p, Bundle b) throws JSONException {
                switch (cmd) {
                    case 1:
                        Log.d("haio", "BurnSn::onCallback json=" + cmd + ":" +json);
                        break;
                }
            }
        };
        jsonChannel.connect();

    }

    public void onServiceConnected() {
        Log.d("haio", "do something::onServiceConnected");
        beginBurnSn();
    }

    public void beginBurnSn(){
        jsonChannel.transmitAsync(1);
    }
}
