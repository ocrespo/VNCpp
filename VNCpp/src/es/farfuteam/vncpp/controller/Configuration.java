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

import android.content.SharedPreferences;

/**
 * @class Configuration
 * @brief This is the class which control the SharedPreferences file.
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 */
public class Configuration {

	/**Singleton */
	private static Configuration singleton = null;
	/**remember the exit */
	private boolean rememberExit;
	/**hide the cursor of the mouse */
	private boolean hideMouse;
	/**the file with the preferences*/
	private SharedPreferences prefs;
	
	/**
	 * @brief Singleton of Configuration
	 * @return Configuration
	 * @details Creates the instance of Configuration
	 */
	public static Configuration getInstance(){
		if(singleton == null){
			singleton = new Configuration();
		}
		return singleton;
	}
	
	/**
	 * @brief Private constructor of Configuration
	 * @details Private constructor of Configuration to set the configuration
	 * options to false
	 */
	private Configuration(){
		rememberExit = false;
		hideMouse = false;

	}

	/**
	 * @brief Returns the rememberExit attribute
	 * @return rememberExit
	 * @details Returns the rememberExit attribute
	 */
	public boolean isRememberExit() {
		return rememberExit;
	}

	/**
	 * @brief Sets the rememberExit attribute
	 * @param rememberExit 
	 * @details Sets the rememberExit attribute
	 */
	public void setRememberExit(boolean rememberExit) {
		SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean("exit", rememberExit);
    	editor.commit();
		this.rememberExit = rememberExit;
	}
	
	/**
	 * @brief Returns the hideMouse attribute
	 * @return hideMouse
	 * @details Returns the hideMouse attribute
	 */
	public boolean isHideMouse() {
		return hideMouse;
	}

	/**
	 * @brief Sets the hideMouse attribute
	 * @param hideMouse 
	 * @details Sets the hideMouse attribute
	 */
	public void setHideMouse(boolean hideMouse) {
		SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean("hidecursor", hideMouse);
    	editor.commit();
		this.hideMouse = hideMouse;
	}
	
	/**
	 * @brief Returns the prefs attribute
	 * @return prefs
	 * @details Returns the prefs attribute, the SharedPreferences file
	 */
	public SharedPreferences getPrefs() {
		return prefs;
	}

	/**
	 * @brief Sets the SharedPreferences attribute
	 * @param SharedPreferences 
	 * @details Sets the SharedPreferences attribute
	 */
	public void setPrefs(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	/**
	 * @brief Reads the SharedPreferences values
	 * @details Reads the SharedPreferences values
	 */
	public void readPrefs(){
		rememberExit =  prefs.getBoolean("exit", false);
		hideMouse =  prefs.getBoolean("hidecursor", false);
	}
	
}
