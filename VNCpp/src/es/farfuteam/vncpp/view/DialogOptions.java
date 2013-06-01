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
package es.farfuteam.vncpp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import es.farfuteam.vncpp.controller.ClientActivityTabs;
import es.farfuteam.vncpp.controller.R;

public class DialogOptions extends DialogFragment {
	
	
	  public interface SuperListener{
		     void deleteUser();
		     void editingUser();
	  }

	  public static DialogOptions newInstance(SuperListener listener){
		  	DialogOptions f = new DialogOptions();
		    f.setTargetFragment((Fragment) listener, 1234);
			return f;
	  }
	
		
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		//menu de opciones
		final String connect = getString(R.string.DialogConnect);
		final String edit = getString(R.string.DialogEdit);
		final String delete = getString(R.string.DialogDelete);
		
		
	    final String[] items = { connect, edit, delete };
		 
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    //titulo
	    String options = getString(R.string.DialogTitle);
	    
	    builder.setTitle(options);
	    
	    
	    builder.setItems(items, new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int item) {
	        	
	            Log.i("Dialogos", "Opción elegida: " + items[item]);
	            
	            if (items[item].equalsIgnoreCase(connect)){
	            	Log.i("Dialogos", "Opción elegida:connect");
	            	Toast.makeText(getActivity(), "Connecting people", Toast.LENGTH_SHORT).show();	
	            	
	            	((ClientActivityTabs)getActivity()).connect();
	            	
	            }	            
	            else if (items[item].equalsIgnoreCase(edit)){
	         
	            	((ClientActivityTabs)getActivity()).edit();
	            	
	            }
	            else if (items[item].equalsIgnoreCase(delete)){	            	
	            	
	            	Fragment parentFragment = getTargetFragment();	            		            	
	            	((SuperListener) parentFragment).deleteUser();            		            	
	            	
	            }
	            else{
	            	Log.i("Dialogos", "Opción elegida INVALIDA");
	            }
	            	            
	            
	        }
	    });
	 
	    return builder.create();
	    
	   
	}
	
	
}
