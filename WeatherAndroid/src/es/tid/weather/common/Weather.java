package es.tid.weather.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.List;

public class Weather implements Serializable{
	private static final long serialVersionUID = 1L;
	public String city;
	public CurrentWeather currentWeather;
	public List<WeatherDay> forecast;

	public Weather(String city, CurrentWeather currentWeather,
			List<WeatherDay> forecast) {
		this.city = city;
		this.currentWeather = currentWeather;
		this.forecast = forecast;
	}

	public static void Serialize(Weather weather, String filePath) throws FileNotFoundException, IOException {
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(filePath));
	    out.writeObject(weather);
	    out.close();
	}

	public static Weather Deserialize(String filePath) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException {
		 File file = new File(filePath);
		 ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		 Weather weather = (Weather) in.readObject();
		 in.close();
		 return weather;
	}	
}
