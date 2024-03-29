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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class HTTPCheckAuthentication extends AsyncTask<String, Void, String> {
	
	private ProgressDialog mProgressDialog;
	private Context context;	
	private final String TAGNAME = "HTTPCheckAuthentication";
	
	private NodoDevice nodo;
	
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
	public HTTPCheckAuthentication(Context context,NodoDevice nodoforcheck){
		this.context = context;
		this.nodo = nodoforcheck;
		
		this.mProgressDialog = new ProgressDialog(context);
		this.mProgressDialog.setMessage("Verificando Datos para Autenticacion..");
		this.mProgressDialog.setIndeterminate(false);
		this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.mProgressDialog.setCancelable(true);
	      
		this.results = false;
		 
		String authorization = this.nodo.getUsername() + ":" + this.nodo.getPasswd();
		 this.author_String = Base64.encodeToString(authorization.getBytes(),0);
	    
		 
	}

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    mProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		//Execute HTTP Basic Authentication Test
		try {
			int port = 80;
			InetAddress addr = InetAddress.getByName(nodo.getIpaddress());
			
			Log.i(TAGNAME, "addr:"+addr);
			
			//Socket socket = new Socket(addr, port);
			Socket socket = new Socket();
			socket.setSoTimeout(10000);
			socket.connect(new InetSocketAddress(addr, port), 10000);
			
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            wr.write("GET / HTTP/1.0rn");
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
			if ( e.getMessage().toString().contains("EHOSTUNREACH")) {
				error = 2;
			}
			if ( e.getMessage().toString().contains("ETIMEDOUT")) {
				error = 3;
			}	
			if ( e.getMessage().toString().contains("ENETUNREACH")) {
				error = 4;
			}
			this.results = false;
		}
		
		
		return null;
	}

	@Override
	protected void onPostExecute(String unused) {
	    mProgressDialog.dismiss();
	    
	    if ( this.results ) {
	    	Toast.makeText(this.context,this.context.getResources().getString(
	    			R.string.toast_accessgranted),Toast.LENGTH_LONG).show();
	    }
	    else { 
	    	if ( error == 0)
	    	Toast.makeText(this.context,this.context.getResources().getString(
	    			R.string.error_accessdenied),Toast.LENGTH_LONG).show();
	    	if ( error == 1)
	    		Toast.makeText(this.context,this.context.getResources().getString(
		    			R.string.error_econnrefused),Toast.LENGTH_LONG).show();
	    	if ( error == 2)
	    		Toast.makeText(this.context,this.context.getResources().getString(
		    			R.string.error_ehostunreach),Toast.LENGTH_LONG).show();
	    	if ( error == 3)
	    		Toast.makeText(this.context,this.context.getResources().getString(
		    			R.string.error_etimedout),Toast.LENGTH_LONG).show();
	    	if ( error == 4)
	    		Toast.makeText(this.context,this.context.getResources().getString(
		    			R.string.error_enetunreach),Toast.LENGTH_LONG).show();
	    	
	    }
	}
}
