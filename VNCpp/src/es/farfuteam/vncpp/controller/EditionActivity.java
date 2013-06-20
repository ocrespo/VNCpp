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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import es.farfuteam.vncpp.controller.NewConnectionActivity.QualityArray;

/**
 * @class EditionActivity
 * @brief This is the activity created to managed the connection edition
 * @extends Activity
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 */
public class EditionActivity extends Activity{

	/** EditText to write the ip connection*/
	private EditText IP_field;
	/** EditText to write the port connection*/
	private EditText PORT_field;
	/** EditText to write the password connection*/
	private EditText PSW_field;
	
	/**ip connection*/
	private String IP;
	/**port connection*/
	private String PORT;
	/**password connection*/
	private String PSW;
	/**spinner with the image quality*/
	private Spinner Spinner_colors;
	/**image quality*/
	private int color_format;
	/**fav connection*/
	private boolean fav;

	/**
	 * @brief This is the onCreate method
	 * @param savedInstanceState
	 * @details The onCreate method adds the buttons and spinner at the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edition);
		
		IP_field=(EditText)findViewById(R.id.editIP_inserted);
		
		PORT_field = (EditText) findViewById(R.id.editPORT_inserted);
		
		PSW_field = (EditText) findViewById(R.id.editPSW_inserted);
		
		Bundle bundle=getIntent().getExtras();
		String name = bundle.getString("Name");
	      String ip = bundle.getString("IP");	      
	      String port = bundle.getString("PORT");
	      String psw = bundle.getString("PSW");
	      String color = bundle.getString("quality");
	      fav = bundle.getBoolean("fav");
	      
	      final int pos = QualityArray.valueOf(color).ordinal();


		TextView text = (TextView) findViewById(R.id.editTextViewName);
		
		final String modify = getString(R.string.modify_connectiontext);
		
		text.setText(modify +" "+name);
		
		//Muestro datos anteriores a la ediccion
		IP_field.setText(ip);
		PORT_field.setText(port);
		if (!psw.equals("")){
			PSW_field.setText("****");
		}
		else{
			PSW_field.setText("");
		}
		
		//desplegable seleccion de colores
		
				ArrayAdapter<CharSequence> adapter =
					    ArrayAdapter.createFromResource(this,
					        R.array.color_array,
					        android.R.layout.simple_spinner_item);

				
				Spinner_colors = (Spinner)findViewById(R.id.Spinner_colors);
				 
				adapter.setDropDownViewResource(
				        android.R.layout.simple_spinner_dropdown_item);
				 
				Spinner_colors.setAdapter(adapter);
				
				//final String[] colors = getResources().getStringArray(R.array.color_array);
				
				//seteo conexion anterior
				Spinner_colors.setSelection(pos);
				
				Spinner_colors.setOnItemSelectedListener(
				        new AdapterView.OnItemSelectedListener() {
				        public void onItemSelected(AdapterView<?> parent,
				            android.view.View v, int position, long id) {
				                
				                color_format = position;
				        }
				 
				        public void onNothingSelected(AdapterView<?> parent) {
				        	//por defecto se selecciona la posicion que indica lo elegido anteriormente
				        	color_format = pos;
				        }
				});
		
		//boton editar y evento
		Button botonEdit = (Button) findViewById(R.id.buttonEdit);
		botonEdit.setOnClickListener(new OnClickListener() {
	
	         @Override
	         public void onClick(View v) {
	        	 
	        	 if (verify()){
	        	 
		        	 Intent returnIntent = new Intent();
		        	 returnIntent.putExtra("newIP",IP_field.getText().toString());
		        	 returnIntent.putExtra("newPORT",PORT_field.getText().toString());
		        	 String psw = PSW_field.getText().toString();
		        	 returnIntent.putExtra("newPSW",psw);
		        	 returnIntent.putExtra("newColor", color_format);
		        	 Log.i("tag",String.valueOf(color_format));
		        	 returnIntent.putExtra("fav", fav);
		        	 setResult(RESULT_OK,returnIntent);     
		        	 finish();
	        	 }
	        	 	
	         }
		});
		
		Button botonCancel = (Button) findViewById(R.id.buttonCancelEdit);
		botonCancel.setOnClickListener(new OnClickListener() {
	
	         @Override
	         public void onClick(View v) {
	        	 
	        	 finish();
	        	 	
	         }
		});
		
		//efectos del actionBar
		final ActionBar actionBar = getActionBar();
        
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
		
		
	}
	
	/**
	 * @brief This function check the ip format and the port parameter
	 * @return true if it is a valid IP and valid Port.
	 * @details This function check the ip format and the port parameter
	 */
	private boolean verify(){
		
		//Comprobar validez IP
		IP = IP_field.getText().toString();
				
				
		if (!validateIPAddress(IP)){
			final String invalidIp = getString(R.string.invalidIp);
			Toast.makeText(this, invalidIp, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		PORT = PORT_field.getText().toString();
		
		if (!validPort(PORT)){
			final String invalidPort = getString(R.string.invalidPort);
			Toast.makeText(this, invalidPort, Toast.LENGTH_SHORT).show();
			return false;
		}		
		
		return true;
		
	}
	
	/**
	 * @brief This function check the port parameter
	 * @return true if it is a valid Port, false in another case 
	 * @details This function check the port parameter
	 */
	private boolean validPort(String port){
		
		try {
			int p = Integer.parseInt(port);
			//rango de puertos no aceptados
			if ((p<0) || (p>65535)){
				return false;
			}
			else{
				return true;
			}
		}
        catch ( NumberFormatException s ) {
            return false;
        }
		
	}
	
	/**
	 * @brief This function check the ip format
	 * @param ipAddress the address
	 * @return true if it's a valid IP, false in another case
	 * @details This function check the ip format
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
	          //Salir
	          finish();

	    // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
	    return true;
	  }
	//para las demas cosas, se reenvia el evento al listener habitual
	  return super.onKeyDown(keyCode, event);
	} 

	/**
	 * @brief Handles the item selected event
	 * @param item
	 * @return True if the event is handled properly
	 * @details Handles the item selected event, in this case, the event of the actionBar to return to the last activity
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
