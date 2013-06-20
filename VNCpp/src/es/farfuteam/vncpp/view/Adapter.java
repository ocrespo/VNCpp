/*
 	Copyright 2013 Oscar Crespo Salazar
 	Copyright 2013 Gorka Jimeno Garrachon
 	Copyright 2013 Luis Valero Martin
  
	This file is part of VNCpp.

	VNCpp is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	any later version.
	
	VNCpp is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with VNCpp.  If not, see <http://www.gnu.org/licenses/>.
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
import es.farfuteam.vncpp.controller.R;
import es.farfuteam.vncpp.model.sql.Connection;

/**
 * @class Adapter
 * @brief This is the Adapter class.
 * 
 * This is the detailed description
 *
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends BaseAdapter
 */
public class Adapter extends BaseAdapter {	
	
	/** the activity which uses the Adapter*/
	private Activity activity;
	/** List of the Adapter */
	private ArrayList<Connection> list;
	/** the listfragment*/
	private ListFragmentTab listFragment;

	/**
	 * @brief Constructor method
	 * @param act The activity
	 * @param u Connection List
	 * @details Constructor method
	 */
	public Adapter(Activity act,ArrayList<Connection> u){
		super();
		activity = act;
		listFragment = new ListFragmentTab();
		setList(u);
	}

	/**
	 * @brief Returns the size of the list
	 * @return size
	 * @details Returns the size of the list
	 */
	@Override
	public int getCount() {
		return getList().size();
	}

	/**
	 * @brief Returns the Object at the position
	 * @param position
	 * @return Object at the position
	 * @details Returns the Object at the position
	 */
	@Override
	public Object getItem(int position) {
		return getList().get(position);
	}

	/**
	 * @brief Returns the id at the position
	 * @param position
	 * @return ID at the position
	 * @details Returns the id at the position
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @brief Returns the view clicked in the list
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return view
	 * @details Returns the view clicked in the list, put the connection name, the ip, and the
	 * icon star
	 */
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
	                  if (isChecked){
	                	  iconFav.setButtonDrawable(R.drawable.star_ful);
	                	  user.setFav(true);	                	  
	                	 
	                  }
	                  else {
	                	  iconFav.setButtonDrawable(R.drawable.star_emp);
	                	  user.setFav(false);	 
	                	  
	                  }
	                  listFragment.refreshFavorites(user);
	              }
	          });
	       
	       //para mantener el estado cuando rota
		    if (user.isFav()){
		    	iconFav.setButtonDrawable(R.drawable.star_ful);
		    	iconFav.setChecked(true);
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
		
	/**
	 * @brief Deletes the indicated row
	 * @param row the row of the list
	 * @details Deletes the indicated row
	 */
    public void deleteRow(Connection row) {
        
        if(this.getList().contains(row)) {
            this.getList().remove(row);
        }   
        
    }    

	/**
	 * @brief Returns the list attribute
	 * @return ArrayList<Connection>
	 * @details Returns the list attribute
	 */
	public ArrayList<Connection> getList() {
		return list;
	}

	/**
	 * @brief Sets the list attribute
	 * @param list 
	 * @details Sets the list attribute
	 */
	public void setList(ArrayList<Connection> list) {
		this.list = list;
	}


  }
