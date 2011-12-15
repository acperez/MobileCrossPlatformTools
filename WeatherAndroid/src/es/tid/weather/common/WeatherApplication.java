package es.tid.weather.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import es.tid.weather.common.Definitions.ERROR_TYPE;
import es.tid.weather.http.HttpConnection;
import es.tid.weather.http.HttpResult;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class WeatherApplication extends Application{
	public String serializedPath;
	public Weather weather = null;
	public String errorMsg = null;
	public ERROR_TYPE errorType = ERROR_TYPE.NONE;
	private Handler callback;
	private Handler httpHandler;
	public static Bitmap texture;
	
	public void Init() {
		serializedPath = getFilesDir().getAbsolutePath() + File.separator + "weather.data";
		
		try {
			weather = Weather.Deserialize(serializedPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void UpdateForecastData(String city, final Handler callback) {
		PrepareHandler();
		HttpConnection forecastConnection = new HttpConnection(httpHandler);
		city = URLEncoder.encode(city);
		String url = String.format(Definitions.WEATHER_URL, city);
		forecastConnection.get(url, null);
		this.callback = callback;
	}
	
	public boolean UpdateForecastDataSync(String city) {
		try {
			city = URLEncoder.encode(city);
			String url = String.format(Definitions.WEATHER_URL, city);
			HttpResult response = HttpConnection.getSync(url, null, null, false);
			if(response.status.getStatusCode() == 200)
				return ParseJSON(response.content);
			else{
				errorType = ERROR_TYPE.HTTP;
				errorMsg = response.status.toString();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorType = ERROR_TYPE.HTTP;
			errorMsg = e.getMessage();
			return false;
		}
	}
	
	private void PrepareHandler(){
		httpHandler = new Handler() {
			
			@Override
			public void handleMessage(Message m) {
				HttpResult response = (HttpResult) m.obj;
				
				switch(m.what){
					case HttpConnection.DID_SUCCEED:
					{
						Message msg = new Message();
						
						if(response.status.getStatusCode() == 200){
							boolean success = ParseJSON(response.content);
							msg.getData().putBoolean("success", success);
						}
						else{
							errorType = ERROR_TYPE.HTTP;
							errorMsg = response.status.toString();
							msg.getData().putBoolean("success", false);
						}
						callback.sendMessage(msg);
					}	
						break;
					case HttpConnection.DID_START:
						break;
					case HttpConnection.DID_ERROR:
						errorType = ERROR_TYPE.HTTP;
						errorMsg = response.exception.getMessage();
						Message msg = new Message();
						msg.getData().putBoolean("success", false);
						callback.sendMessage(msg);
						break;
				}
			}
		};
	}
	
	private boolean ParseJSON(String response){
		try {
			JSONObject document = new JSONObject(response);
			JSONObject data = document.getJSONObject("data");
			if(data.has("error")){
				errorType = ERROR_TYPE.LOCATION_NOT_FOUND;
				errorMsg = ((JSONObject)((JSONArray)data.get("error")).get(0)).getString("msg");
				return false;
			}
			else{
				String city = ((JSONObject)data.getJSONArray("request").get(0)).getString("query");
				
				JSONArray weatherJson = data.getJSONArray("weather");
				List<WeatherDay> forecast = new ArrayList<WeatherDay>();
				for(int i = 0; i < weatherJson.length(); i++){
					JSONObject item = (JSONObject)weatherJson.get(i);
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date date = null;
					try {
						date = format.parse(item.getString("date"));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					String iconUrl = ((JSONObject)item.getJSONArray("weatherIconUrl").get(0)).getString("value");
					HttpResult res = HttpConnection.getSync(iconUrl, null, null, true);
					Bitmap icon = res.bitmap;
					
					int tempMin = item.getInt("tempMinC");
					int tempMax = item.getInt("tempMaxC");
					String condition = ((JSONObject)item.getJSONArray("weatherDesc").get(0)).getString("value");
					forecast.add(new WeatherDay(date, condition, iconUrl, WeatherApplication.GetBytesFromBitmap(icon), tempMin, tempMax));
				}
				
				JSONObject currentCondition = (JSONObject)data.getJSONArray("current_condition").get(0);
				Date date = forecast.get(0).date;
				int cloudCover = currentCondition.getInt("cloudcover");
				int pressure = currentCondition.getInt("pressure");
				int temp = currentCondition.getInt("temp_C");
				int humidity = currentCondition.getInt("humidity");
				String iconUrl = ((JSONObject)currentCondition.getJSONArray("weatherIconUrl").get(0)).getString("value");
				int windspeed = currentCondition.getInt("windspeedKmph");
				String condition = ((JSONObject)currentCondition.getJSONArray("weatherDesc").get(0)).getString("value");
				int tempMin = forecast.get(0).tempMin;
				int tempMax = forecast.get(0).tempMax;
				
				HttpResult res = HttpConnection.getSync(iconUrl, null, null, true);
				Bitmap icon = res.bitmap;
				
				CurrentWeather currentWeather = new CurrentWeather(date, cloudCover, pressure, temp, humidity, iconUrl, WeatherApplication.GetBytesFromBitmap(icon), windspeed, condition, tempMin, tempMax);
				
				weather = new Weather(city, currentWeather, forecast);
				try {
					Weather.Serialize(weather, serializedPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorType = ERROR_TYPE.JSON;
			errorMsg = e.getMessage();
			return false;
		}
		return true;
	}
	
	public static byte[] GetBytesFromBitmap(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);   
		return baos.toByteArray();
	}
}
