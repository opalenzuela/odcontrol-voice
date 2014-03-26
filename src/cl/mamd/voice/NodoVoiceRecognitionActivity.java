package cl.mamd.voice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.mamd.datastore.DataStoreManager;
import cl.mamd.entity.NodoDevicePort;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	private ListView listview;
	private List<NodoDevicePort> values;
	private List<String> keywords;
	private NodoDevicePortAdapter adapter;
	private DataStoreManager dsm;
	private HashMap<String,String[]> action_to_set; 
	private String[] on_options = {"encender","levantar","subir"};
	private String[] off_options = {"apagar","bajar"};
	
	private Integer device_id;
	
	
	
	private final String TAGNAME = "NodoVoiceRecognitionActivity";
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
		
	    this.listview = (ListView)findViewById(R.id.listView_voicerecognition);
	    
	    //Keywords for voice recognition 
	    keywords = new ArrayList<String>();
	    this.keywords.add("apagar");
	    this.keywords.add("encender");
	    this.keywords.add("cortar");
	    this.keywords.add("activar");
	    this.keywords.add("leer");
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
	public void buttonStartSpeech(View view){
		
		
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
 
        				
        				Log.i(TAGNAME, "Result of SPEECH:"+text.get(0));
        				for(int i=0 ; i < this.on_options.length ; i++ ){
        					
        					Log.i(TAGNAME, "Checking ON option:"+this.on_options[i]);
        					if ( text.get(0).contains(this.on_options[i]) ){
        						Log.i(TAGNAME, "RESULT OF SPEECH HAS ON_OPTION:"+this.on_options[i]);
        						//Check witch port/tag was selected
        						
        						Locale locale = Locale.getDefault();
        						
        						String tag = text.get(0).replace(this.on_options[i],"");
        						tag = tag.replace(" ","");
        						tag = tag.toLowerCase(locale);
        						for ( int j = 0; j < this.values.size() ; j ++ ){
        							String lowerTagNodo = this.values.get(j).getTag().toLowerCase(locale);
        							lowerTagNodo = lowerTagNodo.replace(" ","");
        							Log.i(TAGNAME, "Checkig match with tag: "+lowerTagNodo+" ? " +tag);
        							if ( lowerTagNodo.equals(tag)  ){
        								Log.i(TAGNAME, "Tag of port recognized:"+lowerTagNodo+"/"+this.values.get(j).getTag());
        								String url = "http://";
        								url = url+this.ipaddress.getText().toString()+"/set+";
        								url = url+this.values.get(j).getPort()+"+ON";
        								
        								Toast.makeText(this,url,Toast.LENGTH_LONG).show();
        							}
        						}
        					}
        				}
        				//Check OFF options
        				for(int i=0 ; i < this.off_options.length ; i++ ){
        					if ( text.get(0).contains(this.off_options[i]) ){
        						Log.i(TAGNAME, "RESULT OF SPEECH HAS OFF_OPTION:"+this.off_options[i]);
        						
        						Locale locale = Locale.getDefault();
        						
        						String tag = text.get(0).replace(this.off_options[i],"");
        						tag = tag.replace(" ","");
        						tag = tag.toLowerCase(locale);
        						for ( int j = 0; j < this.values.size() ; j ++ ){
        							String lowerTagNodo = this.values.get(j).getTag().toLowerCase(locale);
        							lowerTagNodo = lowerTagNodo.replace(" ","");
        							Log.i(TAGNAME, "Checkig match with tag: "+lowerTagNodo+" ? " +tag);
        							if ( lowerTagNodo.equals(tag) ){
        								Log.i(TAGNAME, "Tag of port recognized:"+lowerTagNodo+"/"+this.values.get(j).getTag());
        								String url = "http://";
        								url = url+this.ipaddress.getText().toString()+"/set+";
        								url = url+this.values.get(j).getPort()+"+OFF";
        								
        								Toast.makeText(this,url,Toast.LENGTH_LONG).show();
        							}
        						}
        						
        					}
        				}
        				
        				/*
        				int i;
        				for ( i = 0; i < this.keywords.size() ; i++){
        					if ( text.get(0).contains(this.keywords.get(i)) ){
        						Log.i(TAGNAME,"Correct Match: '"+text.get(0)+"' contains the word:"+this.keywords.get(i));
        						//adapter.add(text.get(0));
        					}
        					else {
        						Log.i(TAGNAME, "Not match with keywords");
        					}
        				}
        				*/
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
