package attv.toolkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

public class JsonParcelable implements Parcelable {
	public HashMap<String, Object> params = new HashMap<String, Object>();

	public int size() {
		return params.size();
	}

	public void put(String name, Parcelable p) {
		synchronized (params) {
			params.put(name, p);
		}
	}

	public void put(String name, String s) {
		synchronized (params) {
			params.put(name, s);
		}
	}

	public void put(String name, int v) {
		synchronized (params) {
			params.put(name, v);
		}
	}

	public void put(String name, long v) {
		synchronized (params) {
			params.put(name, v);
		}
	}

	public void put(String name, double v) {
		synchronized (params) {
			params.put(name, v);
		}
	}

	public void put(String name, float v) {
		synchronized (params) {
			params.put(name, v);
		}
	}

	public Object getObject(String name) {
		synchronized (params) {
			return params.get(name);
		}
	}

	public String getString(String name) {
		synchronized (params) {
			return (String) params.get(name);
		}
	}

	public int getInt(String name) {
		return (Integer) params.get(name);
	}

	public double getDouble(String name) {
		return (Double) params.get(name);
	}

	public float getFloat(String name) {
		return (Float) params.get(name);
	}

	public Parcelable getParcelable(String name) {
		synchronized (params) {
			return (Parcelable) params.get(name);
		}
	}

	public List<Parcelable> getParcelableValues() {
		synchronized (params) {
			List<Parcelable> ret = new ArrayList<Parcelable>();
			for (Object p : params.values()) {
				if (p instanceof Parcelable) {
					ret.add((Parcelable) p);
				}
			}
			return ret;
		}
	}

	public List<String> getNames() {
		synchronized (params) {
			return new ArrayList<String>(params.keySet());
		}
	}

	public void clear() {
		synchronized (params) {
			params.clear();
		}
	}

	public void clean() {
		for (Parcelable pi : getParcelableValues()) {
			try {
				if (pi instanceof ParcelFileDescriptor)
					((ParcelFileDescriptor) pi).close();
				else if (pi instanceof Bitmap)
					((Bitmap) pi).recycle();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(params);
	}

	public static final Creator<JsonParcelable> CREATOR = new Creator<JsonParcelable>() {
		public JsonParcelable createFromParcel(Parcel in) {
			JsonParcelable tp = new JsonParcelable();
			in.readMap(tp.params, getClass().getClassLoader());
			return tp;
		}

		public JsonParcelable[] newArray(int size) {
			return new JsonParcelable[size];
		}
	};
}