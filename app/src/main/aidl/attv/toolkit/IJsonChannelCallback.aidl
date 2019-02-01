package attv.toolkit;

import android.os.Bundle;
import attv.toolkit.JsonParcelable;

interface IJsonChannelCallback{
	oneway void onVersion(int v);
	oneway void onCallback(int cmd, String json, in JsonParcelable p,in Bundle b,boolean vn);
}