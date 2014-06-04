package cl.mamd.voice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cl.mamd.communication.DeviceHttpCommunication;
import cl.mamd.communication.HTTPExecuteInstruction;
import cl.mamd.datastore.DataStoreManager;
import cl.mamd.entity.NodoDevice;
import cl.mamd.entity.NodoDevicePort;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Activity for launch voice recognition
 */
public class NodoVoiceRecognitionActivity extends Activity implements OnItemClickListener {

	private TextView ipaddress;
	private TextView name;
	private TextView location;
	private TextView username;
	private TextView passwd;
	private EditText editText_voicerecog;
	private ListView listview;
	
	
	private List<NodoDevicePort> values;
	private NodoDevicePortAdapter adapter;
	private DataStoreManager dsm;
	private HashMap<String,String[]> action_to_set; 
	private String[] on_options;
	private String[] off_options;
	private NodoDevice nodo;
	
	
	
	private Integer device_id;
	
	
	
	private final String TAGNAME = "NodoVoiceRecognitionActivity";
	private final int RESULT_BROWSER = 0;
	private final int RESULT_SPEECH = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Disabled screen orientation changes
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle(getResources().getString(R.string.app_name));


		setContentView(R.layout.activity_nodo_voice_recognition);
		
		//Set content to widgets
		this.ipaddress = (TextView)findViewById(R.id.textView_nodoipaddress);        
	    this.name = (TextView)findViewById(R.id.textView_nodoname);
	    this.location = (TextView)findViewById(R.id.textView_nodolocation);
	    this.username = (TextView)findViewById(R.id.textView_nodousername);
	    this.passwd = (TextView)findViewById(R.id.textView_nodopasswd);
		this.editText_voicerecog = (EditText)findViewById(R.id.editText_voicerecog);
		this.editText_voicerecog.setFocusable(false);
		
		this.editText_voicerecog.setBackground(
				getResources().getDrawable(R.drawable.round_result_default));
		
		
		
		
	    this.listview = (ListView)findViewById(R.id.listView_voicerecognition);
	    
	    this.action_to_set = new HashMap<String,String[]>();
	    this.dsm = new DataStoreManager(this);
	    this.dsm.openDataBase();
	    this.values = new ArrayList<NodoDevicePort>();
	    
	    
	    Log.i(TAGNAME, "SIZE OF List<NodoDevicePort>():"+Integer.toString(this.values.size()));
	    
		Bundle extras = getIntent().getExtras();
        if (extras != null){
        	this.device_id = extras.getInt("ID"); 
        	this.ipaddress.setText(extras.getString("IPADDRESS"));        
            this.name.setText(extras.getString("NAME"));
            this.location.setText(extras.getString("LOCATION"));
            this.username.setText(extras.getString("USERNAME"));
            this.passwd.setText(extras.getString("PASSWD"));
            this.ipaddress.setClickable(true);
            
            this.on_options = extras.getStringArray("ON_OPTIONS");
            this.off_options = extras.getStringArray("OFF_OPTIONS");
            
            Log.i(TAGNAME, "size of on_options:"+this.on_options.length);
            Log.i(TAGNAME, "size of off_options:"+this.off_options.length);
            
            this.action_to_set.put("ON",this.on_options);
    	    this.action_to_set.put("OFF",this.off_options);
            
            this.values = dsm.getPortOfDevice(this.device_id); 
            if ( this.values != null )
            	Log.i(TAGNAME,"Size of arraylist port"+Integer.toString(this.values.size()));
            
          //Nodo
    		this.nodo = new NodoDevice();
    		this.nodo.setIpaddress(ipaddress.getText().toString());
    		this.nodo.setUsername(this.username.getText().toString());
    		this.nodo.setPasswd(this.passwd.getText().toString());
    		this.nodo.setName(this.name.getText().toString());
    		this.nodo.setLocation(this.location.getText().toString());
    		this.nodo.setId(this.device_id);
    		
        }
		
        this.adapter = new NodoDevicePortAdapter(this,this.values);
        this.listview.setAdapter(this.adapter);
        
        this.listview.setOnItemClickListener(this);
        
