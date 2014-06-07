/**
Copyright 2014 Manuel Moscoso Dominguez
This file is part of ODControl-Voice.

ODCOntrol-Voice is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation, either version 3 of the License, or 
(at your option) any later version.

ODControl-Voice is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License for more details.

You should have received a copy of the GNU General Public License 
along with ODControl-Voice.  If not, see <http://www.gnu.org/licenses/>.

**/
package cl.mamd.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cl.mamd.entity.NodoDevice;
import cl.mamd.voice.R;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class HTTPExecuteInstruction extends AsyncTask<String, Void, String> {
	
	private Context context;	
	private EditText edittext;
	
	private final String TAGNAME = "HTTPCheckAuthentication";
	
	private NodoDevice nodo;
	private String path = "";
	
	private String author_String;
	private Boolean results;
	private int error = 0;
	
	/**
	 * 
	 * @param context
	 * @param IP
	 * @param username
	 * @param passwd
	 */
	public HTTPExecuteInstruction(Context context,NodoDevice nodoexe,String instruction,EditText text){
		this.context = context;
		this.nodo = nodoexe;
		this.results = false;
		this.edittext = text;
		 
		String authorization = this.nodo.getUsername() + ":" + this.nodo.getPasswd();
		 this.author_String = Base64.encodeToString(authorization.getBytes(),0);
		 
		this.path = "/"+instruction;
	}

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		//Execute HTTP Basic Authentication Test
		
		try {
			int port = 80;
			InetAddress addr = InetAddress.getByName(nodo.getIpaddress());
            
			Log.i(TAGNAME, "addr:"+addr);
			
			Socket socket = new Socket();
			socket.setSoTimeout(10000);
			socket.connect(new InetSocketAddress(addr, port), 10000);
			
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            wr.write("GET "+path+" HTTP/1.0rn");
            wr.write("Content-Type: text/plain");
            wr.write("Authorization: Basic "+author_String);
            wr.write("rn");
            
            // Send parameters
            wr.flush();
            // Get response
            BufferedReader rd = new BufferedReader(
            		new InputStreamReader(socket.getInputStream()));
            
            String line;
            int cont = 0;
            
            Log.i(TAGNAME,"Start reading response");
            while ((line = rd.readLine()) != null) {
                Log.i(TAGNAME,"line:"+line);
                if ( line.contains("Unauthorized") )
                	cont++;
            }
            
            if ( cont == 0 )
            	this.results = true;
        
            
            Log.i(TAGNAME,"Stop reading response");
            
            wr.close();
            socket.close();
            rd.close();

			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.i(TAGNAME, "UnknownHostException:");
			e.printStackTrace();
		}
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAGNAME, "IOException:");
			Log.i(TAGNAME, "IOException-e.getMessage().toString():"+ e.getMessage().toString());
			Log.i(TAGNAME, "IOException-Integer.toString(e.hashCode()):"+ Integer.toString(e.hashCode()));
			Log.i(TAGNAME, "IOException-e.getLocalizedMessage().toString():"+ e.getLocalizedMessage().toString());
			if ( e.getMessage().toString().contains("ECONNREFUSED")) {
				error = 1;
			}
			if ( e.getMessage().toString().contains("failed to connect to")) {
				error = 2;
			}
		}
		
		
		return null;
	}

	@Override
	protected void onPostExecute(String unused) {
	    
	    if ( this.results ) {
	    	this.edittext.setBackground(
	    			this.context.getResources().getDrawable(R.drawable.round_result_success));
	    }
	    else { 
	    	Log.i(TAGNAME,"Error:"+Integer.toString(error));
	    	if ( error == 0)
	    	Toast.makeText(this.context,this.context.getResources().getString(
	    			R.string.error_accessdenied),Toast.LENGTH_LONG).show();
	    	if ( error == 1)
	    		Toast.makeText(this.context,this.context.getResources().getString(
		    			R.string.error_econnrefused),Toast.LENGTH_LONG).show();
	    	if ( error == 2)
	    		Toast.makeText(this.context,this.context.getResources().getString(
		    			R.string.error_failconn),Toast.LENGTH_LONG).show();
	    }
	}
}
