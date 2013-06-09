package es.farfuteam.vncpp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import es.farfuteam.vncpp.controller.R;

public class ConfigurationMenu2 extends Activity{
	
	public static final String PREFS_NAME="PreferencesFile";
	
	private boolean rememberExit;
	private boolean hideMouse;
	private SharedPreferences prefs;
	
	private static ConfigurationMenu2 instance = null;
	
	public ConfigurationMenu2(){
		//setRememberExit(getPrefs().getBoolean("exit", false));
	}
	
	public static ConfigurationMenu2 getInstance() {
	      
		if(instance == null) {
	         instance = new ConfigurationMenu2();
	    }
	    return instance;
	}
	
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        //accedemos a fichero preferencias
        setPrefs(getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE));

        //false es el valor por defecto si no se encuentra la etiqueta exit
        setRememberExit(getPrefs().getBoolean("exit", false));
        setHideMouse(getPrefs().getBoolean("hidecursor", false));
        Log.i("tag","en oncreate conf");
        
        configurationDialog();
        
        //finish();
	
	}*/
	
	private void init(){
        //false es el valor por defecto si no se encuentra la etiqueta exit
        setRememberExit(getPrefs().getBoolean("exit", false));
        setHideMouse(getPrefs().getBoolean("hidecursor", false));
        //Log.i("tag","en oncreate conf");
	}
	
	
	
	public void configurationDialog() {
		
		//init();
		
		View checkBoxOut = View.inflate(this, R.layout.configuration, null);
		CheckBox checkBox = (CheckBox) checkBoxOut.findViewById(R.id.checkbox_remember);

		
		CheckBox checkBoxHide = (CheckBox) checkBoxOut.findViewById(R.id.checkbox_hidemouse);
		
		final String decision = getString(R.string.RememberCheckBox);
		
		checkBox.setText(decision);
		
		final String hide = getString(R.string.hide_mouse);
		
		checkBoxHide.setText(hide);
		
		
		
		final String title = getString(R.string.configuration);
		//final String question = getString(R.string.DialogQuestion);
		//final String r = getString(R.string.hide_mouse);
	   
	    new AlertDialog.Builder(this)
	      .setIcon(android.R.drawable.ic_dialog_alert)
	      .setTitle(title)
	      //.setMessage(question)
	      .setView(checkBoxOut)
	      //.setView(checkBoxHide)
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
	
	
	
	//menu llamado al haber evento sobre el checkbox de la activity
			public void onCheckboxClicked(View view) {
		 
				boolean checked = ((CheckBox) view).isChecked();
			    // Check which checkbox was clicked
			    switch(view.getId()) {
			        case R.id.checkbox_remember:
			            if (checked){
					    	SharedPreferences.Editor editor = prefs.edit();
					    	editor.putBoolean("exit", true);
					    	editor.commit();
					    	setRememberExit(true);
			            }
			            else{
					    	SharedPreferences.Editor editor = prefs.edit();
					    	editor.putBoolean("exit", false);
					    	editor.commit();
					    	setRememberExit(false);
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
