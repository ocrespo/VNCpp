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
package es.farfuteam.vncpp.controller;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import es.farfuteam.vncpp.controller.R;
import es.farfuteam.vncpp.model.sql.Connection;
import es.farfuteam.vncpp.model.sql.ConnectionSQLite;
import es.farfuteam.vncpp.view.Adapter;
import es.farfuteam.vncpp.view.DialogOptions;
import es.farfuteam.vncpp.view.DialogOptions.SuperListener;
/**
 * @Name        : ListFragmentTabFav.java
 * @author      : Oscar Crespo, Luis Valero, Gorka Jimeno
 * @Version     :
 * @Copyright   : GPLv3
 * @Description : Class who control the ListFragment of favorites users
 *
 */
public class ListFragmentTabFav extends ListFragment implements SuperListener{
	
	//variable donde se guarda el Objeto User al pulsarlo en la lista
	private static Object o;
	
	private static Adapter adapter;
	private ArrayList<Connection> userList;
	private static ConnectionSQLite admin;
	private View view;
	
	   
	public ListFragmentTabFav() {

	}

	
	@Override
	  public View onCreateView(LayoutInflater inflater, 
	                 ViewGroup container, Bundle savedInstanceState) {
		
		setUserList(new ArrayList<Connection>());
	    // Inflate the layout for this fragment
	    view = inflater.inflate(R.layout.list_users, container, false);

	    // Se vincula Adaptador
        admin = new ConnectionSQLite(this.getActivity());
        admin.getWritableDatabase();
        adapter = new Adapter(this.getActivity(),admin.getAllFavUsers());
        setListAdapter(adapter);
	   
	    
	    return view;
	  }
	
	/**
	 * 
	 */
	@Override
	public void onStart() {
		
	    super.onStart();       	    
	    admin = ConnectionSQLite.getInstance(getActivity());
	    userList = admin.getAllFavUsers();
    	adapter = new Adapter(this.getActivity(),admin.getAllFavUsers());
        adapter.setList(admin.getAllFavUsers());
        setListAdapter(adapter);        	
      
	    
	}
	    
	  
	    @Override
		public void onListItemClick(ListView listView, View view, int position, long id) {	    	

			super.onListItemClick(listView, view, position, id);			
			setO(getListAdapter().getItem(position));			
			
			((ActivityTabs)getActivity()).setO(getO());
			
			//SuperListener parentFragment;
			DialogOptions dialog1 = DialogOptions.newInstance((SuperListener) this);
			dialog1.show(getFragmentManager(),"dialog");
		
		}
	    

		@Override
		public void deleteUser() {
			
	        admin.getWritableDatabase();
	        admin.deleteUser((Connection) getO());
	        adapter.setList(admin.getAllFavUsers());
	        setListAdapter(adapter);
			
		}
		

		@Override
		public void editingUser() {
			
			ConnectionSQLite admin = new ConnectionSQLite(this.getActivity());
	        admin.getWritableDatabase();
		
	        Intent intent = new Intent (this.getActivity(),EditionActivity.class);
	        
	        intent.putExtra("Name", ((Connection) getO()).getName());
	        intent.putExtra("IP", ((Connection) getO()).getIP());
	        intent.putExtra("PORT", ((Connection) getO()).getPORT());
	        intent.putExtra("PSW", ((Connection) getO()).getPsw());

	        startActivity(intent);
	        
		}

		/**
		 * Return the Object User down at the list
		 * \return
		 */
		public static Object getO() {
			return o;
		}
		
		/**
		 * Set the User 
		 * \param o
		 */
		public static void setO(Object o) {
			ListFragmentTabFav.o = o;
		}
		/**
		 * 
		 * @return
		 */
		public ArrayList<Connection> getUserList() {
			return userList;
		}

		public void setUserList(ArrayList<Connection> userList) {
			this.userList = userList;
		}
	
	    
	  	  

}
