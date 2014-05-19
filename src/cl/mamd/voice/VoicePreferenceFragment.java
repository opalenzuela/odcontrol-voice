package cl.mamd.voice;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class VoicePreferenceFragment extends PreferenceFragment {
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  	addPreferencesFromResource(R.xml.voice_main_preference);
	 } 
}
