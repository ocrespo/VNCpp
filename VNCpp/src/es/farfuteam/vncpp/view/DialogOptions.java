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
package es.farfuteam.vncpp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import es.farfuteam.vncpp.controller.ActivityTabs;
import es.farfuteam.vncpp.controller.R;

/**
 * @class DialogOptions
 * @brief This is class which controls the dialogs
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends DialogFragment
 */
public class DialogOptions extends DialogFragment {
	
	/**
	 * @interface SuperListener
	 * @brief Interface to control the manipulation of the list elements
	 * @details Interface to control the manipulation of the list elements
	 */
	  public interface SuperListener{
		     void deleteUser();
		     void editingUser();
	  }

	/**
	 * @brief Set the dialog listener
	 * @param listener
	 * @return DialogOptions
	 * @details Set the dialog listener
	 */	  
	  public static DialogOptions newInstance(SuperListener listener){
		  	DialogOptions f = new DialogOptions();
		    f.setTargetFragment((Fragment) listener, 1234);
			return f;
	  }
	
		
	/**
	 * @brief Create the dialog with options over the connection list
	 * @param savedInstanceState
	 * @return Dialog
	 * @details Create the dialog with options over the connection list
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		//menu de opciones
		final String connect = getString(R.string.DialogConnect);
		final String info =getString(R.string.DialogInfo);
		final String edit = getString(R.string.DialogEdit);
		final String delete = getString(R.string.DialogDelete);
		 
		
		
	    final String[] items = { connect, info, edit, delete };
		 
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    //titulo
	    String options = getString(R.string.DialogTitle);
	    
	    builder.setTitle(options);
	    
	    
	    builder.setItems(items, new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int item) {
	            
	            if (items[item].equalsIgnoreCase(connect)){	
	            	
	            	((ActivityTabs)getActivity()).connect();
	            	
	            }
	            else if (items[item].equalsIgnoreCase(info)){
	   	         
	            	((ActivityTabs)getActivity()).showInfoDialog();
	            	
	            }
	            else if (items[item].equalsIgnoreCase(edit)){
	         
	            	((ActivityTabs)getActivity()).editingUser();
	            	
	            }
	            else if (items[item].equalsIgnoreCase(delete)){	            	
	            	
	            	Fragment parentFragment = getTargetFragment();	            		            	
	            	((SuperListener) parentFragment).deleteUser();            		            	
	            	
	            }
	            else{
	            	//aqui no debería llegar nunca
	            	Toast.makeText(getActivity(), "Invalid option", Toast.LENGTH_SHORT).show();
	            }
	            	            
	            
	        }
	    });
	 
	    return builder.create();
	    
	   
	}
	
	
}
