package cl.mamd.voice;

import java.util.Calendar;
import java.util.List;

import cl.mamd.datastore.DataStoreManager;
import cl.mamd.entity.NodoDevice;


import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Activity for Display list of device
 */
public class VoiceMainActivity extends Activity implements OnItemClickListener,OnItemLongClickListener {
	
	
	/*
	 * Widgets of current activity
	 */
	private ListView listView;
	private EditText ipaddress_for_add;
	
	/*
	 * Id for intent's recognition
	 */
	private final int NEWDEVICE_REQUEST = 1;
	private final int UPDATEDEVICE_REQUEST = 2;
	private final int NODOVOICE_REQUEST = 3;
	private final int NODODEVICEPORT_REQUEST = 4;
	
	private String TAGNAME = "VoiceMainActivity";
	private DataStoreManager dsm;
	private NodoDeviceAdapter adapter;
	private List<NodoDevice> values;
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * Disabled screen orientation changes and remove title
         */
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_voice_main);
                
        
        this.listView = (ListView)findViewById(R.id.listView);
        this.ipaddress_for_add = (EditText)findViewById(R.id.edittext_ipaddress);

        /*
         * Checking wifi adapter availability
         */
        checkWifiAvailability();
        
        
        /*
         * Getting device information from SQLite Database
         */
        dsm = new DataStoreManager(this);
        dsm.openDataBase();
        this.values = dsm.getAllNodoDevice();
        
        /*
         * oading all devices in listview
         */
        this.adapter = new NodoDeviceAdapter(this,values);
        this.listView.setAdapter(adapter);
        this.listView.setOnItemClickListener(this);
        this.listView.setOnItemLongClickListener(this);
        
        dsm.closeDataBase();
    }
    
    /**
     * Add Line to LinearLayout like a LOG screen.
     * @param textlog
     * @return
     */
    public boolean putLogInScreen(String textlog){
    	String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    	Log.i(TAGNAME,mydate + ":" +textlog);   
    	return true;
    }

    /**
     * @author mmoscoso
     * This method check if the wifi adapter is available
     * @param
     * @return
     * 
     */
    public void checkWifiAvailability(){
    	
    	/*
    	 * Getting ConnectivityManager
    	 */
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        
        
        if (mWifi.isConnected()) {
            
        	this.putLogInScreen(getResources().getString(R.string.wifi_connection_state)+ "OK");
        	WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        	WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        	DhcpInfo dhcpInfo = wifiMgr.getDhcpInfo();
                	
        	/*
        	 * Getting information of current inet connection 
        	 */
        	String netMask = intToIp(dhcpInfo.netmask);
        	String SSID = wifiInfo.getSSID();
        	String hostaddr = intToIp(wifiInfo.getIpAddress());
        	String MAC = wifiInfo.getMacAddress();
            
        	String message = "Conectado a ("+SSID+") IP("+hostaddr+") NETMASK("+netMask+") MAC("+MAC+")"; 
        	this.putLogInScreen(message);
        }
        else {
        	this.putLogInScreen(getResources().getString(R.string.wifi_connection_state)+ "OFF");
        	//Check if 
        	startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }
    
   /**
    * @author mmoscoso
    * Function for search devices for IP.
    * @param view
    * @return
    */
    public boolean searchDeviceButton(View view){
    	
    	String ipforsearch = this.ipaddress_for_add.getText().toString();
    	
    	if (ipforsearch.equals("")){
    		dsm.openDataBase();
    		values = dsm.getAllNodoDevice();
    		dsm.closeDataBase();
    	}
    	else {
    		int i;
    		for ( i=0 ; i < this.values.size() ; i++){
    			Log.i(TAGNAME,this.values.get(i).getIpaddress()+").contains.("+ipforsearch );
    			if ( !this.values.get(i).getIpaddress().contains(ipforsearch) ){
    				Log.i(TAGNAME, "No match with ip, and remove");    			
    				this.values.remove(i);
    			}
    		}
    	}
    	adapter = new NodoDeviceAdapter(this,values);
    	this.listView.setAdapter(adapter);
    	this.ipaddress_for_add.setText("");
    	return true;
    }
    
    /**
     * @author mmoscoso
     * Method for add device to database 
     * @param view
     * @return
     */
    public boolean addDeviceButton(View view){
    	
    	String[] ipaddress = ipaddress_for_add.getText().toString().split("\\.");
    	
    	if(ipaddress_for_add.getText().toString().length() > 15){
    		this.ipaddress_for_add.setError(
    				getResources().getString(R.string.error_ipaddresstolong)
    				);
    		return false;
    	}
    	else {
    		if ( ipaddress.length == 4 ){

    			Log.i(TAGNAME,"NUMBER_1:"+ipaddress[0]);
    			Log.i(TAGNAME,"NUMBER_2:"+ipaddress[1]);
    			Log.i(TAGNAME,"NUMBER_3:"+ipaddress[2]);
    			Log.i(TAGNAME,"NUMBER_4:"+ipaddress[3]);
    			//Check if IP Address Exists
    			//this.dsm = new DataStoreManager(this);
    			this.dsm.openDataBase();
    			boolean exists = this.dsm.checkIfIpExists(ipaddress_for_add.getText().toString());
    			this.dsm.closeDataBase();
    			if (!exists){
    				Log.i(TAGNAME,"Adding new Device");
    				//Creating dialog for adding new device
    				Intent newdevice = new Intent(VoiceMainActivity.this,NodoDeviceActivity.class);
    				newdevice.putExtra("IPADDRESS",ipaddress_for_add.getText().toString());
    				startActivityForResult(newdevice,NEWDEVICE_REQUEST);
    			}
    			else {
    				Log.e(TAGNAME,"PROBLE:ip address already exists");
    				//Creating dialog for show error
    				AlertDialog.Builder builder = new AlertDialog.Builder(this);

    				builder.setMessage(R.string.error_ipaddress_already_exists)
    				       .setTitle(R.string.title_dialog_problem)
    				       .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener() {
    				    	   public void onClick(DialogInterface dialog,int id) {
    				    		   ipaddress_for_add.setText("");
    				    	   }
    				       });
    				AlertDialog dialog = builder.create();
    				dialog.show();
    			}
    		}
    		else {
    			this.ipaddress_for_add.setError(
        				getResources().getString(R.string.error_ipaddressinvalid)
        				);
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * @author mmoscoso
     * Function for Translate Integer to String for IpAddress 
     * @param i
     * @return
     */
    public String intToIp(int i) {
    	   return ( i & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 24 ) & 0xFF );
    }
     
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         * Check which request we're responding to
         */
        if (requestCode == NEWDEVICE_REQUEST) {
        	Log.i(TAGNAME,"REFRESHING UI AFTER ADD NEW DEVICE");
        	if (resultCode == RESULT_OK){
        		data.getExtras().getString("IPADDRESS");
        		Log.i(TAGNAME,data.getExtras().getString("IPADDRESS"));
        		Log.i(TAGNAME,data.getExtras().getString("NAME"));
        		Log.i(TAGNAME,data.getExtras().getString("LOCATION"));
        		Log.i(TAGNAME,data.getExtras().getString("USERNAME"));
        		Log.i(TAGNAME,data.getExtras().getString("PASSWD"));
        		
        		if (data.getExtras().getString("USERNAME").equals("")
        				|| data.getExtras().getString("PASSWD").equals("")
        				|| data.getExtras().getString("NAME").equals("")
        				) {
        			Toast.makeText(this,
        					getResources().getString(R.string.error_missingdatafordevice)
        					,Toast.LENGTH_LONG).show();
        				
        		}
        		else {
        		
        			//INSERT INTO DATABASE
        			this.dsm.openDataBase();
        			ContentValues values = new ContentValues();
        			values.put("IPADDRESS",data.getExtras().getString("IPADDRESS"));
        			values.put("NAME",data.getExtras().getString("NAME"));
        			values.put("LOCATION",data.getExtras().getString("LOCATION"));
        			values.put("USERNAME",data.getExtras().getString("USERNAME"));
        			values.put("PASSWD",data.getExtras().getString("PASSWD"));
        			
        			Log.i(TAGNAME, "Values to String"+values.toString());
        			boolean result = dsm.createNewDevice(values);
        			Log.i(TAGNAME, "RESULT OF CREATE OPERATION"+String.valueOf(result));
        			
        			//Refresh Adapter
        			adapter = new NodoDeviceAdapter(this,dsm.getAllNodoDevice());
        			this.listView.setAdapter(adapter);
        			this.ipaddress_for_add.setText("");
        			this.dsm.closeDataBase();
        			
        			if ( result ){
        				Toast.makeText(this,getResources().getString(
        						R.string.toast_newdevicesuccess),Toast.LENGTH_LONG).show();
        			}
        		}
        		
        	}
        }
        if ( requestCode == UPDATEDEVICE_REQUEST) {
        	Log.i(TAGNAME,"REFRESHING UI AFTER ADD NEW DEVICE");
        	if (resultCode == RESULT_OK){
        		Log.i(TAGNAME, "Updating data for device");
        		//INSERT INTO DATABASE
        		this.dsm.openDataBase();
        		ContentValues values = new ContentValues();
        		values.put("IPADDRESS",data.getExtras().getString("IPADDRESS"));
        		values.put("NAME",data.getExtras().getString("NAME"));
        		values.put("LOCATION",data.getExtras().getString("LOCATION"));
        		values.put("USERNAME",data.getExtras().getString("USERNAME"));
        		values.put("PASSWD",data.getExtras().getString("PASSWD"));
        			
        		boolean result = dsm.updateDevice(values,data.getExtras().getString("IPADDRESS"));
        		Log.i(TAGNAME, "RESULT OF UPDATE OPERATION"+String.valueOf(result));
        		adapter = new NodoDeviceAdapter(this,dsm.getAllNodoDevice());
    	        this.listView.setAdapter(adapter);
    	        this.ipaddress_for_add.setText("");
    	        this.dsm.closeDataBase();
    	        
    	        if ( result ){
    				Toast.makeText(this,getResources().getString(
    						R.string.toast_updatedevicesuccess),Toast.LENGTH_LONG).show();
    			}
        	}
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.voice_main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Log.i(TAGNAME, "ID MENU ITEM:"+item.getItemId());
    	switch (item.getItemId()) {
    		case R.id.action_settings:
    			//Start Preference Activity
        		return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
   

    /**
     * Item Click - Select device
     */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		// TODO Auto-generated method stub
		/*
		 * Access to Device
		 */
		Intent nodovoicerecognition = new Intent(VoiceMainActivity.this,NodoVoiceRecognitionActivity.class);
		TextView ipvalue = (TextView)view.findViewById(R.id.textViewIpAddress);
		dsm.openDataBase();
		final NodoDevice nodo = dsm.getDevice(ipvalue.getText().toString());
		
		/*
		 * Check port configuration
		 */
		dsm.openDataBase();
		if ( dsm.checkPortOfDevice(nodo.getId()) ){
			nodovoicerecognition.putExtra("ID",nodo.getId());
			nodovoicerecognition.putExtra("IPADDRESS",nodo.getIpaddress());
			nodovoicerecognition.putExtra("NAME",nodo.getName());
			nodovoicerecognition.putExtra("LOCATION",nodo.getLocation());
			nodovoicerecognition.putExtra("USERNAME",nodo.getUsername());
			nodovoicerecognition.putExtra("PASSWD",nodo.getPasswd());
			
		
			startActivityForResult(nodovoicerecognition,NODOVOICE_REQUEST);
			dsm.closeDataBase();
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.error_device_whitout_ports)
			       .setTitle(R.string.title_dialog_problem)
			       .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog,int id) {
			    		   Intent nododeviceport = new Intent(VoiceMainActivity.this,NodoDevicePortMainActivity.class);
			    		   nododeviceport.putExtra("DEVICE_ID",nodo.getId());
			    		   nododeviceport.putExtra("DEVICE_NAME",nodo.getName());
			    		   startActivityForResult(nododeviceport,NODODEVICEPORT_REQUEST);
			    		   
			    	   }
			       })
			       .setNegativeButton(R.string.button_cancel,new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int id) {
			    		   
			    		   /*
			    		    * Toast with remainder information
			    		    */
			    		   Toast.makeText(VoiceMainActivity.this,
			    				   getResources().getString(R.string.message_reminder),Toast.LENGTH_LONG).show();
			    	   }
			       });

			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
	
	/**
	 * Display options of Device
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		//Display options of device remove,update.
		final View v = view;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.title_deviceoptions)
	           .setItems(R.array.device_options, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               /*
	                *  The 'which' argument contains the index position
	                *  of the selected item
	                *  0 Delete,1 Update, 2 Ports  
	                */
	            	   final int delete = 0, update = 1, ports = 2;
	            	   TextView ipvalue = (TextView)v.findViewById(R.id.textViewIpAddress);
	            	   switch(which){
	            	   		case delete:
	            	   			//Deleting device
	            	   			dsm.openDataBase();
	            	   			boolean result = dsm.deleteDevice(ipvalue.getText().toString());
	            	   			
	            	   			adapter = new NodoDeviceAdapter(VoiceMainActivity.this,dsm.getAllNodoDevice());
	                	        listView.setAdapter(adapter);
	                	        ipaddress_for_add.setText("");
	                	        dsm.closeDataBase();
	                	        
	                	        if ( result ){
	                				Toast.makeText(VoiceMainActivity.this,getResources().getString(
	                						R.string.toast_deletedevicesuccess),Toast.LENGTH_LONG).show();
	                			}
	                	        
	            	   			break;
	            	   		case update:
	            	   			Intent updatedevice = new Intent(VoiceMainActivity.this,NodoDeviceActivity.class);
	            	   			//Getting all data from device
	            	   			dsm.openDataBase();
	            	   			NodoDevice nodo = dsm.getDevice(ipvalue.getText().toString());
	            	   			
	            	   			Log.i(TAGNAME, "USER/PASS"+nodo.getUsername()+"/"+nodo.getPasswd());
	            	   			updatedevice.putExtra("IPADDRESS",nodo.getIpaddress());
	            	   			updatedevice.putExtra("NAME",nodo.getName());
	            	   			updatedevice.putExtra("LOCATION",nodo.getLocation());
	            	   			updatedevice.putExtra("USERNAME",nodo.getUsername());
	            	   			updatedevice.putExtra("PASSWD",nodo.getPasswd());
	            	   			updatedevice.putExtra("OPTION",UPDATEDEVICE_REQUEST);
	            	   			
	            				startActivityForResult(updatedevice,UPDATEDEVICE_REQUEST);
	            				dsm.closeDataBase();
	            	   			break;
	            	   		case ports:
	            	   			dsm.openDataBase();
	            	   			NodoDevice nodoport = dsm.getDevice(ipvalue.getText().toString());
	            	   			Intent nododeviceport = new Intent(VoiceMainActivity.this,NodoDevicePortMainActivity.class);
	            	   			nododeviceport.putExtra("DEVICE_ID",nodoport.getId());
	            	   			nododeviceport.putExtra("DEVICE_NAME",nodoport.getName());
	            	   			startActivityForResult(nododeviceport,NODODEVICEPORT_REQUEST);
	            	   			
	            	   			dsm.closeDataBase();
	            	   			
	            	   			break;
	            	   }
	           }
	    });
	    builder.create();
	    builder.show();
		
		return true;
	}
	
	
	@Override
	protected void onResume() {
	    super.onResume();
	    Log.i(TAGNAME, ":On Resumen");
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    Log.i(TAGNAME, ":On Pause");
	}
	@Override
	protected void onDestroy() {
	    super.onPause();
	    this.dsm.closeDataBase();
	}
	
}
