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
package cl.mamd.voice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cl.mamd.datastore.DataStoreManager;
import cl.mamd.entity.NodoDevicePort;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	private String device_address;
	private EditText edittext_searchtag;
	private ListView listView;
	
	private int device_id = 0;
	private DataStoreManager dsm;
	private NodoDevicePortAdapter adapter;
	private List<NodoDevicePort> values;
	private String[] portnames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Disabled screen orientation changes
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle(getResources().getString(R.string.app_name));
        
		setContentView(R.layout.activity_nodo_device_port);
		
		Bundle extras = getIntent().getExtras();
		this.device_id = extras.getInt("DEVICE_ID");
		this.device_name = extras.getString("DEVICE_NAME");
		this.device_address = extras.getString("DEVICE_ADDRESS");
		this.values = new ArrayList<NodoDevicePort>();
		
		//Comment
		Log.i(TAGNAME,"Managment port for device ("+Integer.toString(this.device_id)+"/"+this.device_name+")");
		
		
		this.dsm = new DataStoreManager(this);
		this.edittext_searchtag = (EditText)findViewById(R.id.edittext_tagsearch);
		this.listView = (ListView)findViewById(R.id.listViewDevicePorts);
		
		this.dsm = new DataStoreManager(this);
		this.dsm.openDataBase();
		this.values = dsm.getPortOfDevice(this.device_id);
		
		//
		int i;
		this.portnames = new String[this.values.size()];
				
		for ( i = 0 ; i < this.values.size() ; i++ ){
			portnames[i] = this.values.get(i).getPort();
		}
		//
		
		
		this.adapter = new NodoDevicePortAdapter(this,this.values);
		
		this.listView.setAdapter(adapter);
		this.listView.setOnItemLongClickListener(this);
		
		TextView textview = (TextView)findViewById(R.id.textView_devicenameinfo);
		textview.setGravity(Gravity.CENTER);
		textview.setText(createTitleForDeviceInformation());
		
		
	}
	
	/**
     * Menu of Activity
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nodo_device_port_main, menu);
        return true;
    }
    
    /**
     * When item of menu is selected
     */
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Log.i(TAGNAME, "ID MENU ITEM:"+item.getItemId());
    	switch (item.getItemId()) {
    		case R.id.help_nododeviceportmain:
    			//Start a AlertDialog with information
    			Helpdialog();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

	
    
	
	
	
	/**
	 * 
	 * @return
	 */
	private String createTitleForDeviceInformation(){
		return this.device_name+"/"+this.device_address;
	}
	
	/**
	 * 
	 * @param view
	 */
	public void addDevicePortButton(View view){
		
		String tag = this.edittext_searchtag.getText().toString();
		tag = tag.replace(" ","");
		
		String tagport = "";
		//Check if Tag Exists
		this.dsm.openDataBase();
		this.values = dsm.getPortOfDevice(this.device_id);
		this.dsm.closeDataBase();
		
		int i;
		for ( i = 0 ; i < this.values.size() ; i++ ){
			Locale locale = Locale.getDefault();
			tagport = this.values.get(i).getTag().replace(" ","").toLowerCase(locale);
			Log.i(TAGNAME,tagport+ " equals ="+tag);
			if (tagport.equals(tag)){
				Toast.makeText(this,
						getResources().getString(R.string.error_tagduplicate),
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		
		
		Intent newport = new Intent(this,NodoDevicePortActivity.class);
		newport.putExtra("ID",0);
		newport.putExtra("PORT","");
		newport.putExtra("TAG",this.edittext_searchtag.getText().toString());
		newport.putExtra("DEVICE",this.device_name);
		newport.putExtra("DEVICE_ID",this.device_id);
		newport.putExtra("PORTNAMES",this.portnames);
		startActivityForResult(newport,NEWDEVICEPORT_REQUEST);
	}
	
	/**
	 * 
	 * @param view
	 */
	public void searchDevicePortButton(View view){
		String tagsearch = this.edittext_searchtag.getText().toString(); 
		List<NodoDevicePort> ports = new ArrayList<NodoDevicePort>();
		
		
		if (tagsearch.equals("")){
			this.dsm.openDataBase();
			this.values = dsm.getPortOfDevice(this.device_id);
			this.dsm.closeDataBase();
		}
		else {
			int i;
			for ( i = 0 ; i < this.values.size() ; i++ ){
				if (this.values.get(i).getTag().contains(tagsearch)){
					ports.add(this.values.get(i));
				}
			}
			this.values = ports;
		}
		this.edittext_searchtag.setText("");
		this.adapter = new NodoDevicePortAdapter(this,this.values);
		this.listView.setAdapter(adapter);
		
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
						getResources().getString(R.string.error_portdevicesave)
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
					getResources().getString(R.string.error_portdeviceupdate)
							, Toast.LENGTH_LONG).show();
			}
			break;
		}
		
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
	            	   			updateport.putExtra("PORTNAMES",portnames);
	            	   			
	            	   			startActivityForResult(updateport,UPDATEDEVICEPORT_REQUEST);
	            	   			
	            	   			break;
	            	   		case detail_port:
	            	   			//Show all combinations possible to execute actions
	            	   			String message = "";
	            	   			final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NodoDevicePortMainActivity.this,
	            	   					android.R.layout.simple_list_item_1);
	            	   			
	            	   			for ( int i = 0 ; i < values.size() ; i++ ) {
	            	   				//TextView tag = (TextView)v.findViewById(R.id.textViewPort);
	            	   				TextView tag = port;
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
	
	/**
	 * Show dialog for Help information
	 */
	public void Helpdialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	LayoutInflater inflater = this.getLayoutInflater();
    	
    	View customView = inflater.inflate(R.layout.help_layout, null);
    	TextView textmessage = (TextView)customView.findViewById(R.id.help_layout_message);
    	
    	TextView texttitle = (TextView)customView.findViewById(R.id.help_layout_title);
    	    	
    	textmessage.setText(getResources().getText(R.string.help_message_nodo_device_port_main));
    	texttitle.setText(getResources().getText(R.string.help_title_nodo_device_port_main));
    	
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(customView);
		builder.setTitle(R.string.help_title)
		       .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog,int id) {
		    	   }
		       });
		AlertDialog dialog = builder.create();
		dialog.show();
    }
	
	
}//End of class

