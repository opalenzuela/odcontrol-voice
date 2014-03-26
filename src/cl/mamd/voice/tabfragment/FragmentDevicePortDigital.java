package cl.mamd.voice.tabfragment;

import cl.mamd.voice.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentDevicePortDigital extends Fragment {
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View V = inflater.inflate(R.layout.fragment_device, container, false);
    	TextView test = (TextView)V.findViewById(R.id.textView1test);
    	test.setText("DIGITAL");
    
    	
    	return V;
	}
 
}