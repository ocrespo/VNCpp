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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import es.farfuteam.vncpp.model.sql.Connection;
import es.farfuteam.vncpp.model.sql.ConnectionSQLite;


/**
 * @class NewConnectionActivity
 * @brief This is the activity created to make a new connection
 * @extends FragmentActivity
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 */
public class NewConnectionActivity extends FragmentActivity {
	
	/**
	 * @enum QualityArray
	 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero 
	 * @details Controls the image quality
	 */
	public enum QualityArray{SuperHigh,High,Medium,Low};
	
	/** EditText to write the name connection*/
	private EditText ConnectionName_field;
	/** EditText to write the ip connection*/
	private EditText IP_field;
	/** EditText to write the port connection*/
	private EditText PORT_field;
	/** EditText to write the password connection*/
	private EditText PSW_field;
	
	/**connection name */
	private String connectionName;
	/**connection ip */
	private String IP;
	/**connection port */
	private String PORT;
	/**connection psw */
	private String PSW;
	/**connection pswAuth */
	private String PSWAuth;
	/**spinners with the quality image */
	private Spinner Spinner_colors;
	/**image quality selected */
	private QualityArray color_format;	
	

	/**
	 * @brief This is the onCreate method
	 * @param savedInstanceState
	 * @details The onCreate method adds buttons and edittext on the activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.connection_window);
		
		ConnectionName_field=(EditText)findViewById(R.id.ConnectionName_inserted);
		
		IP_field=(EditText)findViewById(R.id.IP_inserted);
		
		PORT_field = (EditText) findViewById(R.id.PORT_inserted);
		
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
		
		Spinner_colors.setOnItemSelectedListener(
		        new AdapterView.OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent,
		            android.view.View v, int position, long id) {
		                setColor_format(getPosEnumQuality(position));
		        }
		 
		        public void onNothingSelected(AdapterView<?> parent) {
		        	//por defecto se selecciona la posicion 0, 24-bit color(extra-high)
		        	setColor_format(QualityArray.SuperHigh);
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
		final ActionBar actionBar = getActionBar();
        
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
        
	}
	
	/**
	 * @brief Returns the image quality 
	 * @param pos position on the array
	 * @return the image quality
	 * @details Returns the image quality of th enum
	 */
	private QualityArray getPosEnumQuality(int pos){
		
		switch (pos) {
		
			case 0:
				return QualityArray.SuperHigh;
			case 1:
				return QualityArray.High;
			case 2:
				return QualityArray.Medium;
			case 3:
				return QualityArray.Low;
			default:
				break;
		
		}
		return null;
	}
	
	/**
	 * @brief Method that initialized the Canvas Activity
	 * @details Method that initialized the Canvas Activity
	 */
	private void iniCanvasActivity(){
		
		Intent canvasActivity = new Intent(this, CanvasActivity.class);
		
		canvasActivity.putExtra("ip", getIP());
		canvasActivity.putExtra("port", getPORT());
		canvasActivity.putExtra("psw", getPSWAuth());
		canvasActivity.putExtra("color", getColor_format().toString());
		
		//Aquí veo el tipo de conexión, para usar un tipo de compresión de imagen u otro
		if (checkConnectivity()){
				
				canvasActivity.putExtra("wifi", isWifiConnectivityType());
				
				startActivity(canvasActivity);
				
				//se finaliza activity
				finish();

		}
		else{
			//dialogo alerta No conexion habilitada
			showDialog(2);
		}		
		
		

	}
	
	/**
	 * @brief Cancels the creation of new connection
	 * @param v the view
	 * @details Cancels the creation of new connection
	 */
	private void Cancel(View v){	        
	        finish();
	}
	
	/**
	 * @brief Checks the connectivity of the terminal
	 * @return True if the connectivity exists, false in another case.
	 * @details Checks the connectivity of the terminal
	 */
	private boolean checkConnectivity()
    {
        boolean enabled = true;
 
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
         
        if ((info == null || !info.isConnected() || !info.isAvailable()))
        {
            enabled = false;
        }
        return enabled;        
    }
	
