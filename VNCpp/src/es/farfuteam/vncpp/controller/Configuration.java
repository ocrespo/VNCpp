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
 * @author roni
 *
 */
public class Configuration {

	private static Configuration singleton = null;
	
	private boolean rememberExit;
	private boolean hideMouse;
	private SharedPreferences prefs;
	
	public static Configuration getInstance(){
		if(singleton == null){
			singleton = new Configuration();
		}
		return singleton;
	}
	private Configuration(){
		rememberExit = false;
		hideMouse = false;

	}
	/**
	 * @return the rememberExit
	 */
	public boolean isRememberExit() {
		return rememberExit;
	}
	/**
	 * @param rememberExit the rememberExit to set
	 */
	public void setRememberExit(boolean rememberExit) {
		SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean("exit", rememberExit);
    	editor.commit();
		this.rememberExit = rememberExit;
	}
	/**
	 * @return the hideMouse
	 */
	public boolean isHideMouse() {
		return hideMouse;
	}
	/**
	 * @param hideMouse the hideMouse to set
	 */
	public void setHideMouse(boolean hideMouse) {
		SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean("hidecursor", hideMouse);
    	editor.commit();
		this.hideMouse = hideMouse;
	}
	/**
	 * @return the prefs
	 */
	public SharedPreferences getPrefs() {
		return prefs;
	}
	/**
	 * @param prefs the prefs to set
	 */
	public void setPrefs(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	public void readPrefs(){
		rememberExit =  prefs.getBoolean("exit", false);
		hideMouse =  prefs.getBoolean("hidecursor", false);
	}
	
}
