package cl.mamd.voice;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

public class VoiceMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_main);
        
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
        	Toast.makeText(this,getResources().getString(R.string.wifi_connection_state) 
        			+ "OK", Toast.LENGTH_LONG).show();
        }
        else {
        	Toast.makeText(this,getResources().getString(R.string.wifi_connection_state) 
        			+ "OFF", Toast.LENGTH_LONG).show();
        	
        	startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.voice_main, menu);
        return true;
    }
    
}
