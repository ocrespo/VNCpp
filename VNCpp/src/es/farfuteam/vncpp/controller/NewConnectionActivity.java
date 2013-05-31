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



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import es.farfuteam.vncplusplus.controller.R;
import es.farfuteam.vncpp.model.sql.Connection;
import es.farfuteam.vncpp.model.sql.ConnectionSQLite;


/**
 * @Name        : Server.cpp
 * @author      : Oscar Crespo, Luis Valero, Gorka Jimeno
 * @Version     :
 * @Copyright   : GPLv3
 * @Description : Main class
 *
 */
public class NewConnectionActivity extends SherlockFragmentActivity {
	
	private EditText ConnectionName_field;
	private EditText IP_field;
	private EditText PORT_field;
	private EditText User_field;
	private EditText PSW_field;
	
	private String connectionName;
	private String IP;
	private String PORT;
	private String PSW;
	private String UserAuth;
	private String PSWAuth;
	
	private Spinner Spinner_colors;
	//formato de color seleccionado
	private String color_format;

   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.connection_window);
		
		ConnectionName_field=(EditText)findViewById(R.id.ConnectionName_inserted);
		
		IP_field=(EditText)findViewById(R.id.IP_inserted);
		
		PORT_field = (EditText) findViewById(R.id.PORT_inserted);
		
		User_field = (EditText) findViewById(R.id.User_inserted);
		
		PSW_field = (EditText)findViewById(R.id.PSW_inserted);			
		
		//desplegable seleccion de colores
		
		ArrayAdapter<CharSequence> adapter =
			    ArrayAdapter.createFromResource(this,
			        R.array.color_array,
			        android.R.layout.simple_spinner_item);

		
		Spinner_colors = (Spinner)findViewById(R.id.Spinner_colors);
		 
		adapter.setDropDownViewResource(
		        android.R.layout.simple_spinner_dropdown_item);
		 
		Spinner_colors.setAdapter(adapter);
		
		final String[] colors = getResources().getStringArray(R.array.color_array);
		
		
		Spinner_colors.setOnItemSelectedListener(
		        new AdapterView.OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent,
		            android.view.View v, int position, long id) {
		                
		                setColor_format(colors[position]);
		        }
		 
		        public void onNothingSelected(AdapterView<?> parent) {
		        	//por defecto se selecciona la posicion 0, 24-bit color
		        	setColor_format(colors[0]);
		        }
		});
		
		
		
		Button botonConnect = (Button) findViewById(R.id.buttonConnect);
		botonConnect.setOnClickListener(new OnClickListener() {

	         @Override
	         public void onClick(View v) {
	        	 
	        	 
	        	//primero se verifica que los campos se rellenan bien,
	        	//y se añade a la BD antes de iniciar Canvas. 
				
	        	if  (verify(v)){	
	        		
	       		 	//crear usuario si todo ha ido bien
	        		createNewConnection();

	        		iniCanvasActivity();

	        	}

	         }
		});
		
		
		Button botonCancel = (Button) findViewById(R.id.buttonCancel);
		botonCancel.setOnClickListener(new OnClickListener() {

	         @Override
	         public void onClick(View v) {

	        	 Cancel(v);

	         }
		});
		
		//efectos del actionBar
		final ActionBar actionBar = getSupportActionBar();
        
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
        
	}
	
	
	private void iniCanvasActivity(){
		
		Intent canvasActivity = new Intent(this, CanvasActivity.class);
		
		canvasActivity.putExtra("ip", getIP());
		canvasActivity.putExtra("port", getPORT());
		canvasActivity.putExtra("user", getUserAuth());
		canvasActivity.putExtra("psw", getPSWAuth());
		canvasActivity.putExtra("color", getColor_format());
		
		startActivity(canvasActivity);
		
		//se finaliza activity
		finish();
	}
	
	private void Cancel(View v){	        
	        finish();
	}
	
    
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Sirve para volver a Tabs al pulsar en la actionBar
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	 			Intent intent= new Intent(this,ClientActivityTabs.class);			
				startActivity(intent);	    		
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


	/**
	 * Aqui se verifican los datos introducidos por el usuario
	 * @param v
	 */
	private boolean verify(View v) {		
		
		//Comprobar validez IP
		IP = IP_field.getText().toString();
		
		
		if (!validateIPAddress(IP)){
			final String invalidIp = getString(R.string.invalidIp);
			Toast.makeText(this, invalidIp, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//Comprobar validez Puerto
		PORT = PORT_field.getText().toString();

		if (!validPort(PORT)){
			final String invalidPort = getString(R.string.invalidPort);
			Toast.makeText(this, invalidPort, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//Comprobar nombre de conexion no repetido
		connectionName = ConnectionName_field.getText().toString();

		if(!validNameConnection(connectionName)){
			final String invalidName = getString(R.string.invalidName);
			Toast.makeText(this, invalidName, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//Si campo usuario o contraseña ..., ambos tienen que estar escritos
		
		setUserAuth(User_field.getText().toString());
		
		setPSWAuth(PSW_field.getText().toString());
		Log.i("tag",getPSWAuth());
		
		if (getUserAuth() == null || getUserAuth().isEmpty()){
			if (!getPSWAuth().isEmpty()){
				final String invalidUserOrPass = getString(R.string.invalidUserOrPass);
				Toast.makeText(this, invalidUserOrPass, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		else{
			if (!getPSWAuth().isEmpty()){
				final String invalidUserOrPass = getString(R.string.invalidUserOrPass);
				Toast.makeText(this, invalidUserOrPass, Toast.LENGTH_SHORT).show();
				return false;
			}

		}
		
		return true;

	}
	
	private boolean validNameConnection(String name){
		
		//se mira en la base de datos que no exista
		ConnectionSQLite dataBase = ConnectionSQLite.getInstance(this);
		if (dataBase.searchNameConnection(name)){
			return false;
		}
		else{		
			return true;
		}
		
	}
	
	private boolean validPort(String port){
		
		try {
			int p = Integer.parseInt(port);
			//rango de puertos no aceptados
			if ((p<0) || (p>65535)){
				return false;
			}
		}
        catch ( NumberFormatException s ) {
            return false;
        }
		return true;		
	}
	
	private boolean validateIPAddress( String ipAddress ) {
		
		String[] tokens = ipAddress.split("\\.");
		
		if (tokens.length != 4) {
			return false;
		}
		
		for (String str : tokens) {
			
			int i;
			
			try {
				i = Integer.parseInt(str);
			}
            catch ( NumberFormatException s ) {
                return false;
            }
			
			if ((i < 0) || (i > 255)) {
				return false;
			}
			
		}
		
		return true;
	}
	
	
	
	private void createNewConnection(){
		
		if (!isEmpty(ConnectionName_field) && !isEmpty(IP_field) && !isEmpty(PORT_field)){
			
	      		
	        connectionName = ConnectionName_field.getText().toString();
	        IP = IP_field.getText().toString();
	        PORT = PORT_field.getText().toString();
	        setUserAuth(User_field.getText().toString());
	        PSW = PSW_field.getText().toString();
	        
	        //se encripta password
	        String md5 = md5(PSW);
	        
	        Connection c = new Connection(connectionName, IP, PORT, getUserAuth(), md5, "false", getColor_format());
	        
	        //se anade el usuario a la base de datos
	        ConnectionSQLite dataBase = ConnectionSQLite.getInstance(this);
	        dataBase.newUser(c);
	        	        
	        //a formar canvas
	        iniCanvasActivity();

		}
		else{
			//Dialogo alerta
			showDialog(1);
		}
	}
	
	
	
	private static String md5(String s) { try {
        
        // Create MD5 Hash
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();
 
         // Create Hex String
         StringBuffer hexString = new StringBuffer();
         for (int i=0; i<messageDigest.length; i++)
             hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        return hexString.toString();
 
     } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
     }
     return "";
 
    }	
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	 
	    switch(id)
	    {
	        case 1:
	            dialog = createAlertDialog();
	            break;
	        default:
	            dialog = createAlertDialog();
	            break;
	    }
	 
	    return dialog;
	}
	
	
	private Dialog createAlertDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.DialogInfo);
		String someEmpty = getString(R.string.DialogSomethingEmpty);
		 
	    builder.setTitle(info);
	    builder.setMessage(someEmpty);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	        }

	    });
	 
	    return builder.create();
	}
	
	
	private boolean isEmpty(EditText etText) {
	    if (etText.getText().toString().trim().length() > 0) {
	        return false;
	    } else {
	        return true;
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   
	  if (keyCode == KeyEvent.KEYCODE_BACK) {
					
			final String titleExit = getString(R.string.DialogTitleExit);
			final String question = getString(R.string.DialogQuestion);			
		   
		    new AlertDialog.Builder(this)
		      .setIcon(android.R.drawable.ic_dialog_alert)
		      .setTitle(titleExit)
		      .setMessage(question)
		      .setNegativeButton(android.R.string.cancel, null)//sin listener
		      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
		        @Override
		        public void onClick(DialogInterface dialog, int which){
		          //Salir
		          finish();
		        }
		      })
		      .show();
		    
		    return true;
		    
		}	  
	  
	//para las demas cosas, se reenvia el evento al listener habitual
	  return super.onKeyDown(keyCode, event);
	
	} 


	public String getConnectionName() {
		return connectionName;
	}


	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}


	public String getIP() {
		return IP;
	}


	public void setIP(String iP) {
		IP = iP;
	}


	public String getPSW() {
		return PSW;
	}


	public void setPSW(String pSW) {
		PSW = pSW;
	}

	public String getPORT() {
		return PORT;
	}

	public void setPORT(String pORT) {
		PORT = pORT;
	}

	public String getColor_format() {
		return color_format;
	}

	public void setColor_format(String color_format) {
		this.color_format = color_format;
	}

	public String getUserAuth() {
		return UserAuth;
	}

	public void setUserAuth(String userAuth) {
		UserAuth = userAuth;
	}

	public String getPSWAuth() {
		return PSWAuth;
	}

	public void setPSWAuth(String pSWAuth) {
		PSWAuth = pSWAuth;
	}
	

}
