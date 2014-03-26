package cl.mamd.voice;

import java.util.List;

import cl.mamd.datastore.DataStoreManager;
import cl.mamd.entity.NodoDevicePort;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class NodoDevicePortMainActivity extends Activity {
	
	private final int NEWDEVICEPORT_REQUEST = 1;
	private int device_id = 0;
	private String device_name;
	private EditText edittext_searchtag;
	private final String TAGNAME = "NodoDevicePortMainActivity";
	private DataStoreManager dsm;
	private NodoDevicePortAdapter adapter;
	private ListView listView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nodo_device_port);
		
		Bundle extras = getIntent().getExtras();
		this.device_id = extras.getInt("DEVICE_ID");
		this.device_name = extras.getString("DEVICE_NAME");
		
		//Comment
		Log.i(TAGNAME,"Managment port for device ("+Integer.toString(this.device_id)+"/"+this.device_name+")");
		
		
		this.dsm = new DataStoreManager(this);
		this.edittext_searchtag = (EditText)findViewById(R.id.edittext_tagsearch);
		this.listView = (ListView)findViewById(R.id.listViewDevicePorts);
		
		this.dsm = new DataStoreManager(this);
		this.dsm.openDataBase();
		this.adapter = new NodoDevicePortAdapter(this,dsm.getPortOfDevice(this.device_id));
		this.listView.setAdapter(adapter);		
		
		int i = 0;
		Log.i(TAGNAME,"GET LIST OF ALL PORTS");
		//List<NodoDevicePort> list = this.dsm.getPortOfAllDevice();
		List<NodoDevicePort> list = this.dsm.getPortOfDevice(2); 
		while ( i < list.size() ){
			Log.i(TAGNAME,"ID:"+Integer.toString(list.get(i).getId())+" DEVICE:"+Integer.toString(list.get(i).getDevice()));
			i++;
		}
		
		
		
		
		TextView textview = (TextView)findViewById(R.id.textView_devicenameinfo);
		textview.setText(createTitleForDeviceInformation());
		
	}
	private String createTitleForDeviceInformation(){
		return "Equipo("+Integer.toString(this.device_id)+"): "+this.device_name;
	}
	
	public void addDevicePortButton(View view){
		
		String tag = this.edittext_searchtag.getText().toString();
		//Check length of 
		if ( tag.length() < 3 ){
			tag = "";
		}
		
		Intent newport = new Intent(this,NodoDevicePortActivity.class);
		newport.putExtra("PORT","");
		newport.putExtra("TAG",tag);
		newport.putExtra("DEVICE",this.device_name);
		newport.putExtra("DEVICE_ID",this.device_id);
		startActivityForResult(newport,NEWDEVICEPORT_REQUEST);
	}
	public void searchDevicePortButton(View view){
		
	}

	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // Check which request we're responding to
	        if (requestCode == this.NEWDEVICEPORT_REQUEST) {
	        	if (resultCode == RESULT_OK){
	        		String[] values = data.getExtras().getStringArray("ACTIONS");
	        		int i;
	        		String actions = "";
	        		for ( i = 0 ; i < values.length ; i++ ){
	        			actions = actions+values[i]+",";
	        			Log.i(TAGNAME,values[i]);
	        		}

	        		//Comment
	        		Log.i(TAGNAME,Integer.toString(data.getExtras().getInt("DEVICE_ID"))+"/"+Integer.toString(this.device_id));
	        		
	        		
	        		NodoDevicePort nodoport = new NodoDevicePort();
	        		nodoport.setId(0);
	        		nodoport.setDevice(data.getExtras().getInt("DEVICE_ID"));
	        		nodoport.setTag(data.getExtras().getString("TAG"));
	        		nodoport.setPort(data.getExtras().getString("PORT"));
	        		nodoport.setAction(actions);
	        		
	        		this.dsm.openDataBase();
	        		
	        		if(this.dsm.creatNodoDevicePort(nodoport))
	        			Log.i(TAGNAME,"Created NodoDevicePort");
	        		else
	        			Log.i(TAGNAME,"Failed NodoDevicePort");
	        		
	        		this.adapter = new NodoDevicePortAdapter(this,dsm.getPortOfDevice(this.device_id));
	        		
	     	        this.listView.setAdapter(adapter);
	     	        this.edittext_searchtag.setText("");
	     	        this.dsm.closeDataBase();
	        		
	        	}
	        }
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nodo_device_port, menu);
		return true;
	}
	
}

