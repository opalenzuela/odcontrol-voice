package cl.mamd.datastore;

import java.util.ArrayList;
import java.util.List;

import cl.mamd.entity.NodoDevice;
import cl.mamd.entity.NodoDevicePort;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * 
 * @author mmoscoso	
 * @version 0.1
 */
public class DataStoreManager {
	/**
	 * allColumnsX = X Name of Entity/Table
	 */
	private String[] allColumnsDevice = {"ID","NAME","LOCATION","IPADDRESS","USERNAME","PASSWD"};
	private String[] allColumnsDevicePort = {"ID","DEVICE","PORT","TAG","ACTION"};
	private SQLiteDatabase database;
	private DataStoreOpenHelper dbHelper;
	private static String TAGNAME = "DataStoreManager";
	
	/**
	 * 
	 * @param context
	 */
	public DataStoreManager(Context context) {
	    dbHelper = new DataStoreOpenHelper(context);
	    Log.i(TAGNAME,"Creating DataStoreOpenHelper instance");
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	public void closeDataBase() throws SQLException {
		this.dbHelper.close();
	}
	
	/**
	 * 
	 */
	public void refreshDataBase(int old,int newv){
		this.dbHelper.onUpgrade(database, 2, 2);
	}
	
	
	public boolean creatNodoDevicePort(NodoDevicePort port){
		ContentValues values = new ContentValues();
		
		values.put("DEVICE", port.getDevice());
		values.put("PORT", port.getPort());
		values.put("TAG", port.getTag());
		values.put("ACTION", port.getAction());
		
		Log.i(TAGNAME, "VALUES:"+Integer.toString(port.getDevice())+
				"/"+port.getPort()+
				"/"+port.getTag()+
				"/"+port.getAction());
				
		
		
		long result = this.database.insert("nododeviceport","DEVICE,PORT,TAG,ACTION",values);
		if ( result == -1 ){
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Function for checking if device has port configurations
	 * @param iddevice
	 * @return
	 */
	public boolean checkPortOfDevice(Integer iddevice){
		Log.i(TAGNAME, "Checking the existance of configuration for Port");
		Cursor cursor = database.rawQuery("select  count(*) from nododeviceport where device="+iddevice,null);
		cursor.moveToFirst();
		Log.i(TAGNAME,"Ports COUNT for Device("+iddevice+")="+Integer.toString(cursor.getInt(0)));
		Integer count = cursor.getInt(0);
		if ( count != 0 ){
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Function for check if exists a device with especific ip address
	 * @param ipaddress
	 * @return
	 */
	public boolean checkIfIpExists(String ipaddress){
		Log.i(TAGNAME,"Checking if exists device with this ip address:"+ipaddress);
		Cursor cursor = database.rawQuery("select  count(*) from nododevice where ipaddress='"+ipaddress+"'",null);
		cursor.moveToFirst();
		Log.i(TAGNAME,"COUNT="+Integer.toString(cursor.getInt(0)));
		Integer count = cursor.getInt(0);
		
		if ( count != 0 ){
			return true;
		}
		else {
			return false;
		}

	}
	/**
	 * Create a new device in Data base
	 * @param values
	 * @return
	 */
	public boolean createNewDevice(ContentValues values){
		long result = this.database.insert("nododevice","IPADDRESS,NAME,LOCATION,USERNAME,PASSWD",values);
		if ( result == -1 ){
			return false;
		}
		else {
			return true;
		}
		
	}
	/**
	 * 
	 * @param values
	 * @param ipaddress
	 * @return
	 */
	public boolean updateDevice(ContentValues values,String ipaddress){
		long result = this.database.update("nododevice",values,"IPADDRESS='"+ipaddress+"'",null);
		if ( result == -1 ){
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * 
	 * @param ipaddress
	 * @return
	 */
	public boolean deleteDevice(String ipaddress){
		
		
		
		if (this.database.delete("nododevice","IPADDRESS = '"+ipaddress+"'",null) == 0){
			return false;
		}
		else {
			return true;
		}
	}
	/**
	 * 
	 * @param ipaddress
	 * @return
	 */
	public NodoDevice getDevice(String ipaddress){
		Log.i(TAGNAME,"Getting Device from DB for ip address:"+ipaddress);
		Cursor cursor = database.rawQuery("SELECT * FROM nododevice WHERE IPADDRESS = '"+ipaddress+"'", null);
		cursor.moveToFirst();
		NodoDevice nodo = new NodoDevice();
		nodo = this.cursorToNodo(cursor);
		nodo.setPorts(this.getPortOfDevice(nodo.getId()));
		return nodo;
	}
	
	public List<NodoDevicePort> getPortOfDevice(Integer id){
		List<NodoDevicePort> ports = new ArrayList<NodoDevicePort>();
		
		Log.i(TAGNAME, "TABLE NAME:"+DataStoreOpenHelper.getTableNameDeviceport());
		Log.i(TAGNAME, "ID of Device for get PORTS:"+Integer.toString(id));
		Cursor cursor = database.rawQuery("SELECT ID,DEVICE,PORT,TAG,ACTION FROM nododeviceport WHERE DEVICE = "+Integer.toString(id), null);
		
		/*Cursor cursor = database.query(DataStoreOpenHelper.getTableNameDeviceport(),
		        this.allColumnsDevicePort,
		        "DEVICE = "+Integer.toString(id), null, null, null, null);
		        */
		Log.i(TAGNAME, "CURSOR OK");
		Log.i(TAGNAME, "count for CURSOR:"+Integer.toString(cursor.getCount()));
		cursor.moveToFirst();
		if ( cursor.getCount() != 0 ) {
			Log.i(TAGNAME, "Adding Port to List");
			while ( !cursor.isAfterLast()) {
				ports.add(this.cursorToPort(cursor));
				cursor.moveToNext();
			}
		}
		return ports;
	}
	
	public List<NodoDevicePort> getPortOfAllDevice(){
		List<NodoDevicePort> ports = new ArrayList<NodoDevicePort>();
		

		Cursor cursor = database.query(DataStoreOpenHelper.getTableNameDeviceport(),
		        this.allColumnsDevicePort,
		        null, null, null, null, null);

		cursor.moveToFirst();
		if ( cursor.getCount() != 0 ) {
			Log.i(TAGNAME, "Adding all Port to List");
			while ( !cursor.isAfterLast()) {
				ports.add(this.cursorToPort(cursor));
				cursor.moveToNext();
			}
		}
		return ports;
	}
	
	
	/**
	 * Returns all device from DataBase
	 * @return
	 */
	public List<NodoDevice> getAllNodoDevice() {
		List<NodoDevice> nodos = new ArrayList<NodoDevice>();

	    Cursor cursor = database.query("nododevice",
	        this.allColumnsDevice, null, null, null, null, null);

	    cursor.moveToFirst();
	    	    
	    while (!cursor.isAfterLast()) {
	    	NodoDevice device = this.cursorToNodo(cursor);
	    	nodos.add(device);
	    	cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return nodos;
	}
	
	private NodoDevicePort cursorToPort(Cursor cursor){
		NodoDevicePort port = new NodoDevicePort();
		
		port.setId(cursor.getInt(0));
		port.setDevice(cursor.getInt(1));
		port.setPort(cursor.getString(2));
		port.setTag(cursor.getString(3));
		port.setAction(cursor.getString(4));
		
		return port;
	}
	
	/**
	 * Transform cursor to Device
	 * @param cursor
	 * @return
	 */
	private NodoDevice cursorToNodo(Cursor cursor){
		NodoDevice nodo = new NodoDevice();

		nodo.setId(cursor.getInt(0));
		nodo.setName(cursor.getString(1));		
		nodo.setLocation(cursor.getString(2));
		nodo.setIpaddress(cursor.getString(3));
		nodo.setUsername(cursor.getString(4));
		nodo.setPasswd(cursor.getString(5));
		
		return nodo;
	}
	
}
