package cl.mamd.voice;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class PreferenceVoice extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(getResources().getString(R.string.app_name));
		
		
		VoicePreferenceFragment prefFrag = new VoicePreferenceFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(android.R.id.content, prefFrag);
		fragmentTransaction.commit();
		
	}
}
