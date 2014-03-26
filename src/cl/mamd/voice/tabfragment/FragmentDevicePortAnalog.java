package cl.mamd.voice.tabfragment;

import cl.mamd.voice.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentDevicePortAnalog extends Fragment {
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View V = inflater.inflate(R.layout.fragment_device_port_analog, container, false);   
    	return V;
	}
    public void testButtonFrame(View v){
    	Toast.makeText(getActivity(), "ANALOG",Toast.LENGTH_LONG).show();
    }
}