        checkWifiAvailability();
        
	}
	
    /**
     * @author mmoscoso
     * This method check if the wifi adapter is available
     * @param
     * @return
     * 
     */
    public void checkWifiAvailability(){
    	/*
    	 * Getting ConnectivityManager
    	 */
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (!mMobile.isConnected() && !mWifi.isConnected() ){
        	//Not network connection available
        	Toast.makeText(getApplicationContext(),
        			getResources().getString(R.string.error_notnetworkconnection),Toast.LENGTH_LONG).show();
        }
        
    }//
	
	/**
     * Menu of Activity
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nodo_voice_recognition, menu);
        return true;
    }
    
    /**
     * When item of menu is selected
     */
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Log.i(TAGNAME, "ID MENU ITEM:"+item.getItemId());
    	switch (item.getItemId()) {
    		case R.id.help_nodovoiceirecognition:
    			//Start a AlertDialog with information
    			Helpdialog();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
    /**
     * This function allows to user open a web browser
     * @param view
     */
	public void openBrowser(View view){
		final Intent intent = new Intent(Intent.ACTION_VIEW)
			.setData(Uri.parse("http://"+this.ipaddress.getText().toString()));
		startActivityForResult(intent,RESULT_BROWSER);
	}
	
	/**
	 * This function begin the voice recognition
	 * @param view
	 */
	public void buttonStartSpeech(View view){
		
		this.editText_voicerecog.setText("");
		this.editText_voicerecog.setVisibility(View.VISIBLE);
		this.editText_voicerecog.setBackground(
				getResources().getDrawable(R.drawable.round_result_default));
		this.editText_voicerecog.setGravity(Gravity.CENTER_HORIZONTAL);
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        startActivityForResult(intent, RESULT_SPEECH);
    
        Log.i(TAGNAME, "Stop recognition");
        
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        	case RESULT_SPEECH: 
        		if (resultCode == RESULT_OK && null != data) {
        			
        			Log.i(TAGNAME, "RESULT_OK");
        			ArrayList<String> text = data
        					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 
        			Log.i(TAGNAME, "Result of SPEECH:"+text.size());
	
        			//checking through all results
        			int i = 0;
        			while ( i < text.size()){
        				Log.i(TAGNAME, "Result:'"+text.get(i)+"'");
        				this.editText_voicerecog.setText(text.get(i));
        				
        				//Get text from voice recognition for search matches
        				Locale locale = Locale.getDefault();
            			String tag = text.get(i);
            			tag = tag.replace(" ","");
    					tag = tag.toLowerCase(locale);
        				
    					//Search in each port of device 
    					for ( int j = 0; j < this.values.size() ; j ++ ){
    						
    						
            				String lowerTagNodo = this.values.get(j).getTag().toLowerCase(locale);
            				lowerTagNodo = lowerTagNodo.replace(" ","");
            				
            				Log.i(TAGNAME, "tag.contains ='"+tag+"'/'"+lowerTagNodo+"'");
            				
            				if ( tag.contains(lowerTagNodo) ){
            					Log.i(TAGNAME, "tag.contains = TRUE");
            					String action = tag;
            					action = action.replace(lowerTagNodo,"");
            					String[] actionlist = this.values.get(j).getAction().split(",");
            					for ( int k = 0 ; k < actionlist.length ; k ++ ){
            						Log.i(TAGNAME, "action.equals ='"+action+"'/'"+actionlist[k]+"'");
            						if ( action.equals(actionlist[k])) {
            				
            							//Recognition Success
            							Log.i(TAGNAME, "Recognition success:'"+action+"'/'"+actionlist[k]+"'");
            							//this.editText_voicerecog.setBackgroundColor(getResources().getColor(
                    					//				R.color.green));
            							//this.editText_voicerecog.setBackground(
            							//		getResources().getDrawable(R.drawable.round_result_success));
            							
            							List<String> list = Arrays.asList(this.off_options);
            							if (list.contains(action)){
            								Log.i(TAGNAME, "OFF ACTION");
            								this.executeInstructionOnDevice(this.values.get(j).getPort(),
            										"off");
            							}
            							else {
            								Log.i(TAGNAME, "Is not in off_options");
            							}
            							list = Arrays.asList(this.on_options);
            							if (list.contains(action)){
            								Log.i(TAGNAME, "ON ACTION");
            								this.executeInstructionOnDevice(this.values.get(j).getPort(),
            										"on");
            							}
            							else {
            								Log.i(TAGNAME, "Is not in on_options");
            							}
            							
            							i = text.size();
            							k = actionlist.length;
            							j = this.values.size();
            						}
            					}
            				}
            			}
        				i++;
        			}
        				
        		}
        		break;
        }
    }
	
	private boolean executeInstructionOnDevice(String port,String option){
		
		Log.i(TAGNAME, "executeInstructionOnDevice");
		
		DeviceHttpCommunication dhc = new DeviceHttpCommunication(
				this.ipaddress.getText().toString(),this.username.getText().toString(),
				this.passwd.getText().toString(),2);
		
		String inst = "set+"+port+"+"+option;
		
		new HTTPExecuteInstruction(NodoVoiceRecognitionActivity.this,nodo,inst,this.editText_voicerecog)
			.execute();
		
		if ( dhc.executeInstructionSocket(inst)){
		//if (dhc.executeInstruction(inst)){
			return true;
		}
		else {
			return false;
		}		
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    Log.i(TAGNAME, ":On Resumen");
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    Log.i(TAGNAME, ":On Pause");
	}
	@Override
	protected void onDestroy() {
	    super.onPause();
	    this.dsm.closeDataBase();
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
    	    	
    	textmessage.setText(getResources().getText(R.string.help_message_nodo_voice_recognition));
    	texttitle.setText(getResources().getText(R.string.help_title_nodo_voice_recognition));
    	
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
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		//Show all combinations possible to execute actions
		String message = "";
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
				
		for ( int i = 0 ; i < this.values.size() ; i++ ) {
			TextView tag = (TextView)view.findViewById(R.id.textViewPort);
			if ( tag.getText().toString().equals(this.values.get(i).getPort())) {
				String[] actions = this.values.get(i).getAction().split(",");
				for ( int j = 0 ; j < actions.length ; j ++ ){
							
					message = this.values.get(i).getTag() + " " + actions[j] + "\n";
					arrayAdapter.add(message);
					message = actions[j] + " " + this.values.get(i).getTag() + "\n";
					arrayAdapter.add(message);
				}
			}
		}
			
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
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
	}
	
	
}
