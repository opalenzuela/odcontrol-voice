package cl.mamd.voice;

import java.util.List;

import cl.mamd.entity.NodoDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author mmoscoso	
 * @version 0.2
 * @comment Class for create adapter of Devices
 */
public class NodoDeviceAdapter extends BaseAdapter {
	  private Context mcontext;
	  private List<NodoDevice> values;
	  

	  public NodoDeviceAdapter(Context context, List<NodoDevice> nodos) {
		  	super();
		    this.mcontext = context;
		    this.values = nodos;
	  }
	  //@Override
	  public View getView(int position, View rowView, ViewGroup parent) {
		  
		  LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  
		  rowView = inflater.inflate(R.layout.item_nodo_device,null);
		  TextView textName = (TextView)rowView.findViewById(R.id.textViewName);
		  TextView textIP = (TextView) rowView.findViewById(R.id.textViewIpAddress);
		  TextView textCredentials = (TextView)rowView.findViewById(R.id.textView_Credentials);
	    
		  textName.setText(values.get(position).getName());
		  textIP.setText(values.get(position).getIpaddress());
		  textCredentials.setText("("+values.get(position).getUsername()+":"
				  +values.get(position).getPasswd()+")");
		  
	    return rowView;
	  }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.size();
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