	/**
	 * @brief Checks the connectivity type of the terminal
	 * @return True if the connectivity type is Wifi, false in another case.
	 * @details Checks the connectivity type of the terminal
	 */
	//devuelve true si es conexion wifi, false en caso contrario
	private boolean isWifiConnectivityType(){
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        
        String connectionType = info.getTypeName();
        
        if (connectionType.equalsIgnoreCase("wifi")){
        	return true;
        }
        else{
        	//3g u otro tipo
        	return false;
        }
        	
	}
	
    
    /**
     * @brief Preserved the state of the activity
     * @param newConfig the new state
     * @details Preserved the state of the activity when the terminal changed the orientation
     */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * @brief Handles the item selection
	 * @param item
	 * @return always true
	 * @details Handles the item selection. Not ready yet
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Sirve para volver a Tabs al pulsar en la actionBar
	    switch (item.getItemId()) {
	    	case android.R.id.home:	 
				finish();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


	/**
	 * @brief Verifies the data introduced by the user
	 * @param v the view
	 * @details Verifies the data introduced by the user
	 */
	private boolean verify(View v) {		
		
		//Comprobar validez IP
		IP = IP_field.getText().toString();
		
		
		if (IP.equals("") || !validateIPAddress(IP)){
			final String invalidIp = getString(R.string.invalidIp);
			Toast.makeText(this, invalidIp, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//Comprobar validez Puerto
		PORT = PORT_field.getText().toString();

		if (PORT.equals("") || !validPort(PORT)){
			final String invalidPort = getString(R.string.invalidPort);
			Toast.makeText(this, invalidPort, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//Comprobar nombre de conexion no repetido
		connectionName = ConnectionName_field.getText().toString();

		if(connectionName.equals("") || !validNameConnection(connectionName)){
			final String invalidName = getString(R.string.invalidName);
			Toast.makeText(this, invalidName, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		setPSWAuth(PSW_field.getText().toString());
				
		return true;

	}
	
	/**
	 * @brief Valids the connection name
	 * @param name the name of the connection
	 * @return true if is a valid name, false if this name is in use
	 * @details Returns true if this name it is not in use
	 */
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
	
	/**
	 * @brief This function checks the port parameter
	 * @return true if it is a valid Port, false in another case 
	 * @details This function checks the port parameter
	 */
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
	
	/**
	 * @brief This function checks the ip format
	 * @param ipAddress the address
	 * @return true if it is a valid IP, false in another case
	 * @details This function checks the ip format
	 */
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
	
	
	/**
	 * @brief Creates and connects to the new connection
	 * @details Creates and connects to the new connection
	 */
	private void createNewConnection(){
		
		if (!isEmpty(ConnectionName_field) && !isEmpty(IP_field) && !isEmpty(PORT_field)){
			
	      		
	        connectionName = ConnectionName_field.getText().toString();
	        IP = IP_field.getText().toString();
	        PORT = PORT_field.getText().toString();
	        PSW = PSW_field.getText().toString();
	        
	        Connection c = new Connection(connectionName, IP, PORT, PSW, false, getColor_format());
	        
	        //se anade el usuario a la base de datos
	        ConnectionSQLite dataBase = ConnectionSQLite.getInstance(this);
	        dataBase.newUser(c);

		}
		else{
			//Dialogo alerta
			showDialog(1);
		}
	}
		
	/**
	 * @brief Override function to create dialogs
	 * @param id
	 * @return Dialog created
	 * @details Creates the dialog with a showDialog(id) called,
	 * id is the number of the dialog to be created
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	 
	    switch(id)
	    {
	        case 1:
	            dialog = createAlertDialog();
	            break;
	        case 2:
	        	dialog = createNonConnectionDialog();
	        	break;
	        default:
	            dialog = createAlertDialog();
	            break;
	    }
	 
	    return dialog;
	}
	
	/**
	 * @brief Shows the dialog when any field is empty
	 * @return The new dialog
	 * @details Shows the dialog when any field is empty
	 */
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
	
	/**
	 * @brief Shows the dialog when the connection is not available
	 * @return The new dialog
	 * @details Shows the dialog when the connection is not available
	 */
	private Dialog createNonConnectionDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.DialogNonConnectionInfo);
		String someEmpty = getString(R.string.DialogNonConnection);
		 
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
	
	/**
	 * @brief Controls if the EditText is empty
	 * @return False if the EditText is empty,true in another case
	 * @details Controls if the EditText is empty
	 */
	private boolean isEmpty(EditText etText) {
	    if (etText.getText().toString().trim().length() > 0) {
	        return false;
	    } else {
	        return true;
	    }
	}
	
	/**
	 * @brief Handles the onKeyDown event
	 * @param keyCode
	 * @param event
	 * @return True if the event is handled properly. If the keyCode is not equal to KEYCODE_BACK 
	 * it returns the event
	 * @details Only handles the back key. Otherwise it returns the event
	 */
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

	/**
	 * @brief Returns the connectionName attribute
	 * @return connectionName the connection name
	 * @details Returns the connectionName attribute
	 */
	public String getConnectionName() {
		return connectionName;
	}

	/**
	 * @brief Sets the connectionName attribute
	 * @param connectionName the connection name
	 * @details Sets the connectionName attribute
	 */
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	/**
	 * @brief Returns the IP attribute
	 * @return IP
	 * @details Returns the IP attribute
	 */
	public String getIP() {
		return IP;
	}

	/**
	 * @brief Sets the IP attribute
	 * @param iP
	 * @details Sets the IP attribute
	 */
	public void setIP(String iP) {
		IP = iP;
	}

	/**
	 * @brief Returns the PSW attribute
	 * @return PSW the password atributte
	 * @details Returns the PSW attribute
	 */
	public String getPSW() {
		return PSW;
	}

	/**
	 * @brief Sets the PSW attribute
	 * @param pSW the password of the connection
	 * @details Sets the PSW attribute
	 */
	public void setPSW(String pSW) {
		PSW = pSW;
	}

	/**
	 * @brief Returns the PORT attribute
	 * @return PORT
	 * @details Returns the PORT attribute
	 */
	public String getPORT() {
		return PORT;
	}

	/**
	 * @brief Sets the PSW attribute
	 * @param pSW the password of the connection
	 * @details Sets the PSW attribute
	 */
	public void setPORT(String pORT) {
		PORT = pORT;
	}

	/**
	 * @brief Returns the color_format attribute
	 * @return color_format
	 * @details Returns the color_format attribute
	 */
	public QualityArray getColor_format() {
		return color_format;
	}

	/**
	 * @brief Sets the color_format attribute
	 * @param color_format the image quality of the connection
	 * @details Sets the color_format attribute
	 */
	public void setColor_format(QualityArray color_format) {
		this.color_format = color_format;
	}

	/**
	 * @brief Returns the PSWAuth attribute
	 * @return PSWAuth
	 * @details Returns the PSWAuth attribute
	 */
	public String getPSWAuth() {
		return PSWAuth;
	}

	/**
	 * @brief Sets the PSWAuth attribute
	 * @param pSWAuth the password authentication of the connection
	 * @details Sets the PSWAuth attribute
	 */
	public void setPSWAuth(String pSWAuth) {
		PSWAuth = pSWAuth;
	}
	

}
