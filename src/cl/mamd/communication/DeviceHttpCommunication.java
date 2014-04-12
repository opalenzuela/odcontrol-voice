package cl.mamd.communication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;

public class DeviceHttpCommunication {
	
	private final String TAGNAME = "DeviceHttpCommunication";
	
	private String URL;
	private String username;
	private String passwd;

	private CredentialsProvider credProvider;
	
	public DeviceHttpCommunication(String IP,String username,String passwd){
		this.username = username;
		this.passwd = passwd;
		this.URL = "http://"+IP;
		
		this.credProvider = new BasicCredentialsProvider();
	    this.credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
	        new UsernamePasswordCredentials(username,passwd));
		
		
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public boolean executeInstruction(String instruction){
		
		final DefaultHttpClient http = new DefaultHttpClient();
	    http.setCredentialsProvider(credProvider);
	    
	    URL = URL + "/" + instruction;
		Log.i(TAGNAME, "URL for REQUEST:"+URL);
	    final HttpPut put = new HttpPut(URL);
	    
	    Thread t = new Thread(new Runnable() {
	    	public void run() {
	    		try {
	    			String data = "";
	    			HttpPut put = new HttpPut(URL);
	    	        put.setEntity(new StringEntity(data, "UTF8"));
	    	    } catch (UnsupportedEncodingException e) {
	    	        Log.e(TAGNAME, "UnsupportedEncoding: ", e);
	    	    }
	    	    put.addHeader("Content-type","");
	    	   
	    		try {
	    			HttpResponse response;
	    			response = http.execute(put);
	    			Log.i(TAGNAME, "Response status:"+Integer.toString(response.getStatusLine().getStatusCode()));
	    			if ( response.getStatusLine().getStatusCode() == 200) {
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
	    
	}
  
}
