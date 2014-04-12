package cl.mamd.voice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cl.mamd.communication.DeviceHttpCommunication;
import cl.mamd.datastore.DataStoreManager;
import cl.mamd.entity.NodoDevicePort;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Activity for launch voice recognition
 */
public class NodoVoiceRecognitionActivity extends Activity {

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
	
	
	
	private Integer device_id;
	
	
	
	private final String TAGNAME = "NodoVoiceRecognitionActivity";
	private final int RESULT_BROWSER = 0;
	private final int RESULT_SPEECH = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Disabled screen orientation changes and remove title
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_nodo_voice_recognition);
		

		
		
		this.ipaddress = (TextView)findViewById(R.id.textView_nodoipaddress);        
	    this.name = (TextView)findViewById(R.id.textView_nodoname);
	    this.location = (TextView)findViewById(R.id.textView_nodolocation);
	    this.username = (TextView)findViewById(R.id.textView_nodousername);
	    this.passwd = (TextView)findViewById(R.id.textView_nodopasswd);
		this.editText_voicerecog = (EditText)findViewById(R.id.editText_voicerecog);
		
		this.editText_voicerecog.setFocusable(false);
		this.editText_voicerecog.setBackgroundColor(getResources().getColor(
				R.color.disabled_field));
		
		this.on_options = getResources().getStringArray(R.array.on_options);
		this.off_options = getResources().getStringArray(R.array.off_options);
	    
	    this.listview = (ListView)findViewById(R.id.listView_voicerecognition);
	    
	    
	    this.action_to_set = new HashMap<String,String[]>();
	    
	    
	    this.dsm = new DataStoreManager(this);
	    this.dsm.openDataBase();
	    
	    this.values = new ArrayList<NodoDevicePort>();
	    
	    this.action_to_set.put("ON",this.on_options);
	    this.action_to_set.put("OFF",this.off_options);
	    
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
            
            
            Log.i(TAGNAME, "ID of Device for control:"+Integer.toString(this.device_id));
            
            this.values = dsm.getPortOfDevice(this.device_id); 
            if ( this.values != null )
            	Log.i(TAGNAME,"Size of arraylist port"+Integer.toString(this.values.size()));
            
        }
		
        this.adapter = new NodoDevicePortAdapter(this,this.values);
        this.listview.setAdapter(this.adapter);
        
	}
	
	public void openBrowser(View view){
		final Intent intent = new Intent(Intent.ACTION_VIEW)
			.setData(Uri.parse("http://"+this.ipaddress.getText().toString()));
		startActivityForResult(intent,RESULT_BROWSER);
	}
	
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
        				
        				Locale locale = Locale.getDefault();
            			String tag = text.get(i);
            			tag = tag.replace(" ","");
    					tag = tag.toLowerCase(locale);
        				
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
            							this.editText_voicerecog.setBackgroundColor(getResources().getColor(
                    									R.color.recognition_success));
            							this.editText_voicerecog.setBackground(
            									getResources().getDrawable(R.drawable.round_result_success));
            							
            							List<String> list = Arrays.asList(this.off_options);
            							if (list.contains(action)){
            								Log.i(TAGNAME, "OFF ACTION");
            								this.executeInstructionOnDevice(this.values.get(j).getPort(),
            										"OFF");
            							}
            							list = Arrays.asList(this.on_options);
            							if (list.contains(action)){
            								Log.i(TAGNAME, "ON ACTION");
            								this.executeInstructionOnDevice(this.values.get(j).getPort(),
            										"ON");
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
		DeviceHttpCommunication dhc = new DeviceHttpCommunication(
				this.ipaddress.getText().toString(),this.username.getText().toString(),
				this.passwd.getText().toString());
		
		String inst = "set+"+port+"+"+option;
		if (dhc.executeInstruction(inst)){
			return true;
		}
		else {
			return false;
		}		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nodo_voice_recognition, menu);
		return true;
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
	
	
}
