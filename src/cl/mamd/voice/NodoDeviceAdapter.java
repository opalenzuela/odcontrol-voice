package cl.mamd.voice;

import java.util.List;

import cl.mamd.entity.NodoDevice;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NodoDeviceAdapter extends BaseAdapter {
	  private Context mcontext;
	  private List<NodoDevice> values;
	  

	  public NodoDeviceAdapter(Context context, List<NodoDevice> nodos) {
		  	super();
		    this.mcontext = context;
		    this.values = nodos;
		    Log.i(this.getClass().getName(),"---------->"+nodos.get(0).getName());
		    
	  }
	  //@Override
	  public View getView(int position, View rowView, ViewGroup parent) {
		  
		  LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  
		  Log.i("TEST","::::::::::::::::::::::::->"+position+"----"+values.get(position).getName());
		  
		  rowView = inflater.inflate(R.layout.item_nodo_device,null);
		  TextView textName = (TextView)rowView.findViewById(R.id.textViewName);
		  TextView textIP = (TextView) rowView.findViewById(R.id.textViewIpAddress);
	    
		  textName.setText(values.get(position).getName());
		  textIP.setText(values.get(position).getIpaddress());
		  
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
		return null;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}

