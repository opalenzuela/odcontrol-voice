package cl.mamd.voice;

import java.util.ArrayList;
import java.util.List;

import cl.mamd.entity.NodoDevicePort;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Class for create adapter of Devices Ports
 */
public class NodoDevicePortAdapter extends BaseAdapter {
	  private Context mcontext;
	  private List<NodoDevicePort> values;
	  private static String TAGNAME = "NodoDeviceAdapter";
	  

	  public NodoDevicePortAdapter(Context context, List<NodoDevicePort> nodos) {
		  	super();
		    this.mcontext = context;
		    this.values = nodos;
	  }
	  //@Override
	  public View getView(int position, View rowView, ViewGroup parent) {
		  
		  LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  
		  Log.i(TAGNAME,"Creating textview for Nodo Device Adapter");
		  rowView = inflater.inflate(R.layout.item_nodo_device_port,null);
		  TextView textPort = (TextView)rowView.findViewById(R.id.textViewPort);
		  TextView textTag = (TextView) rowView.findViewById(R.id.textViewTag);
		  TextView textActions = (TextView)rowView.findViewById(R.id.textViewActions);
	    
		  textPort.setText(values.get(position).getPort());
		  textTag.setText(values.get(position).getTag());
		  textActions.setText(values.get(position).getAction());
		  
	    return rowView;
	  }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.values.size(); 
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}

