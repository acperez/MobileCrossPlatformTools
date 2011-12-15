package es.tid.weather.http;

import java.util.ArrayList;

import org.apache.http.HttpHost;

import android.content.Context;
import android.net.Proxy;

public class ConnectionManager {
    
    public static final int MAX_CONNECTIONS = 5;

    private ArrayList<Runnable> active = new ArrayList<Runnable>();
    private ArrayList<Runnable> queue = new ArrayList<Runnable>();

    private static ConnectionManager instance;
    
    public static HttpHost proxy = null;

    public static ConnectionManager getInstance() {
         if (instance == null)
              instance = new ConnectionManager();
         return instance;
    }

    public static ConnectionManager getInstance(Context context) {
        if (instance == null){
        	instance = new ConnectionManager();
        	String host = Proxy.getHost(context);
    		if (host != null) {
    			proxy = new HttpHost(host, Proxy.getPort(context));
    		}
        }
        return instance;
   }
    
    public void push(Runnable runnable) {
         queue.add(runnable);
         if (active.size() < MAX_CONNECTIONS)
              startNext();
    }

    private void startNext() {
         if (!queue.isEmpty()) {
              Runnable next = queue.get(0);
              queue.remove(0);
              active.add(next);

              Thread thread = new Thread(next);
              thread.start();
         }
    }

    public void didComplete(Runnable runnable) {
         active.remove(runnable);
         startNext();
    }
    
    public void setProxy(String ip, int port) {
    	if (ip != null) {
    		proxy = new HttpHost(ip, port);
    	} else {
    		proxy = null;
    	}
    }
}