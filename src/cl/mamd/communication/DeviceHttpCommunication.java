package cl.mamd.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.util.Base64;
import android.util.Log;
import java.net.URL;

public class DeviceHttpCommunication {
	
	private final String TAGNAME = "DeviceHttpCommunication";
	
	private String URL;
	private String username;
	private String passwd;
	// 1 PUT / 2 GET
	private Integer option;
	private String author_String;
	

	private CredentialsProvider credProvider;
	
	public DeviceHttpCommunication(String IP,String username,String passwd,Integer opt){
		this.username = username;
		this.passwd = passwd;
		//this.URL = "http://"+IP;
		this.URL = IP;
		this.option = opt;
		
		this.credProvider = new BasicCredentialsProvider();
	    this.credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
	        new UsernamePasswordCredentials(username,passwd));
		
	    String authorization = this.username + ":" + this.passwd;
	    this.author_String = Base64.encodeToString(authorization.getBytes(),0);	    
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

	public boolean executeInstructionSocket(String instruction){
		final String path = "/"+instruction;
		Thread t = new Thread(new Runnable() {
	    	public void run() {
		
		try {
			String params = URLEncoder.encode("param1", "UTF-8")
					+ "=" + URLEncoder.encode("value1", "UTF-8");
					            params += "&" + URLEncoder.encode("param2", "UTF-8")
					+ "=" + URLEncoder.encode("value2", "UTF-8");
					  
					            int port = 80;
					            InetAddress addr = InetAddress.getByName("local.opendomo.com");
					            Socket socket = new Socket(addr, port);
					            
					            BufferedWriter wr =
					     new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
					            wr.write("GET "+path+" HTTP/1.0rn");
					            //wr.write("Content-Length: "+params.length()+"rn");
					            wr.write("Content-Type: text/plain");
					            wr.write("Authorization: Basic "+author_String);
					            wr.write("rn");
					
					            // Send parameters
					            wr.flush();
					            // Get response
					            BufferedReader rd = new BufferedReader(
					            		new InputStreamReader(socket.getInputStream()));
					            String line;
					            Log.i(TAGNAME,"Start reading response");
					            while ((line = rd.readLine()) != null) {
					                Log.i(TAGNAME,"line:"+line);
					
					            }
					            Log.i(TAGNAME,"Stop reading response");
					             
					
					            wr.close();
					            socket.close();
					            rd.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAGNAME, "Error:"+e.toString());
			
		}
		
	    	}
	    });
	    t.start();
	    return true;
	}
	
	public boolean executeInstruction(String instruction){
		
		
		
		final DefaultHttpClient client = new DefaultHttpClient();
	    client.setCredentialsProvider(credProvider);
	    
	    
	    
	    Log.i(TAGNAME, client.getCredentialsProvider().toString());
	    
	    URL = URL + "/" + instruction;
	    
	    
	    if ( this.option == 1 ) {
	    	//PUT
	    	final HttpPut request = new HttpPut(URL);
	    	String data = "";
	    	try {
				request.setEntity(new StringEntity(data, "UTF8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	request.addHeader("Content-type","");
	    	Thread t = new Thread(new Runnable() {
		    	public void run() {
		    		
		    		try {
		    			
		    			Log.i(TAGNAME,"PUT");
		    			final DefaultHttpClient client = new DefaultHttpClient();
		    		    client.setCredentialsProvider(credProvider);
		    			
		    			
		    			HttpResponse response;
		    			response = client.execute(request);
		    					//http.execute(get);
		    			
		    			
		    			Log.i(TAGNAME, "Response status:"+Integer.toString(response.getStatusLine().getStatusCode()));
		    			if ( response.getStatusLine().getStatusCode() == 200) {
		    				String responseString = new BasicResponseHandler().handleResponse(response);
		    				Log.i(TAGNAME, "response to string: "+responseString);
		    			}
		    			Log.i(TAGNAME, "response to string: "+response.getStatusLine().getReasonPhrase());
		    			Log.d(TAGNAME, "This is what we get back:"+response.getStatusLine().toString()+", "
		    		    		+response.getEntity().toString());
		    		} catch (ClientProtocolException e1) {
		    			// TODO Auto-generated catch block Log.e(getClass().getSimpleName(), "HTTP protocol error", e);
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
	    	
	    	
	    	
	    	
	    }
	    else {
	    	//GET
	    	
	    	Thread t = new Thread(new Runnable() {
		    	public void run() {
		    		
		    		try {

		    			Log.i(TAGNAME,"GET");
		    			final HttpGet request = new HttpGet(URL);
		    			
		    			request.setHeader("Accept","*/*");
		    			request.setHeader("Connection","close");
		    			request.setHeader("Content-Type", "text/plain; charset=utf-8");
		    			request.setHeader("Expect", "100-continue");
		    			
		    			Header[] header = request.getAllHeaders();
		    			for (int i = 0 ; i < header.length ; i++)
		    				Log.i(TAGNAME, "Header:"+header[i]);
		    		
		    	    	Log.i(TAGNAME, "URL for REQUEST:"+request.getURI());
		    			final DefaultHttpClient client = new DefaultHttpClient();
		    		    client.setCredentialsProvider(credProvider);
		    			
		    		 
		    		    HttpResponse response = client.execute(request);
		    		    response.getEntity().consumeContent();
		    			
//		    			BasicResponseHandler responseHandler = new BasicResponseHandler();//Here is the change
	//	    	        String responseBody = client.execute(request, responseHandler);
		    	        
//		    	        Log.i(TAGNAME,responseBody);
		    					//http.execute(get);
		    			
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
		    			Log.e(getClass().getSimpleName(), "HTTP protocol error", e1);
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
	    	
	    	
	    	
	    	
	    	
	    }

	    
		
	    
	    
		return false;
	    
	}
  
}
