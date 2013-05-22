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
package es.farfuteam.vncplusplus.controller;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import es.farfuteam.vncplusplus.model.sql.Connection;
import es.farfuteam.vncplusplus.model.sql.ConnectionSQLite;
import es.farfuteam.vncplusplus.view.Adapter;
import es.farfuteam.vncplusplus.view.DialogOptions.SuperListener;
import es.farfuteam.vncplusplus.controller.R;



/**
 * @class ClientActivityTabs
 * @brief This is the main activity.
 * 
 * This is the detailed description
 *
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends SherlockFragmentActivity
 * @implements SuperListener
 */

public class ClientActivityTabs extends SherlockFragmentActivity implements SuperListener{	
		
	/** A string */
	private static String ACTIVE_TAB = "activeTab";
	
	/** Object */
	private Object o;
	
	private Adapter adapter;
	
	//private Fragment mFragment;
	
	//recuerda si sacar o no la ventana de pregunta de salida,
	//que se guarda en el properties
	private boolean rememberExit; /**< Remember whether or not to show the exit dialog  */
	private SharedPreferences prefs;/**< The preferences from the preferences file */

	/**
	 * @brief This is the onCreate method
	 * @param savedInstanceState
	 * @details The onCreate method adds an actionBar to the activity with two tabs (recent and favorites).
	 * It also load the preferences file into the prefs attribute and sets the rememeberExit attribute.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		
		
		   super.onCreate(savedInstanceState);
		   setContentView(R.layout.tab_host);
		      
	        
	        final ActionBar actionBar = getSupportActionBar();
	        
	        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);
	        
	        
			final String recents = getString(R.string.recents);
			final String favorites = getString(R.string.favoritesTab);
	        
	        // add tabs
	        Tab tab1 = actionBar.newTab()
	                  .setText(recents)
	                  .setTabListener(new TabListener<ListFragmentTab>(
	                   this, "tab1", ListFragmentTab.class));
	        actionBar.addTab(tab1);
	        

	        Tab tab2 = actionBar.newTab()
	               .setText(favorites)
	               .setTabListener(new TabListener<ListFragmentTabFav>(
	                    this, "tab2", ListFragmentTabFav.class));
	        		
	        actionBar.addTab(tab2);
	        
	        
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        
	        //accedemos a fichero preferencias
	        prefs = getSharedPreferences("PreferencesFile",Context.MODE_PRIVATE);
	        
	        //false es el valor por defecto si no se encuentra la etiqueta exit
	        rememberExit = prefs.getBoolean("exit", false);
	        
	        
	        // Orientation Change Occurred
	        if(savedInstanceState!=null){
	            int currentTabIndex = savedInstanceState.getInt("tab_index");
	            actionBar.setSelectedNavigationItem(currentTabIndex);
	        }
	        	        
			       
	}
	
	/**
	 * @brief Adds the new connection item to the top bar
	 * @param menu
	 * @return always true
	 * @details The onCreateOptionsMenu adds the new connection item to the action bar created before on 
	 * the onCreate method
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.new_connection, menu);
		return true;
	}
	
	/**
	 * @brief Handles the item selection
	 * @param item
	 * @return always true
	 * @details Handles the item selection. Not ready yet
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.addConnection:
	 			Intent i= new Intent(this,NewConnectionActivity.class);
				startActivity(i);
	        	return true;
	        	
	        case R.id.configuration:
	        	Log.i("tag","estamos en configuration");
	        	return true;
	        	
	        case R.id.howto:
	        	//TODO funcion texto Luis como usar
	        	return true;
	        	
	        case R.id.about:
	        	//TODO funcion texto Luis acerca de
	        	Log.i("tag","en about");
	        	return true;
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	/**
	 * @brief Saves the active tab
	 * @param outState
	 * @details Saves the active tab
	 */
	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
	    // save active tab
	    outState.putInt(ACTIVE_TAB,
	            getSupportActionBar().getSelectedNavigationIndex());
	    super.onSaveInstanceState(outState);
	  }
	
	  	/**
		 * @brief Deletes a connection from the Data Base 
		 * @details Deletes a connection from the Data Base  
		 */
		public void deleting(){

	        ConnectionSQLite admin = ConnectionSQLite.getInstance(this);
	        admin.deleteUser((Connection) getO());
	        	        	        
	        adapter = new Adapter(this,admin.getAllUsers());
	        
		}

		/**
		 * @brief Creates the edit frame
		 * @details Creates the edit frame 
		 */
		public void edit(){
	        
	        //Ventana de edition
	        Intent intent = new Intent (this,EditionActivity.class);
	        
	        intent.putExtra("Name", ((Connection) getO()).getName());
	        intent.putExtra("IP", ((Connection) getO()).getIP());
	        intent.putExtra("PORT", ((Connection) getO()).getPORT());
	        intent.putExtra("User", ((Connection) getO()).getUserAuth());
	        intent.putExtra("PSW", ((Connection) getO()).getPsw());
	        //el color no me hace falta
	        //1234 es el codigo que servira para su identificacion en onActivityResult
	        startActivityForResult(intent, 1234);	        
			
		}
		
		/**
		 * @brief Saves the information on the Data Base
		 * @param requestCode
		 * @param resultCode
		 * @param data
		 * @details If the resultCode is RESULT_OK then saves the information on the Data Base  
		 */
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {

			  if (requestCode == 1234) {

			     if(resultCode == RESULT_OK){ 
			    	 
			         String ip=data.getStringExtra("newIP");
			         String port=data.getStringExtra("newPORT");
			         String user=data.getStringExtra("newUser");
			         String psw=data.getStringExtra("newPSW");
			         String color=data.getStringExtra("newColor");
			         
			         ConnectionSQLite admin = ConnectionSQLite.getInstance(this);
			         
			        //seteo valores nuevos
			        ((Connection) getO()).setIP(ip);
			        ((Connection) getO()).setPORT(port);
			        ((Connection) getO()).setUserAuth(user);
			        //la pass viene ya encriptada
			        ((Connection) getO()).setPsw(psw);
			        ((Connection) getO()).setFav("false");
			        ((Connection) getO()).setColorFormat(color);
			        
			        //User user = new User(name,ip,port,psw);
			        admin.updateUser((Connection) getO());        
        			        
			         
			     }
			     if (resultCode == RESULT_CANCELED) {    
			         //Write your code on no result return
			    	 Toast.makeText(this, "No connection edition", Toast.LENGTH_SHORT).show();
			     }
			  }
			  
			  
			}//onActivityResult
		
		
		/**
		 * @brief Starts a connection
		 * @param view
		 * @details This method is called from the android:onClic of the element_user.xml.
		 * When it is called calls the connect method which properly makes the connection.
		 */
		//tiene que ser publico porque se llama desde el android:onClic del xml
		public void onCheckboxConnectClicked(View view) {
			
			CheckBox cb = (CheckBox) view;	
			
			//Connection 
			setO(cb.getTag());
						
			connect();

		}
		
		/**
		 * @brief Saves a connection to favorites
		 * @param view
		 * @details Saves a connection to favorites. It's called from the android:onClic of the 
		 * element_user.xml
		 */
		public void onCheckboxFavClicked(View view) {

			CheckBox cb = (CheckBox) view;
			//User 
			setO(cb.getTag());
			
			String fav = ((Connection) getO()).getFav();
			
			if (fav.equalsIgnoreCase("true")){
				((Connection) getO()).setFav("false");
			}
			else{
				((Connection) getO()).setFav("true");
			}
			
			 Toast.makeText(this, "A favoritos", Toast.LENGTH_SHORT).show();
		}

		/**
		 * @brief Returns the o attribute
		 * @return o
		 * @details Returns the o attribute
		 */
		public Object getO() {
			return o;
		}

		/**
		 * @brief Sets the o attribute
		 * @param o 
		 * @details Sets the o attribute
		 */
		public void setO(Object o) {
			this.o = o;
		}

		/**
		 * @brief Calls to the deleting method
		 * @details Calls to the deleting method
		 */
		@Override
		public void deleteUser() {			
			
			deleting();
			
		}

		/**
		 * @brief Calls to the edit method
		 * @details Calls to the edit method
		 */
		@Override
		public void editingUser() {
			edit();
			
		}

		/**
		 * @brief Starts the canvasActivity
		 * @details Starts the canvasActivity 
		 */
		//la llamo desde dialogo, tiene que ser publica
		public void connect() {
				        
	        Intent canvasActivity = new Intent(this, CanvasActivity.class);

	        String ip = ((Connection) getO()).getIP();
	        String port = ((Connection) getO()).getPORT();
	        String user = ((Connection) getO()).getUserAuth();
	        String psw = ((Connection) getO()).getPsw();
	        String color = ((Connection) getO()).getColorFormat();
			
	        canvasActivity.putExtra("ip", ip );
			canvasActivity.putExtra("port", port);
			canvasActivity.putExtra("user", user);
			canvasActivity.putExtra("psw", psw);
			canvasActivity.putExtra("color", color);
			
			startActivity(canvasActivity);
			
			//se finaliza activity
			//finish();
			
		}		
		
		/**
		 * @brief Handles the onKeyDown event
		 * @param keyCode
		 * @param event
		 * @return True if the event is handled properly. If the keyCode is not equal to KEYCODE_BACK 
		 * it returns the event.
		 * @details Only handles the back key. Otherwise it returns the event. When the back key is 
		 * down and the rememberExit is equal to true then finishes the activity. If the rememberExit 
		 * is equal to false
		 */
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
		   
		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  
			//si en las preferencias esta a true, se sale sin preguntar
			if (rememberExit) finish();
			
			else{
				
					View checkBoxOut = View.inflate(this, R.layout.checkbox_out, null);
				CheckBox checkBox = (CheckBox) checkBoxOut.findViewById(R.id.checkboxOut);
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	
				    @Override
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	
				        // Save to shared preferences
				    	SharedPreferences.Editor editor = prefs.edit();
				    	editor.putBoolean("exit", true);
				    	editor.commit();
				    	
				    }
				});
				
				final String decision = getString(R.string.RememberCheckBox);
				
				checkBox.setText(decision);
				
				final String titleExit = getString(R.string.DialogTitleExit);
				final String question = getString(R.string.DialogQuestion);
				
			   
			    new AlertDialog.Builder(this)
			      .setIcon(android.R.drawable.ic_dialog_alert)
			      .setTitle(titleExit)
			      .setMessage(question)
			      .setView(checkBoxOut)
			      .setNegativeButton(android.R.string.cancel, null)//sin listener
			      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
			        @Override
			        public void onClick(DialogInterface dialog, int which){
			          //Salir
			          finish();
			        }
			      })
			      .show();
			    
			}

		    // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
		    return true;
		  }
		  
		  
		//para las demas cosas, se reenvia el evento al listener habitual
		  return super.onKeyDown(keyCode, event);
		
		}//onKeyDown 


			
	
  }

