package es.tid.weather.main;

import es.tid.weather.common.Definitions;
import es.tid.weather.common.Definitions.GPS_CALLBACK_STATUS;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

public class GpsListener implements LocationListener{
	public Location location = null;
	private Handler handler = null;
	private LocationManager locationManager = null;
	private CountDownTimer timer = null;
	
	public GpsListener(Context context, Handler callback){
		this.handler = callback;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		timer = new CountDownTimer(Definitions.GPS_TIMEOUT_MILIS, Definitions.GPS_TIMEOUT_MILIS) {
			
			@Override
			public void onTick(long arg0) {}
			
			@Override
			public void onFinish() {
				StopService();
				if(handler != null)
					handler.sendEmptyMessage(GPS_CALLBACK_STATUS.TIMEOUT.ordinal());
			}
		};
	}
	
	public void SetCallback(Handler callback){
		this.handler = callback;
	}
	
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if(handler != null){
        	StopService();
	        Message msg = new Message();
	        msg.getData().putParcelable("location", location);
	        msg.what = GPS_CALLBACK_STATUS.NOTIFY.ordinal();
	        handler.sendMessage(msg);
	        timer.cancel();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }    

    @Override
    public void onProviderEnabled(String provider) {
    }    

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    
    public void StartService(){
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    	timer.start();
    }
    
    public void StopService(){
    	if (locationManager != null){
        	locationManager.removeUpdates(this);
        	timer.cancel();
    	}
    }
}
