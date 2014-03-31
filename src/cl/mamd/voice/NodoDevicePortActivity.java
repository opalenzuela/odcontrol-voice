package cl.mamd.voice;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Activity for create and view data of device
 */
public class NodoDevicePortActivity extends Activity {
	
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

	
	protected void onCreate(Bundle savedInstanceState) {
		
		
		
		super.onCreate(savedInstanceState);
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
        	this.port.setText(extras.getString("PORT"));        
            this.tag.setText(extras.getString("TAG"));
            this.device.setText(extras.getString("DEVICE"));
            this.device.setFocusable(false);
            this.device.setBackgroundColor(getResources().getColor(R.color.disabled_field));
            this.device_id = extras.getInt("DEVICE_ID");
        }

        this.listview.setAdapter(adapter);
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

        				
        				builder.setMessage(getResources().getText(R.string.message_confirmaddaction).toString()+text.get(0))
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
        					   
        				AlertDialog dialog = builder.create();
        				dialog.show();
        		}
        		break;
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
		
		if (this.values.size() == 0 ) {
			return false;
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
}
