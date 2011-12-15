package es.tid.weather.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CurrentWeather implements Serializable{
	private static final long serialVersionUID = 2326668849357284612L;
	public Date date;
	public int cloudCover;
	public int pressure;
	public int temp;
	public int humidity;
	public String iconUrl;
	public transient Bitmap icon;
	public byte[] iconBytes;
	public int windspeed;
	public String condition;
	public int tempMin;
	public int tempMax;

	public CurrentWeather(Date date, int cloudCover, int pressure,
			int temp, int humidity, String iconUrl, byte[] iconBytes, int windspeed,
			String condition, int tempMin, int tempMax) {
		this.date = date;
		this.cloudCover = cloudCover;
		this.pressure = pressure;
		this.temp = temp;
		this.humidity = humidity;
		this.iconUrl = iconUrl;
		this.iconBytes = iconBytes;
		this.icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
		this.windspeed = windspeed;
		this.condition = condition;
		this.tempMin = tempMin;
		this.tempMax = tempMax;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
	}
}
