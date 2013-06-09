package es.farfuteam.vncpp.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;
import es.farfuteam.vncpp.controller.R;

public class ConfigurationMenu extends Activity{
	
	public static final String PREFS_NAME="PreferencesFile";
	
	private boolean rememberExit;
	private boolean hideMouse;
	private SharedPreferences prefs;
	
	private static ConfigurationMenu instance = null;
	
	public static ConfigurationMenu getInstance() {
	      
		if(instance == null) {
	         instance = new ConfigurationMenu();
	    }
	    return instance;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		
		
		View checkBoxOut = View.inflate(this, R.layout.configuration, null);
		ToggleButton checkBox = (ToggleButton) checkBoxOut.findViewById(R.id.checkbox_remember);

		
		ToggleButton checkBoxHide = (ToggleButton) checkBoxOut.findViewById(R.id.checkbox_hidemouse);
		
		final String decision = getString(R.string.RememberCheckBox);
		
		checkBox.setText(decision);
		
		final String hide = getString(R.string.hide_mouse);
		
		checkBoxHide.setText(hide);
		
		
		
		//final String title = getString(R.string.configuration);
		
		
		
		
		
        //accedemos a fichero preferencias
        setPrefs(getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE));

        //false es el valor por defecto si no se encuentra la etiqueta exit
        setRememberExit(getPrefs().getBoolean("exit", false));
        setHideMouse(getPrefs().getBoolean("hidecursor", false));
        
        //configurationDialog();
        
        //finish();
        
		//efectos del actionBar
		final ActionBar actionBar = getActionBar();
        
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
	
	}
	
	
	//menu llamado al haber evento sobre el checkbox de la activity
			public void onToggleClicked(View view) {
		 
				boolean checked = ((ToggleButton) view).isChecked();
			    // Check which checkbox was clicked
			    switch(view.getId()) {
			        case R.id.checkbox_remember:
			            if (checked){
					    	SharedPreferences.Editor editor = prefs.edit();
					    	editor.putBoolean("exit", true);
					    	editor.commit();
					    	setRememberExit(true);
					    	Log.i("tag","chekeadoRemember" + isRememberExit());
			            }
			            else{
					    	SharedPreferences.Editor editor = prefs.edit();
					    	editor.putBoolean("exit", false);
					    	editor.commit();
					    	setRememberExit(false);
					    	Log.i("tag","DESSSchekeadoRemember");
			            }
			            break;
			        case R.id.checkbox_hidemouse:
			            if (checked){
					    	SharedPreferences.Editor editor = prefs.edit();
					    	editor.putBoolean("hidecursor", true);
					    	editor.commit();
					    	setHideMouse(true);
			            }
			            else{
					    	SharedPreferences.Editor editor = prefs.edit();
					    	editor.putBoolean("hidecursor", false);
					    	editor.commit();
					    	setHideMouse(false);
			            }
			            break;

			    }
			}
	
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


	public boolean isRememberExit() {
		return rememberExit;
	}


	public void setRememberExit(boolean rememberExit) {
		this.rememberExit = rememberExit;
	}


	public boolean isHideMouse() {
		return hideMouse;
	}


	public void setHideMouse(boolean hideMouse) {
		this.hideMouse = hideMouse;
	}


	public SharedPreferences getPrefs() {
		return prefs;
	}


	public void setPrefs(SharedPreferences prefs) {
		this.prefs = prefs;
	} 

}
