package cl.mamd.voice;

import java.util.ArrayList;
import java.util.List;

import cl.mamd.datastore.DataStoreManager;
import cl.mamd.entity.NodoDevicePort;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 
 * @author mmoscoso
 * @version 0.1
 */
public class NodoDevicePortMainActivity extends Activity implements OnItemLongClickListener {
	
	private final int NEWDEVICEPORT_REQUEST = 1;
	private final int UPDATEDEVICEPORT_REQUEST = 2;
	private final String TAGNAME = "NodoDevicePortMainActivity";
	
	
	private String device_name;
	private EditText edittext_searchtag;
	private ListView listView;
	
	private int device_id = 0;
	private DataStoreManager dsm;
	private NodoDevicePortAdapter adapter;
	private List<NodoDevicePort> values;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Disabled screen orientation changes and remove title
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.activity_nodo_device_port);
		
		Bundle extras = getIntent().getExtras();
		this.device_id = extras.getInt("DEVICE_ID");
		this.device_name = extras.getString("DEVICE_NAME");
		this.values = new ArrayList<NodoDevicePort>();
		
		//Comment
		Log.i(TAGNAME,"Managment port for device ("+Integer.toString(this.device_id)+"/"+this.device_name+")");
		
		
		this.dsm = new DataStoreManager(this);
		this.edittext_searchtag = (EditText)findViewById(R.id.edittext_tagsearch);
		this.listView = (ListView)findViewById(R.id.listViewDevicePorts);
		
		this.dsm = new DataStoreManager(this);
		this.dsm.openDataBase();
		this.values = dsm.getPortOfDevice(this.device_id);
		this.adapter = new NodoDevicePortAdapter(this,this.values);
		
		this.listView.setAdapter(adapter);
		this.listView.setOnItemLongClickListener(this);
		
		TextView textview = (TextView)findViewById(R.id.textView_devicenameinfo);
		textview.setText(createTitleForDeviceInformation());
		
		
	}
	/**
	 * 
	 * @return
	 */
	private String createTitleForDeviceInformation(){
		return "Equipo("+Integer.toString(this.device_id)+"): "+this.device_name;
	}
	
	/**
	 * 
	 * @param view
	 */
	public void addDevicePortButton(View view){
		
		String tag = this.edittext_searchtag.getText().toString();
		//Check length of 
		if ( tag.length() < 3 ){
			tag = "";
		}
		
		Intent newport = new Intent(this,NodoDevicePortActivity.class);
		newport.putExtra("ID",0);
		newport.putExtra("PORT","");
		newport.putExtra("TAG",tag);
		newport.putExtra("DEVICE",this.device_name);
		newport.putExtra("DEVICE_ID",this.device_id);
		startActivityForResult(newport,NEWDEVICEPORT_REQUEST);
	}
	
	/**
	 * 
	 * @param view
	 */
	public void searchDevicePortButton(View view){
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		switch (requestCode){
			case NEWDEVICEPORT_REQUEST:
				if ( resultCode == RESULT_OK ){

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
	        		
					this.values = dsm.getPortOfDevice(this.device_id);
					this.adapter = new NodoDevicePortAdapter(this,this.values);
	        	
					this.listView.setAdapter(this.adapter);
					this.edittext_searchtag.setText("");
					this.dsm.closeDataBase();

				}
				else {
					Toast.makeText(this,
						getResources().getString(R.string.error_portdevicesave)+":CODE:"+
								Integer.toString(resultCode)
								, Toast.LENGTH_LONG).show();
				}
			break;
		case UPDATEDEVICEPORT_REQUEST:
			if ( resultCode == RESULT_OK ){

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
				nodoport.setId(data.getExtras().getInt("ID"));
				nodoport.setDevice(data.getExtras().getInt("DEVICE_ID"));
				nodoport.setTag(data.getExtras().getString("TAG"));
				nodoport.setPort(data.getExtras().getString("PORT"));
				nodoport.setAction(actions);
        		
				this.dsm.openDataBase();
        		
				if(this.dsm.updateNodoDevicePort(nodoport))
					Log.i(TAGNAME,"Created NodoDevicePort");
				else
					Log.i(TAGNAME,"Failed NodoDevicePort");
        		
				this.values = dsm.getPortOfDevice(this.device_id);
				this.adapter = new NodoDevicePortAdapter(this,this.values);
        	
				this.listView.setAdapter(this.adapter);
				this.edittext_searchtag.setText("");
				this.dsm.closeDataBase();

			}
			else {
				Toast.makeText(this,
					getResources().getString(R.string.error_portdeviceupdate)+":CODE:"+
							Integer.toString(resultCode)
							, Toast.LENGTH_LONG).show();
			}
			break;
		}
		/*if (requestCode == this.NEWDEVICEPORT_REQUEST) {
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
	        		
	        	this.values = dsm.getPortOfDevice(this.device_id);
	        	this.adapter = new NodoDevicePortAdapter(this,this.values);
	        		
	     	    this.listView.setAdapter(this.adapter);
	     	    this.edittext_searchtag.setText("");
	     	    this.dsm.closeDataBase();
	        }
		}*/
		
	}
	
	
	public NodoDevicePort getPortFromList(String port){
		NodoDevicePort nodoport = new  NodoDevicePort();
		
		for ( int i = 0; i < this.values.size() ; i++ ){
			Log.i(TAGNAME,"Compare: "+this.values.get(i).getPort() +
					"/" + port );
			if ( this.values.get(i).getPort().equals(port) ){
				nodoport = this.values.get(i);
			}
		}
		return nodoport;
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		final View v = view;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.title_portoptions)
	           .setItems(R.array.port_options, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   //OPTIONS
	            	   TextView port = (TextView)v.findViewById(R.id.textViewPort);
	            	   final int delete = 0, update = 1,detail_port = 2;
	            	   switch(which){
	            	   		case delete:
	            	   			dsm.openDataBase();
	            	   			dsm.deletePortOfDevice(device_id,
	            	   					port.getText().toString());
	            	   			break;
	            	   		case update:
	            	   			Intent updateport = new Intent(NodoDevicePortMainActivity.this,NodoDevicePortActivity.class);
	            	   			NodoDevicePort nodoport = getPortFromList(port.getText().toString());
	            	   			
	            	   			updateport.putExtra("ACTIONS",nodoport.getAction());
	            	   			updateport.putExtra("DEVICE",device_name);
	            	   			updateport.putExtra("DEVICE_ID",device_id);
	            	   			updateport.putExtra("ID",nodoport.getId());
	            	   			updateport.putExtra("PORT",nodoport.getPort());
	            	   			updateport.putExtra("TAG",nodoport.getTag());
	            	   			
	            	   			startActivityForResult(updateport,UPDATEDEVICEPORT_REQUEST);
	            	   			
	            	   			break;
	            	   		case detail_port:
	            	   			//Show all combinations possible to execute actions
	            	   			String message = "";
	            	   			final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NodoDevicePortMainActivity.this,
	            	   					android.R.layout.simple_list_item_1);
	            	   			
	            	   			for ( int i = 0 ; i < values.size() ; i++ ) {
	            	   				TextView tag = (TextView)findViewById(R.id.textViewPort);
	            	   				if ( tag.getText().toString().equals(values.get(i).getPort())) {
	            	   					String[] actions = values.get(i).getAction().split(",");
	            	   					for ( int j = 0 ; j < actions.length ; j ++ ){
	            	   						
	            	   						message = values.get(i).getTag() + " " + actions[j] + "\n";
	            	   						arrayAdapter.add(message);
	            	   						message = actions[j] + " " + values.get(i).getTag() + "\n";
	            	   						arrayAdapter.add(message);
	            	   					}
	            	   				}
	            	   			}
	            	   			
	            	   			AlertDialog.Builder builder = new AlertDialog.Builder(NodoDevicePortMainActivity.this);
	            	   			builder.setTitle(R.string.title_actionexecuteoption)
	            	   				.setAdapter(arrayAdapter, new DialogInterface.OnClickListener(){
	            	   					@Override
	            	   					public void onClick(DialogInterface dialog, int which) {
	            	   						// TODO Auto-generated method stub
	            	   						dialog.dismiss();
	            	   					}
	            	   				})
	            	   				.setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener() {
	            	   					@Override
	            	   					public void onClick(DialogInterface dialog, int which) {
	            	   						// TODO Auto-generated method stub
	            	   						dialog.dismiss();
	            	   					}
	            	   				});
	            	   				
	            	   			AlertDialog alert = builder.create();
	            	   			alert.show();
	            	   			break;
	            	   		default:
	            	   			break;
	            	   }
	            	   dsm.openDataBase();
	            	   values = dsm.getPortOfDevice(device_id);
	            	   adapter = new NodoDevicePortAdapter(NodoDevicePortMainActivity.this,
	            			   values);
	            	   listView.setAdapter(adapter);
	            	   dsm.closeDataBase();
	               }
	           });
		
	    AlertDialog alert = builder.create();
	    alert.show();
	    
		return false;
	}
	
	
	
}//End of class

