package es.tid.weather.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class HttpConnection implements Runnable {
 
	static final String CONTENT_TYPE = "Content-Type";
	static final String FORM_URL = "application/x-www-form-urlencoded";
	
     public static final int DID_START = 0;
     public static final int DID_ERROR = 1;
     public static final int DID_SUCCEED = 2;
 
     protected static final int GET = 0;
     protected static final int POST = 1;
     protected static final int PUT = 2;
     protected static final int DELETE = 3;
     protected static final int BITMAP = 4;
 
     protected String url;
     protected int method;
     protected Handler handler;
     protected String data;
     protected HttpEntity entity;
     protected String[] headers;
     protected Object callBackParams;
     protected String saveFilePath;
     protected HttpHost proxyHost;
 
     protected HttpClient httpClient;
 
     public HttpConnection() {
          this(new Handler());
     }
 
     public HttpConnection(Handler _handler) {
          handler = _handler;
     }
     
     public HttpConnection(Handler _handler, Object callBackParams) {
    	 this(_handler);
    	 this.callBackParams = callBackParams;
     }
     
     public HttpConnection(Handler _handler, String saveFilePath) {
    	 this(_handler);
    	 this.saveFilePath = saveFilePath;
     }
     
     public HttpConnection(Handler _handler, Object callBackParams, String saveFilePath) {
    	 this(_handler);
    	 this.callBackParams = callBackParams;
    	 this.saveFilePath = saveFilePath;
     }
          
	 public void create(int method, String url, String data) {
		 create(method, url, data, null);
	 }
 
     public void create(int method, String url, String data, String[] headers) {
          this.method = method;
          this.url = url;
          this.data = data;
          this.headers = headers;
          ConnectionManager.getInstance().push(this);
     }
     
     private void create(int method, String url, HttpEntity data, String[] headers){
    	 this.method = method;
         this.url = url;
         this.entity = data;
         this.headers = headers;
         ConnectionManager.getInstance().push(this);
     }
     
     public static DefaultHttpClient createStandardHttpClient()
     {
    	 SchemeRegistry schemeRegistry = new SchemeRegistry();
    	 schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    	 final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
    	 sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    	 schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
    	 
    	 HttpParams httpparams = new BasicHttpParams();
    	 DefaultHttpClient httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpparams, schemeRegistry), httpparams);

    	 return httpclient;
     }
 
     public void get(String url, String[] headers) {
          create(GET, url, (String)null, headers);
     }
     
     public static HttpResult getSync(String url, String[] headers, String saveFilePath, boolean isBitmap) throws Exception {
    	 HttpResult response = null;
    	 
    	 HttpGet httpGet = new HttpGet(url);  
    	 
    	 if (headers != null)
    		 for (int i=0; i<headers.length-1; i+=2) 
    			 httpGet.addHeader(headers[i], headers[i+1]);
    		 
    	 httpGet.addHeader("accept", "*/*");
    	 
    	 HttpClient httpClient = createStandardHttpClient();

		//Proxy setting
    	 HttpHost proxyHost = null;
    	 if (new URI(url).getScheme().equals("http") && ConnectionManager.proxy != null) {
    		 httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, ConnectionManager.proxy);
    		 proxyHost = new HttpHost(ConnectionManager.proxy.getHostName(), ConnectionManager.proxy.getPort(), ConnectionManager.proxy.getSchemeName());
         }
    	 
    	 HttpResponse resp = null;
    	 
    	 if (proxyHost != null)
    		 resp = httpClient.execute(proxyHost, httpGet);
    	 else
    		 resp = httpClient.execute(httpGet);
    	 
    	 if(saveFilePath != null){
    		 InputStream is = resp.getEntity().getContent();
    		 
    		 int length;
    		 int bufferSize = 1024;
    		 byte[] buffer = new byte[bufferSize];
    		 File file = new File(saveFilePath);
    		 file.createNewFile();
    		 FileOutputStream fos = new FileOutputStream(file);
    		 
    		 while((length = is.read(buffer)) != -1)
    			 fos.write(buffer, 0, length);
    		 
    		 fos.close();
    		 is.close();
    		 
    		 response = new HttpResult(saveFilePath, resp.getStatusLine(), null);
    	 }
    	 else{
    		 if(!isBitmap){
            	 BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
                 String line, result = "";
                 while ((line = br.readLine()) != null)
                      result += line;
                 
                 response = new HttpResult(result, resp.getStatusLine(), null);
    		 }
    		 else{
    	          BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(resp.getEntity());
    	          Bitmap bm = BitmapFactory.decodeStream(bufHttpEntity.getContent());
    	          response = new HttpResult(null, resp.getStatusLine(), bm);
    		 }
    	 }

         return response;
     }
     
     public void post(String url, String data, String[] headers) throws UnsupportedEncodingException {
    	 post(url, new StringEntity(data), headers);
     }
     
     public void post(String url, HttpEntity data, String[] headers){
    	 create(POST, url, data, headers);
     }
 
     public void put(String url, String data) {
          create(PUT, url, data);
     }
 
     public void delete(String url) {
          create(DELETE, url, null);
     }
 
     public void bitmap(String url, String[] headers) {
          create(BITMAP, url, (String)null, headers);
     }
 
     public void run() {
          handler.sendMessage(Message.obtain(handler, HttpConnection.DID_START));
          httpClient = createStandardHttpClient();
          HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 8000);	//8 seg timeout

          try {
        	  proxyHost = null;
              if (new URI(url).getScheme().equals("http") && ConnectionManager.proxy != null) {
            	  httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, ConnectionManager.proxy);
            	  proxyHost = new HttpHost(ConnectionManager.proxy.getHostName(), ConnectionManager.proxy.getPort(), ConnectionManager.proxy.getSchemeName());
              }
              
               HttpResponse response = null;
               switch (method) {
               case GET:
            	   response = doHttpGet();
                    break;
               case POST:
            	   response = doHttpPost();
                    break;
               case PUT:
            	   response = doHttpPut();
                    break;
               case DELETE:
            	   response = doHttpDelete();
                    break;
               case BITMAP:
            	   processBitmapEntity(doHttpGetBitmap());
                   break;
               }
               if (method < BITMAP)
                    processEntity(response);
          } catch (Exception e) {
        	  HttpResult resp = new HttpResult(e, callBackParams);
        	  Message message = Message.obtain(handler, DID_ERROR, resp);
               handler.sendMessage(message);
          }
          ConnectionManager.getInstance().didComplete(this);
     }
     
     private void addHeaders(HttpRequestBase _httpRequest){
    	 if (headers != null)
    		 for (int i=0; i<headers.length-1; i+=2) 
    			 _httpRequest.addHeader(headers[i], headers[i+1]);
    		 
    	 _httpRequest.addHeader("accept", "*/*");
     }
     
     private HttpResponse doHttpGet() throws ClientProtocolException, IOException{
    	 HttpGet httpGet = new HttpGet(url);
    	 addHeaders(httpGet);
    	 httpGet.addHeader(CONTENT_TYPE, FORM_URL);  	   
  	    return doExecute(httpGet);
     }
     
     private HttpResponse doHttpPost() throws ClientProtocolException, IOException{
    	 HttpPost httpPost = new HttpPost(url);
    	 addHeaders(httpPost);
    	 
         httpPost.setEntity(entity);    
         return doExecute(httpPost);         
     }
     
     private HttpResponse doHttpPut() throws ClientProtocolException, IOException{
    	 HttpPut httpPut = new HttpPut(url);
         httpPut.setEntity(new StringEntity(data));         
         return doExecute(httpPut);
     }
 
     private HttpResponse doHttpDelete() throws ClientProtocolException, IOException{
    	 return doExecute(new HttpDelete(url));   	 
     }
     
     private HttpResponse doHttpGetBitmap() throws ClientProtocolException, IOException{    	 
    	 HttpGet httpGet = new HttpGet(url);
    	 addHeaders(httpGet);
    	 return doExecute(httpGet);
     }       
     
     protected HttpResponse doExecute(HttpUriRequest _request) throws ClientProtocolException, IOException{    	 
    	 if (proxyHost != null)
    		 return httpClient.execute(proxyHost, _request);
    	 else
    		 return httpClient.execute(_request);
     }          

     private void processEntity(HttpResponse resp) throws IllegalStateException, IOException {
    	 HttpResult response = null;
    	 
    	 if(saveFilePath != null){
    		 InputStream is = resp.getEntity().getContent();
    		 
    		 int length;
    		 int bufferSize = 1024;
    		 byte[] buffer = new byte[bufferSize];
    		 File file = new File(saveFilePath);
    		 FileOutputStream fos = new FileOutputStream(file);
    		 
    		 while((length = is.read(buffer)) != -1)
    			 fos.write(buffer, 0, length);
    		 
    		 fos.close();
    		 is.close();
    		 
    		 response = new HttpResult(saveFilePath, resp.getStatusLine(), null);
    	 }
    	 else{
    		 String result = readBody(resp);
    		 response = new HttpResult(result, resp.getStatusLine(), null, callBackParams);
    	 }
    	 
    	 Message message = Message.obtain(handler, DID_SUCCEED, response);
    	 handler.sendMessage(message);
     }
     
     private String readBody(HttpResponse response) throws IllegalStateException, IOException {
    	 BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
         String line, result = "";
         while ((line = br.readLine()) != null)
              result += line;
         
         return result;
     }
 
     private void processBitmapEntity(HttpResponse response) throws IOException {
          BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
          Bitmap bm = BitmapFactory.decodeStream(bufHttpEntity.getContent());
          handler.sendMessage(Message.obtain(handler, DID_SUCCEED, new Object[]{bm, response, callBackParams}));
     }
}