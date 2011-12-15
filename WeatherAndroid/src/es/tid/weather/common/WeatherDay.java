package es.tid.weather.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WeatherDay implements Serializable{
	private static final long serialVersionUID = -1092121797431998037L;
	public Date date;
	public String condition;
	public String iconUrl;
	public transient Bitmap icon;
	public byte[] iconBytes;
	public int tempMin;
	public int tempMax;

	public WeatherDay(Date date, String condition, String iconUrl, byte[] iconBytes, int tempMin, int tempMax) {
		this.date = date;
		this.condition = condition;
		this.iconUrl = iconUrl;
		this.iconBytes = iconBytes;
		this.icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
		this.tempMin = tempMin;
		this.tempMax = tempMax;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
	}

}
