/*
 	Copyright 2013 Oscar Crespo Salazar
 	Copyright 2013 Gorka Jimeno Garrachon
 	Copyright 2013 Luis Valero Martin
  
	This file is part of VNC++.

	VNC++ is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	any later version.
	
	VNC++ is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with VNC++.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.farfuteam.vncpp.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import es.farfuteam.vncpp.controller.ListFragmentTab;
import es.farfuteam.vncpp.controller.R;
import es.farfuteam.vncpp.model.sql.Connection;

public class Adapter extends BaseAdapter {	
	
	private Activity activity;
	private ArrayList<Connection> list;
	

	
	public Adapter(Activity act,ArrayList<Connection> u){
		super();
		activity = act;
		setList(u);
	}


	@Override
	public int getCount() {
		return getList().size();
	}

	@Override
	public Object getItem(int position) {
		return getList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		
		   View vi=convertView;
	         
		    if(convertView == null) {
		      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		      vi = inflater.inflate(R.layout.element_user, null);
		    }
		             
		    final Connection user = getList().get(position);
		         		    
	        
	       final CheckBox iconFav = (CheckBox)vi.findViewById(R.id.checkFav);
	       iconFav.setTag(user);    	       
	       
	       iconFav.setOnCheckedChangeListener(
	          new CheckBox.OnCheckedChangeListener() {
	              @Override
				public void onCheckedChanged(CompoundButton buttonView,
	                                                  boolean isChecked) {
	                  if (isChecked && !user.isFav()) {
	                	  iconFav.setButtonDrawable(R.drawable.star_ful);
	                	  user.setFav(true);
	                	  
	                	  ListFragmentTab l = ListFragmentTab.getInstance();
	                	  l.refreshFavorites(user);
	                	  
	                  }
	                  else {
	                	  iconFav.setButtonDrawable(R.drawable.star_emp);
	                	  user.setFav(false);
	                	  
	                	  ListFragmentTab l = ListFragmentTab.getInstance();
	                	  l.refreshFavorites(user);
	                	  
	                  }
	                  }
	              });
	       
	       //para mantener el estado cuando rota
		    if (user.isFav()){
		    	iconFav.setButtonDrawable(R.drawable.star_ful);
		    }
		    else{
		    	iconFav.setButtonDrawable(R.drawable.star_emp);
		    }
		         
		    TextView name = (TextView) vi.findViewById(R.id.nameConnection);
		    name.setText(user.getName());
		    
		    TextView ip_view = (TextView) vi.findViewById(R.id.descIP);
		    ip_view.setText(user.getIP());
		    
		       CheckBox iconConnect = (CheckBox)vi.findViewById(R.id.checkConnect);
		       iconConnect.setTag(user);   		    
		 
		    return vi;
		
	}	
	
    public void deleteRow(Connection row) {
        
        if(this.getList().contains(row)) {
            this.getList().remove(row);
        }   
        
    }
    


	public ArrayList<Connection> getList() {
		return list;
	}

	public void setList(ArrayList<Connection> list) {
		this.list = list;
	}


  }
