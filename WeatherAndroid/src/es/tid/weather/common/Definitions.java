package es.tid.weather.common;

public class Definitions {

	public static final int GPS_TIMEOUT_MILIS = 20000;
	
	public static enum GPS_CALLBACK_STATUS { TIMEOUT, NOTIFY };
	public static enum ERROR_TYPE { NONE, JSON, HTTP, LOCATION_NOT_FOUND };
	
	public static final String API_KEY = "6affebacbb125602110709";
	public static final String DATA_FORMAT = "json";
	public static final String FORECAST_DAYS = "5";
	
	public static final String WEATHER_URL = "http://free.worldweatheronline.com/feed/weather.ashx?key=" + API_KEY + "&q=%s&num_of_days=" + FORECAST_DAYS + "&format=" + DATA_FORMAT;
	
}