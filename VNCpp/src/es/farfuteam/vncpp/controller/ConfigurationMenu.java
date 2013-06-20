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
package es.farfuteam.vncpp.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

/**
 * @class ConfigurationMenu
 * @brief This is the activity which control the SharedPreferences file.
 * 
 * This is the detailed description
 * @extends Activity
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 */
public class ConfigurationMenu extends Activity{
	
	public static final String PREFS_NAME="PreferencesFile";
		
	/**
	 * @brief This is the onCreate method
	 * @param savedInstanceState
	 * @details The onCreate method adds two switches to control the remember exit,
	 * and the show of the cursor
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		
	
		Switch checkBox = (Switch) findViewById(R.id.checkbox_remember);
		
		Switch checkBoxHide = (Switch) findViewById(R.id.checkbox_hidemouse);
		
		
		checkBox.setChecked((Configuration.getInstance()).isRememberExit());
		
		checkBoxHide.setChecked((Configuration.getInstance()).isHideMouse());
        
		//efectos del actionBar
		final ActionBar actionBar = getActionBar();
        
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
        
	
	}
	
	/**
	 * @brief This is the onToggleClicked method
	 * @param view
	 * @details The onToggleClicked method is called when the state of the switch has changed
	 */
	//menu llamado al haber evento sobre el switch de la activity
	public void onToggleClicked(View view) {
		 
		boolean checked = ((Switch) view).isChecked();
	    // Check which switch was clicked
	    switch(view.getId()) {
	        case R.id.checkbox_remember:
	            if (checked){			    	
			    	(Configuration.getInstance()).setRememberExit(true);
	            }
	            else{
			    	(Configuration.getInstance()).setRememberExit(false);
	            }
	            break;
	        case R.id.checkbox_hidemouse:
	            if (checked){	
			    	(Configuration.getInstance()).setHideMouse(true);
	            }
	            else{
			    	(Configuration.getInstance()).setHideMouse(false);
	            }
	            break;

	    }
	}
	
	/**
	 * @brief Handles the item selection
	 * @param item
	 * @return always true
	 * @details Handles the item selection. Not ready yet
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Sirve para volver atrás al pulsar en la actionBar
	    switch (item.getItemId()) {
	    	case android.R.id.home:	 
				finish();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


}
