package es.tid.weather.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.tid.weather.R;
import es.tid.weather.common.Definitions.GPS_CALLBACK_STATUS;
import es.tid.weather.common.CurrentWeather;
import es.tid.weather.common.Weather;
import es.tid.weather.common.WeatherApplication;
import es.tid.weather.common.WeatherDay;
import es.tid.weather.opengl.TranslucentGLSurfaceViewActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {
	private GpsListener gpsListener;
	Dialog alertDialog;
	ListViewAdapter listViewAdapter;
	ListView list;
	WeatherApplication application;
	private AnimationDrawable loading;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);

		gpsListener = new GpsListener(this, null);
		gpsListener.SetCallback(gpsCallback);
		
		list = (ListView)findViewById(R.id.list);
        listViewAdapter = new ListViewAdapter(this, R.layout.day_item_layout, new ArrayList<WeatherDay>());
        list.setAdapter(listViewAdapter);
        
        application = (WeatherApplication)getApplication();
        if(application.weather == null){
        	((LinearLayout)findViewById(R.id.help_layout)).setVisibility(View.VISIBLE);
        	((LinearLayout)findViewById(R.id.forecast_layout)).setVisibility(View.GONE);
        }
        else{
        	((LinearLayout)findViewById(R.id.forecast_layout)).setVisibility(View.VISIBLE);
        	((LinearLayout)findViewById(R.id.help_layout)).setVisibility(View.GONE);
        	
			InitFromPreferences(application.weather);
        }
        
		ImageView image = (ImageView)findViewById(R.id.loading);
        image.setBackgroundResource(R.drawable.loading);
        loading = (AnimationDrawable) image.getBackground();
        
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				WeatherDay item = (WeatherDay)listViewAdapter.getItem(position);
				WeatherApplication.texture = item.icon;
				
				Intent intent = new Intent(WeatherActivity.this, TranslucentGLSurfaceViewActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				
			}
		});
	}
	
	Handler gpsCallback = new Handler() {

		@Override
		public void handleMessage(Message m) {
			switch(GPS_CALLBACK_STATUS.values()[m.what]){
				case TIMEOUT:
					
					Toast.makeText(WeatherActivity.this, getString(R.string.gps_timeout), Toast.LENGTH_LONG).show();
					
					break;
				case NOTIFY:
					
					Location location = (Location) m.getData().get("location");
					Geocoder geocoder = new Geocoder(WeatherActivity.this, Locale.getDefault());
					
					String query = null;
					
					try {
						List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
						
						if (addresses != null && addresses.size() > 0)
							query = addresses.get(0).getLocality();
						else
							query = location.getLatitude() + "," + location.getLongitude();
	
					} catch (IOException e) {
						e.printStackTrace();
						query = location.getLatitude() + "," + location.getLongitude();
					}
					
					application.UpdateForecastData(query, updateHandler);
					
					break;
			}
		}
	};
	
	Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message m) {
			HideLoading();
			boolean success = m.getData().getBoolean("success");
			if(success)
				InitFromPreferences(application.weather);
			else{
				alertDialog = ShowAlertDialog(WeatherActivity.this, getString(R.string.alert_error_title),
						application.errorMsg, getString(R.string.alert_ok_button),
						new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								alertDialog.dismiss();
							}
						}, null, null);
			}
		}
	};
	
	private void InitFromPreferences(Weather weather){
		((TextView)findViewById(R.id.city)).setText(weather.city);
		
		listViewAdapter.clear();
		for(int i=1; i<weather.forecast.size();i++){
			listViewAdapter.add(weather.forecast.get(i));
		}
		
		UpdateCurrentWeather(weather.currentWeather);
		
		listViewAdapter.notifyDataSetChanged();
		list.invalidate();
		
    	((LinearLayout)findViewById(R.id.forecast_layout)).setVisibility(View.VISIBLE);
    	((LinearLayout)findViewById(R.id.help_layout)).setVisibility(View.GONE);
	}
	
	private void UpdateCurrentWeather(CurrentWeather currentWeather){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		((TextView)findViewById(R.id.current_date)).setText(sdf.format(currentWeather.date));
		((TextView)findViewById(R.id.current_temp)).setText(currentWeather.temp + "ºC");
		((ImageView)findViewById(R.id.current_icon)).setImageBitmap(currentWeather.icon);
		
		((TextView)findViewById(R.id.current_condition)).setText(currentWeather.condition);
		((TextView)findViewById(R.id.current_temp_min)).setText("Min temp: " + currentWeather.tempMin + "ºC");
		((TextView)findViewById(R.id.current_temp_max)).setText("Min temp: " + currentWeather.tempMax + "ºC");
		((TextView)findViewById(R.id.current_pressure)).setText("Pressure: " + currentWeather.pressure + "mb");
		((TextView)findViewById(R.id.current_humidity)).setText("Humidity: " + currentWeather.humidity + "%");
		((TextView)findViewById(R.id.current_cloud_cover)).setText("Cloud cover: " + currentWeather.cloudCover + "%");
		((TextView)findViewById(R.id.current_windspeed)).setText("Wind speed: " + currentWeather.windspeed + "km/h");
	}

	public class ListViewAdapter extends ArrayAdapter<WeatherDay> {
        
        private ArrayList<WeatherDay> items;
        private ViewHolder holder;
        
        class ViewHolder {
            TextView day;
            ImageView icon;
            TextView condition;
            TextView low_temp;
            TextView high_temp;
        }
        
        public ListViewAdapter(Context context, int textViewResourceId, ArrayList<WeatherDay> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            
            if(v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.day_item_layout, null);
                
                holder = new ViewHolder();
                
                holder.day = (TextView) v.findViewById(R.id.day);
                holder.icon = (ImageView) v.findViewById(R.id.icon);
                holder.condition = (TextView) v.findViewById(R.id.condition);
        		holder.low_temp = (TextView) v.findViewById(R.id.low_temp);
				holder.high_temp = (TextView) v.findViewById(R.id.high_temp);
				
                v.setTag(holder);
            }
            else
                holder = (ViewHolder) v.getTag();
            
            WeatherDay listViewItem = items.get(position);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date = sdf.format(listViewItem.date);
            holder.day.setText(date);
            holder.icon.setImageBitmap(listViewItem.icon);
            holder.condition.setText(listViewItem.condition);
            holder.low_temp.setText("Min temp: " + listViewItem.tempMin + "ºC");
            holder.high_temp.setText("Max temp: " + listViewItem.tempMax + "ºC");
            
            return v;
        }
    }
	
    @Override
    protected void onDestroy() {
        gpsListener.StopService();
        super.onDestroy();
    }
    
    @Override
    protected void onPause() {
        gpsListener.StopService();
        super.onPause();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu_layout, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.gps_location:
		        gpsListener.StartService();
		        return true;
		    case R.id.city:
		    	
		    	alertDialog = ShowAlertCityDialog(this, getString(R.string.alert_title), getString(R.string.alert_msg), getString(R.string.alert_ok_button), new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						gpsListener.StopService();
						String query = ((EditText)alertDialog.findViewById(R.id.alert_dialog_city_input)).getText().toString();
						application.UpdateForecastData(query, updateHandler);
						ShowLoading();
						alertDialog.dismiss();
					}
				}, getString(R.string.alert_cancel_button), new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						alertDialog.dismiss();
					}
				}, getString(R.string.alert_form_title), "");

		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	public static Dialog ShowAlertDialog(Context context, String title, String message, String btn_ok_msg, OnClickListener btn_ok_listener, String btn_cancel_msg, OnClickListener btn_cancel_listener){
		return ShowAlertDialog(context, title, new SpannableString(message), btn_ok_msg, btn_ok_listener, btn_cancel_msg, btn_cancel_listener);
	}

	public static Dialog ShowAlertDialog(Context context, String title, Spannable message, String btn_ok_msg, OnClickListener btn_ok_listener, String btn_cancel_msg, OnClickListener btn_cancel_listener){
		Dialog alertDialog = new Dialog(context, R.style.dialog_style);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.alert_dialog, null);

	    TextView titleView = (TextView)layout.findViewById(R.id.alert_dialog_title);
	    if(title != null)
	    	titleView.setText(title);
	    else
	    	titleView.setVisibility(View.GONE);

    	((TextView)layout.findViewById(R.id.alert_dialog_msg)).setText(message);
		
	    Button ok = (Button)layout.findViewById(R.id.alert_dialog_ok_btn);
	    Button cancel = ((Button)layout.findViewById(R.id.alert_dialog_cancel_btn));
	    
	    if(btn_ok_msg != null){
			ok.setText(btn_ok_msg);
			ok.setOnClickListener(btn_ok_listener);
	    }
	    else{
	    	ok.setVisibility(View.GONE);
	    	((LinearLayout)layout.findViewById(R.id.alert_dialog_btn_group)).setWeightSum(1.0f);
	    }
	    
	    if(btn_cancel_msg != null){
			cancel.setText(btn_cancel_msg);
			cancel.setOnClickListener(btn_cancel_listener);
	    }
	    else{
	    	cancel.setVisibility(View.GONE);
	    	((LinearLayout)layout.findViewById(R.id.alert_dialog_btn_group)).setWeightSum(1.0f);
	    }
	    	
	    if(btn_cancel_msg != null && btn_ok_msg != null){
	    	((LinearLayout)layout.findViewById(R.id.alert_dialog_left_margin)).setVisibility(View.GONE);
	    	((LinearLayout)layout.findViewById(R.id.alert_dialog_right_margin)).setVisibility(View.GONE);
	    }
    	
	    alertDialog.setContentView(layout);
	    alertDialog.show();
	    
		return alertDialog;
	}
	
	public static Dialog ShowAlertCityDialog(Context context, String title, String message, String btn_ok_msg, OnClickListener btn_ok_listener, String btn_cancel_msg, OnClickListener btn_cancel_listener, String city_msg, String error_msg){
		return ShowAlertCityDialog(context, title, new SpannableString(message), btn_ok_msg, btn_ok_listener, btn_cancel_msg, btn_cancel_listener, city_msg, error_msg);
	}

	public static Dialog ShowAlertCityDialog(Context context, String title, Spannable message, String btn_ok_msg, OnClickListener btn_ok_listener, String btn_cancel_msg, OnClickListener btn_cancel_listener, String city_msg, String error_msg){
		Dialog alertDialog = ShowAlertDialog(context, title, message, btn_ok_msg, btn_ok_listener, btn_cancel_msg, btn_cancel_listener);
		
		if(city_msg != null){
    		((TextView)alertDialog.findViewById(R.id.alert_dialog_city_msg)).setVisibility(View.VISIBLE);
    		((TextView)alertDialog.findViewById(R.id.alert_dialog_city_msg)).setText(city_msg);
    		((TextView)alertDialog.findViewById(R.id.alert_dialog_city_input)).setVisibility(View.VISIBLE);
    		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	}
    			
    	if(error_msg != null){
    		((TextView)alertDialog.findViewById(R.id.alert_dialog_error_msg)).setText(error_msg);
    		((TextView)alertDialog.findViewById(R.id.alert_dialog_error_msg)).setVisibility(View.VISIBLE);
    	}
	    
		return alertDialog;
	}
	
	public void ShowLoading(){
		loading.start();
		((LinearLayout)findViewById(R.id.loadingSplashContainer)).setVisibility(View.VISIBLE);
		list.setEnabled(false);
	}
	
	public void HideLoading(){
		loading.stop();
		((LinearLayout)findViewById(R.id.loadingSplashContainer)).setVisibility(View.GONE);
		list.setEnabled(true);
	}
}
