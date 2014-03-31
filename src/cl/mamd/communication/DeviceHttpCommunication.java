package cl.mamd.communication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import android.util.Base64;
import android.util.Log;

public class DeviceHttpCommunication {
	
	private final String TAGNAME = "DeviceHttpCommunication";
	
	private String URL;
	private HttpUriRequest request;
	private String username;
	private String passwd;
	private String credentials;
	private String base64EncodedCredentials;
	private CredentialsProvider credProvider;
	
	public DeviceHttpCommunication(String URL,String username,String passwd){
		this.username = username;
		this.passwd = passwd;
		this.URL = URL;
		this.credentials = username + ":" + passwd;
		
		this.credProvider = new BasicCredentialsProvider();
	    this.credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
	        new UsernamePasswordCredentials(username,passwd));
		
		
		this.base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(),
				Base64.NO_WRAP);
		
	}

	public boolean executeInstruction(String intruction){
		
		final DefaultHttpClient http = new DefaultHttpClient();
	    http.setCredentialsProvider(credProvider);
	    
	    String data = "YOUR REQUEST BODY HERE";
	    URL = URL + "/?SET=luz01&option=ON";
		 Log.i(TAGNAME, "URL for REQUEST:"+URL);
	    final HttpPut put = new HttpPut(URL);
	    
	    Thread t = new Thread(new Runnable() {
	    	public void run() {
	    		try {
	    			 String data = "YOUR REQUEST BODY HERE";
	    			 
	    			HttpPut put = new HttpPut(URL);
	    	        put.setEntity(new StringEntity(data, "UTF8"));
	    	    } catch (UnsupportedEncodingException e) {
	    	        Log.e(TAGNAME, "UnsupportedEncoding: ", e);
	    	    }
	    	    put.addHeader("Content-type","SET CONTENT TYPE HERE IF YOU NEED TO");
	    	   
	    		try {
	    			HttpResponse response;
	    			response = http.execute(put);
	    			
	    			if ( response.getStatusLine().getStatusCode() == 200) {
	    				Log.i(TAGNAME, "Response status:"+Integer.toString(response.getStatusLine().getStatusCode()));
	    				String responseString = new BasicResponseHandler().handleResponse(response);
	    				Log.i(TAGNAME, "response to string: "+responseString);
	    			}
	    			Log.i(TAGNAME, "response to string: "+response.getStatusLine().getReasonPhrase());
	    			Log.d(TAGNAME, "This is what we get back:"+response.getStatusLine().toString()+", "
	    		    		+response.getEntity().toString());
	    			
	    		} catch (ClientProtocolException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    			Log.e(TAGNAME, e1.toString());
	    		} catch (IOException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    			Log.e(TAGNAME, e1.toString());
	    		}
	    	}
	    });
	    t.start();
		return false;
	    
		/*
	    
		
		HttpUriRequest request = new HttpGet(this.URL);
		request.addHeader("Authorization", "Basic " + base64EncodedCredentials);
		Log.i(TAGNAME, "HEADER:"+request.getHeaders("Authorization").toString());
		
		HttpClient httpclient = new DefaultHttpClient();  
		try {
			HttpResponse response;
			response = httpclient.execute(request);
			
			Log.i(TAGNAME,request.toString());
			Log.i(TAGNAME,response.getStatusLine().toString());
			return true;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAGNAME, e.toString());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAGNAME, e.toString());
			return false;
		}
		*/
	}
  
}
