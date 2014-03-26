package cl.mamd.datastore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 
 * @author mmoscoso	
 * @version 0.1
 */
public class DataStoreOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "voicedemo";
    private static final String TABLE_NAME_NODODEVICE = "nododevice";
    private static final String TABLE_NAME_DEVICEPORT = "nododeviceport";
    private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME_NODODEVICE + " (" +
                "ID" + " integer primary key autoincrement, " +
                "NAME"  + " TEXT, " +
                "LOCATION" + " TEXT, " +
                "IPADDRESS" + " TEXT, " +
                "PASSWD" + " TEXT, " +
                "USERNAME" + " TEXT);";
    private static final String TABLE_PORT_CREATE = 
    		" CREATE TABLE " + TABLE_NAME_DEVICEPORT + "(" +
    		" ID " + "integer primary key autoincrement, " +
    		" DEVICE " + " integer, " +
    		" PORT " + "TEXT, " +
    		" TAG " + "TEXT, " +
    		" ACTION " + "TEXT, " +
    		" FOREIGN KEY (DEVICE) REFERENCES " + TABLE_NAME_NODODEVICE + "(ID))"; 

    public DataStoreOpenHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_PORT_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		// TODO Auto-generated method stub
		Log.w(DataStoreOpenHelper.class.getName(),
		        "Upgrading database from version " + oldV + " to "
		            + newV + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NODODEVICE);
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DEVICEPORT);
		    onCreate(db);
	}

	public static String getTableNameNododevice() {
		return TABLE_NAME_NODODEVICE;
	}

	public static String getTableNameDeviceport() {
		return TABLE_NAME_DEVICEPORT;
	}
	
	
	
}
