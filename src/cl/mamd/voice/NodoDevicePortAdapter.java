/**
Copyright 2014 Manuel Moscoso Dominguez
This file is part of ODControl-Voice.

ODCOntrol-Voice is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation, either version 3 of the License, or 
(at your option) any later version.

ODControl-Voice is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License for more details.

You should have received a copy of the GNU General Public License 
along with ODControl-Voice.  If not, see <http://www.gnu.org/licenses/>.

**/
package cl.mamd.voice;

import java.util.List;

import cl.mamd.entity.NodoDevicePort;

import android.content.Context;
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
	  

	  public NodoDevicePortAdapter(Context context, List<NodoDevicePort> nodos) {
		  	super();
		    this.mcontext = context;
		    this.values = nodos;
	  }
	  //@Override
	  public View getView(int position, View rowView, ViewGroup parent) {
		  
		  LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  
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

