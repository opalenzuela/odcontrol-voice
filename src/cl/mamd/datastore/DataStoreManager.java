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
	
	
	public boolean updateNodoDevicePort(NodoDevicePort nodo){
		ContentValues values = new ContentValues();
		
		values.put("ID",nodo.getId());
		values.put("DEVICE", nodo.getDevice());
		values.put("PORT", nodo.getPort());
		values.put("TAG", nodo.getTag());
		values.put("ACTION", nodo.getAction());
		
		long result = this.database.update("nododeviceport",values,"DEVICE="+Integer.toString(nodo.getDevice())+
				" AND ID = "+Integer.toString(nodo.getId()),null);
		if ( result == -1 ){
			return false;
		}
		else {
			return true;
		}
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
		
		
		Cursor cursor = database.rawQuery("select  count(*) from nododeviceport where device="+iddevice,null);
		cursor.moveToFirst();
		
		Integer count = cursor.getInt(0);
		cursor.close();
		
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
		
		Cursor cursor = database.rawQuery("select  count(*) from nododevice where ipaddress='"+ipaddress+"'",null);
		cursor.moveToFirst();
		
		Integer count = cursor.getInt(0);
		cursor.close();
		
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
		
		Log.i(TAGNAME,"IPADDRESS,NAME,LOCATION,USERNAME,PASSWD");
		Log.i(TAGNAME, "VALUES OF DATAMANAGER"+values.toString());
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
	public boolean deletePortOfDevice(Integer device,String port){
		
		
		if (this.database.delete("nododeviceport","DEVICE = "+Integer.toString(device)
				+ " AND PORT='"+port+"'",null) == 0){
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
		
		Cursor cursor = database.rawQuery("SELECT ID,NAME,LOCATION,IPADDRESS,USERNAME,PASSWD FROM nododevice WHERE IPADDRESS = '"+ipaddress+"'", null);
		cursor.moveToFirst();
		NodoDevice nodo = new NodoDevice();
		nodo = this.cursorToNodo(cursor);
		nodo.setPorts(this.getPortOfDevice(nodo.getId()));
		cursor.close();
		return nodo;
	}
	
	
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<NodoDevicePort> getPortOfDevice(Integer id){
		List<NodoDevicePort> ports = new ArrayList<NodoDevicePort>();
		
		Cursor cursor = database.rawQuery("SELECT ID,DEVICE,PORT,TAG,ACTION FROM nododeviceport WHERE DEVICE = "+Integer.toString(id), null);
		
		cursor.moveToFirst();
		if ( cursor.getCount() != 0 ) {
			Log.i(TAGNAME, "Adding Port to List");
			while ( !cursor.isAfterLast()) {
				ports.add(this.cursorToPort(cursor));
				cursor.moveToNext();
			}
		}
		cursor.close();
		return ports;
	}
	/**
	 * 
	 * @return
	 */
	public List<NodoDevicePort> getPortOfAllDevice(){
		List<NodoDevicePort> ports = new ArrayList<NodoDevicePort>();
		

		Cursor cursor = database.query(DataStoreOpenHelper.getTableNameDeviceport(),
		        this.allColumnsDevicePort,
		        null, null, null, null, null);

		cursor.moveToFirst();
		if ( cursor.getCount() != 0 ) {
		
			while ( !cursor.isAfterLast()) {
				ports.add(this.cursorToPort(cursor));
				cursor.moveToNext();
			}
		}
		cursor.close();
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

		//ID,NAME,LOCATION,IPADDRESS,USERNAME,PASSWD
		nodo.setId(cursor.getInt(0));
		nodo.setName(cursor.getString(1));		
		nodo.setLocation(cursor.getString(2));
		nodo.setIpaddress(cursor.getString(3));
		nodo.setUsername(cursor.getString(4));
		nodo.setPasswd(cursor.getString(5));
		
		return nodo;
	}
	
}
