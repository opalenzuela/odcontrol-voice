package cl.mamd.voice;

import java.util.Calendar;
import java.util.List;

import cl.mamd.datastore.DataStoreManager;
import cl.mamd.datastore.DataStoreOpenHelper;
import cl.mamd.entity.NodoDevice;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceMainActivity extends Activity {
	
	private ListView listView;
	private String TAGNAME = "VoiceMainActivity";
	private NodoDeviceAdapter adapter;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_voice_main);
       
        this.listView = (ListView)findViewById(R.id.listView);
        //Checking system
        checkWifiAvailability();
       //connectToDataBase();
        
        DataStoreManager dsm = new DataStoreManager(this);
        dsm.openDataBase();
        List<NodoDevice> values = dsm.getAllNodoDevice();
        
        
        //ArrayAdapter<NodoDevice> adapter = new ArrayAdapter<NodoDevice>(this,android.R.layout.simple_list_item_1,values);
        
        adapter = new NodoDeviceAdapter(this,values);
        		
        this.listView.setAdapter(adapter);
        
        dsm.closeDataBase();
    }
    /**
     * Add Line to LinearLayout like a LOG screen.
     * @param textlog
     * @return
     */
    private boolean putLogInScreen(String textlog){
    	String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    	Log.i(TAGNAME,mydate + ":" +textlog);   
    	return true;
    }

    private void checkWifiAvailability(){
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        
        if (mWifi.isConnected()) {
            
        	this.putLogInScreen(getResources().getString(R.string.wifi_connection_state)+ "OK");
        	WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        	WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        	DhcpInfo dhcpInfo = wifiMgr.getDhcpInfo();
        	
        	
        	//Get Connection Info 
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
     * Function for Translate Integer to String for IpAddress 
     * @param i
     * @return
     */
    private String intToIp(int i) {
    	   return ( i & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 24 ) & 0xFF );
    }
    
    /**
     * Function to connect (or create) a DataBase for OPControls information
     * @return
     */
    private boolean connectToDataBase(){
    	DataStoreOpenHelper dataHelper = new DataStoreOpenHelper(this);
    	
    	//dataHelper.onUpgrade(dataHelper.getWritableDatabase(), 1, 2);
    	
    	this.putLogInScreen("Nombre base de datos :"+dataHelper.getDatabaseName());
    	
    	
    	ContentValues values = new ContentValues();
    	
    	
    	values.put("USERNAME","admin");
    	values.put("PASSWD","opendomopass");
    	values.put("LOCATION","TALCA - CHILe");
    	values.put("IPADDRESS","192.168.0.31");
    	values.put("NAME","ODCONTROL-1");
    	
    	dataHelper.getWritableDatabase().insert("nododevice","USERNAME,PASSWD,LOCATION,IPADDRESS,NAME",values);
    	
    	dataHelper.close();
    	return true;
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
        case R.id.addDevice:
        	Toast.makeText(this, "Creating new device",Toast.LENGTH_LONG).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}
    	
    	
    }
    
}
