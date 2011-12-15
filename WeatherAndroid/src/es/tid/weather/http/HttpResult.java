package es.tid.weather.http;

import org.apache.http.StatusLine;

import android.graphics.Bitmap;

public class HttpResult {
	public String content;
	public StatusLine status;
	public Object callBackParams;
	public Exception exception;
	public Bitmap bitmap;
	
	public HttpResult(String content, StatusLine status, Bitmap bitmap){
		this.content = content;
		this.status = status;
		this.bitmap = bitmap;
		this.callBackParams = null;
		this.exception = null;
	}

	public HttpResult(String content, StatusLine status, Bitmap bitmap, Object callBackParams) {
		this.content = content;
		this.status = status;
		this.bitmap = bitmap;
		this.callBackParams = callBackParams;
		this.exception = null;
	}
	
	public HttpResult(Exception exception, Object callBackParams) {
		this.content = null;
		this.status = null;
		this.bitmap = null;
		this.callBackParams = callBackParams;
		this.exception = exception;
	}
}
