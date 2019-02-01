package attv.toolkit;

import android.os.Parcelable;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import attv.toolkit.JsonParcelable;


interface IJsonChannelSession{
	String transmit(int code, String json, in JsonParcelable p, in Bundle b, int nv);
	oneway void atransmit(int code, String json, in JsonParcelable p, in Bundle b);
	void close();
}