package es.tid.weather.splash;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.tid.weather.R;
import es.tid.weather.common.WeatherApplication;
import es.tid.weather.main.WeatherActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	private AnimationDrawable loading;
	private boolean initSuccess;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        
		// Load 'loading' animation
		ImageView image = (ImageView)findViewById(R.id.loadingSplash);
        image.setBackgroundResource(R.drawable.loading);
        loading = (AnimationDrawable) image.getBackground();
        
        ViewTreeObserver loadingObserver = image.getViewTreeObserver();
        loadingObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	@Override
        	public void onGlobalLayout() {
        		loading.start();
        	}
        });
        
        splashThread.start();
	}
    
    private Thread splashThread = new Thread() {
		final long splashTime = 4000;
		
		@Override
		public void run() {
			long now;
			long startTime = System.currentTimeMillis();
			
			try {
				Looper.prepare();

				initSuccess = true;
				
				WeatherApplication application = (WeatherApplication)getApplication();
				application.Init();
				if(application.weather != null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String currentDate = sdf.format(Calendar.getInstance().getTime());
					String prefDate = sdf.format(application.weather.currentWeather.date);
					
					if(!currentDate.equals(prefDate))
						initSuccess = application.UpdateForecastDataSync(application.weather.city);
				}
				
				now = System.currentTimeMillis();
				while ( now - startTime < splashTime ) {
				    sleep(100);
				    now = System.currentTimeMillis();
				}
				
				Intent intent = new Intent(SplashActivity.this, WeatherActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("success", initSuccess);
				startActivity(intent);
				finish();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}