package cl.mamd.voice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Activity for create and view data of device
 */
public class NodoDevicePortActivity extends Activity implements OnItemClickListener {
	
	private EditText port;
	private EditText tag;
	private EditText device;
	private Button button;
	private final String TAGNAME = "NodoDevicePortActivity";
	private final int RESULT_SPEECH = 0;
	private int device_id = 0;
	private int id = 0;
	
	private ListView listview;
	private List<String> values;
	private ArrayAdapter<String> adapter;
	private String[] on_option = {};
	private String[] off_option = {};
	private String[] portnames = {};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Disabled screen orientation changes and remove title
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.nodo_device_port);

        this.port = (EditText)findViewById(R.id.editText_port);        
        this.tag = (EditText)findViewById(R.id.editText_tag);
        this.device = (EditText)findViewById(R.id.editText_device);
        this.button = (Button)findViewById(R.id.button_save);
        
        this.listview = (ListView)findViewById(R.id.listViewDevicePort);
        this.values = new ArrayList<String>();
	    
	    adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, values);

	    
	    
        Bundle extras = getIntent().getExtras();
        if (extras != null){
        	this.id = extras.getInt("ID");
        	if ( this.id != 0 ){
        		this.port.setFocusable(false);
        		this.port.setBackgroundColor(getResources().getColor(R.color.disabled_field));
        		this.button.setText(R.string.button_updatedevice);
        		String[] actions = extras.getString("ACTIONS").split(",");
        		for ( int i = 0 ; i < actions.length ; i++ )
        			if ( !actions[i].equals("") )
        				adapter.add(actions[i]);
        		
        	}
        	this.portnames = extras.getStringArray("PORTNAMES");
        	this.port.setText(extras.getString("PORT"));        
            this.tag.setText(extras.getString("TAG"));
            this.device.setText(extras.getString("DEVICE"));
            this.device.setFocusable(false);
            this.device.setBackgroundColor(getResources().getColor(R.color.disabled_field));
            this.device_id = extras.getInt("DEVICE_ID");
        }

        this.listview.setAdapter(adapter);
        this.listview.setOnItemClickListener(this);
        
        getOptionsFromPreferences();
        
	}
	
		
	/**
     * Menu of Activity
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nodo_device_port, menu);
        return true;
    }
    
    /**
     * When item of menu is selected
     */
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Log.i(TAGNAME, "ID MENU ITEM:"+item.getItemId());
    	switch (item.getItemId()) {
    		case R.id.help_nododeviceport:
    			//Start a AlertDialog with information
    			Helpdialog();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	
	
	
	public boolean addActionByVoice(View view){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        startActivityForResult(intent, RESULT_SPEECH);
        //Checking alternative with settings
        //sr.startListening(intent);
        Log.i(TAGNAME, "Stop recognition");
        return true;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        	case RESULT_SPEECH: 
        		if (resultCode == RESULT_OK && null != data) {
        			Log.i(TAGNAME, "RESULT_OK");
        				final ArrayList<String> text = data
        						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 
        				//CONFIR ACTION
        				AlertDialog.Builder builder = new AlertDialog.Builder(this);

        				//Check if exists on Optiosn
        				boolean result = false;
        				int i;
        				
        				for ( i = 0 ; i < this.on_option.length ; i++ ){
        					Log.i(TAGNAME, "Checking "+this.on_option[i] +" whit "+text.get(0));
        					if ( this.on_option[i].equals(text.get(0)) ){
        						result = true;
        					}
        				}
        				if ( result == false ) {
        					for ( i = 0 ; i < this.off_option.length ; i++ ){
        						Log.i(TAGNAME, "Checking "+this.off_option[i] +" whit "+text.get(0));
            					if ( this.off_option[i].equals(text.get(0)) ){
            						result = true;
            					}
            				}
        				}
        				
        				if (result) {
        					builder.setMessage(
        						getResources().getText(R.string.message_confirmaddactionbegin).toString()+text.get(0)+
        						getResources().getText(R.string.message_confirmaddactionend).toString()
        						)
        				       .setTitle(R.string.title_action)
        				       .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener() {
        				    	   public void onClick(DialogInterface dialog,int id) {
        				    		   adapter.add(text.get(0));
        				    	   }
        				       })
        				       .setNegativeButton(R.string.button_cancel,new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									
								}
        				    	   
        				       });
        				}
        				else {
        					builder.setMessage(
            						getResources().getText(R.string.message_refuseactionbegin).toString()+text.get(0)+
            						getResources().getText(R.string.message_refuseactionend).toString()
            						)
            				       .setTitle(R.string.title_action)
            				       .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener() {
            				    	   public void onClick(DialogInterface dialog,int id) {
            				    	
            				    	   }
            				       });
        				}
        				AlertDialog dialog = builder.create();
        				dialog.show();
        				
        		}
        		break;
        }
    }
	
	private void getOptionsFromPreferences(){
		//Check if recognition is valid.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        
        String[] on = getResources().getStringArray(R.array.on_options_values);
        Set<String> onSet = new HashSet<String>(Arrays.asList(on));
        
        String[] off = getResources().getStringArray(R.array.off_options_values);
        Set<String> offSet = new HashSet<String>(Arrays.asList(off));
        
        
        Set<String> on_list = sharedPref.getStringSet("on_options_list",onSet);
        Set<String> off_list = sharedPref.getStringSet("off_options_list",offSet);
        
        
        if (on_list != null && off_list != null ) 
        {
        	
        	this.on_option = new String[on_list.size()];
        	this.off_option = new String[off_list.size()];
        	int position = 0;
        
        	for (String s : on_list) {
        		on_option[position] = s;
        		position++;
        	}
        
        	position = 0;
        
        	for (String s : off_list) {
        		off_option[position] = s;
        		position++;
        	}
        }

	}
	
	
	public boolean addDevicePortButton(View view){
		
		String[] strarray = new String[values.size()];
		values.toArray(strarray);
		Intent data = new Intent();
		
		//VALIDATE CONTENT OF EDITTEXT
		if (this.port.getText().toString().equals("") || 
				this.port.getText().toString().length() > 5 ) {
				this.port.setError(
					getResources().getString(
							R.string.error_porterror)
			);
			return false;
		}
		if (this.tag.getText().toString().equals("")){
		
			this.tag.setError(
					getResources().getString(R.string.error_tagerror)
					);
			return false;
		}
		
		if (this.values.size() == 0 ) {
			Toast.makeText(this,getResources().getString(R.string.error_actionerror)
					,Toast.LENGTH_LONG).show();
			return false;
		}
		
		int i;
		for ( i = 0 ; i < this.portnames.length ; i++ ) {
			if ( this.port.getText().toString().equals(this.portnames[i]) && this.id == 0 ){
				Log.i(TAGNAME, "ERROR:DUPLICATE PORT");
				Toast.makeText(this,getResources().getString(R.string.error_portduplicate)
						,Toast.LENGTH_LONG).show();
				return false;
			}
		}
		
		
		
		data.putExtra("ID",this.id);
		data.putExtra("DEVICE_ID",this.device_id);
		data.putExtra("ACTIONS", strarray);
		data.putExtra("PORT",this.port.getText().toString());
		data.putExtra("TAG",this.tag.getText().toString());
			
			
		// Activity finished ok, return the data
		setResult(RESULT_OK, data);
		finish();
		this.finish();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		//final View v = view;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final int position_value = position;
	    builder.setTitle(R.string.title_remove_action)
	    .setMessage(getResources().getString(R.string.message_remove_action_port) + values.get(position).toString())
	    .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener() {
	    	   public void onClick(DialogInterface dialog,int id) {
	    		   values.remove(position_value);
	    		   //Remove from DATABASE
	    		   
	    		   
	    		   
	    		   adapter = new ArrayAdapter<String>(NodoDevicePortActivity.this,
	    		            android.R.layout.simple_list_item_1, values);
	    		   listview.setAdapter(adapter);
	    	   }
	       })
	       .setNegativeButton(R.string.button_cancel,new DialogInterface.OnClickListener() {
	    	   public void onClick(DialogInterface dialog, int id) {
	    		   
	 
	    	   }
	       });
	           
	    builder.create();
	    builder.show();	
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
    	    	
    	textmessage.setText(getResources().getText(R.string.help_message_nodo_device_port));
    	texttitle.setText(getResources().getText(R.string.help_title_nodo_device_port));
    	
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

	
	
}
