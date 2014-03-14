package cl.mamd.voice;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
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
	private ListView listview;
	private List<String> values;
	private List<String> keywords;
	private ArrayAdapter<String> adapter;
	
	private final String TAGNAME = "NodoVoiceRecognitionActivity";
	private int RESULT_SPEECH = 1;
	
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
	    
	    values = new ArrayList<String>();
	    
	    adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, values);
	    
	    this.listview.setAdapter(adapter);
	    
		Bundle extras = getIntent().getExtras();
        if (extras != null){
        	this.ipaddress.setText(extras.getString("IPADDRESS"));        
            this.name.setText(extras.getString("NAME"));
            this.location.setText(extras.getString("LOCATION"));
            this.username.setText(extras.getString("USERNAME"));
            this.passwd.setText(extras.getString("PASSWD"));
        }
		
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
                	values.add(str);
                	Log.i(TAGNAME, "values count:"+Integer.toString(values.size()));
                	adapter.add(str);
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
        	case 1: 
        		if (resultCode == RESULT_OK && null != data) {
        			Log.i(TAGNAME, "RESULT_OK");
        				ArrayList<String> text = data
        						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 
        				int i;
        				for ( i = 0; i < this.keywords.size() ; i++){
        					if ( text.get(0).contains(this.keywords.get(i)) ){
        						Log.i(TAGNAME,"Correct Match: '"+text.get(0)+"' contains the word:"+this.keywords.get(i));
        						adapter.add(text.get(0));
        					}
        					else {
        						Log.i(TAGNAME, "Not match with keywords");
        					}
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
