package attv.toolkit;

import attv.toolkit.IJsonChannelCallback;
import attv.toolkit.IJsonChannelSession;
import android.os.Bundle;

interface IJsonChannelService{
	IJsonChannelSession createSession(String name, in IJsonChannelCallback cb, in Bundle bundle);
}