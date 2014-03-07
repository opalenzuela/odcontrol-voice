package cl.mamd.datastore;

import java.util.ArrayList;
import java.util.List;

import cl.mamd.entity.NodoDevice;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataStoreManager {
	/**
	 * allColumnsX = X Name of Entity/Table
	 */
	private String[] allColumnsDevice = {"ID","NAME","LOCATION","IPADDRESS","USERNAME","PASSWD"};
	private SQLiteDatabase database;
	private DataStoreOpenHelper dbHelper;
	
	public DataStoreManager(Context context) {
	    dbHelper = new DataStoreOpenHelper(context);
	}
	public void openDataBase() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	public void closeDataBase() throws SQLException {
		this.dbHelper.close();
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
