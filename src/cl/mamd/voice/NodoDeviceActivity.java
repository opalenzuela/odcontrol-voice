package cl.mamd.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Activity for create and view data of device
 */
public class NodoDeviceActivity extends Activity {
	
	private EditText ipaddress;
	private EditText name;
	private EditText location;
	private EditText username;
	private EditText passwd;
	private Button button_action;
	private final String TAGNAME = "NodoDeviceAddDialog";
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nodo_device);

        this.ipaddress = (EditText)findViewById(R.id.editText_ipaddress);        
        this.name = (EditText)findViewById(R.id.editText_name);
        this.location = (EditText)findViewById(R.id.editText_location);
        this.username = (EditText)findViewById(R.id.editText_username);
        this.passwd = (EditText)findViewById(R.id.editText_passwd);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getInt("OPTION") == 2){
        	this.ipaddress.setText(extras.getString("IPADDRESS"));        
            this.name.setText(extras.getString("NAME"));
            this.location.setText(extras.getString("LOCATION"));
            this.username.setText(extras.getString("USERNAME"));
            this.passwd.setText(extras.getString("PASSWD"));
            this.button_action = (Button)findViewById(R.id.button_adddevice);
            this.button_action.setText(R.string.button_updatedevice);
            
        }
        else {
        	setIpToEditText();
        }
        this.ipaddress.setFocusable(false);
	}
	private void setIpToEditText(){
		Bundle extras = getIntent().getExtras();
        String ipvalue = "";
		if (extras != null)
        	ipvalue = extras.getString("IPADDRESS");
		else
			ipvalue= "0";
		Log.i(TAGNAME,"Value of IP is:"+ipvalue);
		this.ipaddress.setText(ipvalue);
		this.ipaddress.setFocusable(false);
	}
	public void addNodoDevice(View view){
		
		Intent data = new Intent();
		data.putExtra("IPADDRESS",this.ipaddress.getText().toString());
		data.putExtra("NAME", this.name.getText().toString());
		data.putExtra("LOCATION", this.location.getText().toString());
		data.putExtra("USERNAME", this.username.getText().toString());
		data.putExtra("PASSWD", this.passwd.getText().toString());
		
		// Activity finished ok, return the data
		setResult(RESULT_OK, data);
		finish();

		
		this.finish();
	}
}
