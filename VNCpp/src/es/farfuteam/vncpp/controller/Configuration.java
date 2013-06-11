/**
 * 
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
