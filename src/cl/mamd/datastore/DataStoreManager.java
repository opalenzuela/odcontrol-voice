package cl.mamd.datastore;

import java.util.ArrayList;
import java.util.List;

import cl.mamd.entity.NodoDevice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataStoreManager {
	/**
	 * allColumnsX = X Name of Entity/Table
	 */
	private String[] allColumnsDevice = {"ID","NAME","LOCATION","IPADDRESS","USERNAME","PASSWD"};
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
	public void refreshDataBase(){
		this.dbHelper.onUpgrade(database, 2, 2);
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
		return this.cursorToNodo(cursor);
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
