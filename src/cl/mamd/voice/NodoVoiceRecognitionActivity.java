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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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
	
	
	//For voice Recognition
	private SpeechRecognizer sr;
	private RecognitionListener rlistener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
        
        
        this.rlistener = new RecognitionListener(){
			@Override
			public void onBeginningOfSpeech() {
				// TODO Auto-generated method stub
				Log.i(TAGNAME, "onReadyForSpeech");
			}
			@Override
			public void onBufferReceived(byte[] arg0) {
				// TODO Auto-generated method stub
				Log.i(TAGNAME, "onBeginningOfSpeech");
			}
			@Override
			public void onEndOfSpeech() {
				// TODO Auto-generated method stub
				Log.i(TAGNAME, "onEndofSpeech");
			}
			@Override
			public void onError(int error) {
				// TODO Auto-generated method stub
				Log.i(TAGNAME,  "error " +  error);
			}
			@Override
			public void onEvent(int eventType, Bundle params) {
				// TODO Auto-generated method stub
				Log.i(TAGNAME, "onEvent " + eventType);
			}
			@Override
			public void onPartialResults(Bundle partialResults) {
				// TODO Auto-generated method stub
				Log.i(TAGNAME, "onPartialResults");
			}
			@Override
			public void onReadyForSpeech(Bundle params) {
				// TODO Auto-generated method stub
				Log.i(TAGNAME,"onReadyForSpeech");
			}
			@Override
			public void onResults(Bundle results) {
				// TODO Auto-generated method stub
				String str = new String();
                Log.i(TAGNAME, "onResults " + results);
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (int i = 0; i < data.size(); i++)
                {
                	Log.i(TAGNAME,"Length of data:"+String.valueOf(data.get(i)).length());
                	Log.i(TAGNAME, "result :" + data.get(i));
                    str += data.get(i);
                }
                if (str.length() > 10){
                	Log.i(TAGNAME, "Addign result to LISTVIEW");
                	//values.add(str);
                	Log.i(TAGNAME, "values count:"+Integer.toString(values.size()));
                	//adapter.add(str);
                	listview.setAdapter(adapter);
           	    }
                Log.i(TAGNAME,"results: "+String.valueOf(data.size()));
			}
			@Override
			public void onRmsChanged(float rmsdB) {
				// TODO Auto-generated method stub
			}
        	
        };

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(rlistener); 
        
	}
	
	public void openBrowser(View view){
		final Intent intent = new Intent(Intent.ACTION_VIEW)
			.setData(Uri.parse("http://"+this.ipaddress.getText().toString()));
		startActivityForResult(intent,RESULT_BROWSER);
	}
	
	public void buttonStartSpeech(View view){
		
		this.editText_voicerecog.setText("");
		this.editText_voicerecog.setBackgroundColor(
				getResources().getColor(R.color.disabled_field));
		
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        startActivityForResult(intent, RESULT_SPEECH);
        //Checking alternative with settings
        //sr.startListening(intent);
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
            							
            							List<String> list = Arrays.asList(this.off_options);
            							if (list.contains(action)){
            								Log.i(TAGNAME, "OFF ACTION");
            							}
            							list = Arrays.asList(this.on_options);
            							if (list.contains(action)){
            								Log.i(TAGNAME, "ON ACTION");
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
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nodo_voice_recognition, menu);
		return true;
	}

}
