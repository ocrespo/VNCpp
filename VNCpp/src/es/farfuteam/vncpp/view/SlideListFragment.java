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



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import es.farfuteam.vncpp.controller.CanvasActivity;
import es.farfuteam.vncpp.controller.CanvasActivity.EnumDialogs;
import es.farfuteam.vncpp.controller.R;

/**
 * @class SlideListFragment
 * @brief This is class which controls the lateral menu in Canvas
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends ListFragment
 */
public class SlideListFragment extends ListFragment {
	
	/**
	 * @brief Method that creates the view
	 * @param inflater the inflater layout
	 * @param container The container
	 * @param savedInstanceState
	 * @details Method that creates the view
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		return inflater.inflate(R.layout.list_slide, null);
	}

	/**
	 * @brief Override method called on activity created
	 * @param savedInstanceState
	 * @details Override method called on activity created which form the lateral menu
	 */
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MenuSlideAdapter adapter = new MenuSlideAdapter(getActivity());
		
		//textos
		final String keyboard = getString(R.string.Keyboard);
		final String comboKeys = getString(R.string.combo_keys_title);
		final String centerImage = getString(R.string.center_image);
		final String help = getString(R.string.help);
		final String disconnect = getString(R.string.disconnect);

			adapter.add(new SlideMenuItem(keyboard, R.drawable.keyboard_image));
			adapter.add(new SlideMenuItem(comboKeys, R.drawable.ctrl_image));
			adapter.add(new SlideMenuItem(centerImage, R.drawable.image_center));
			adapter.add(new SlideMenuItem(help, R.drawable.helpsymbol));
			adapter.add(new SlideMenuItem(disconnect, R.drawable.disconnect_image));

		setListAdapter(adapter);
	}

	/**
	 * @class SlideMenuItem
	 * @brief This is class which controls the items of menu in Canvas
	 * 
	 * This is the detailed description
	 *
	 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
	 */
	private class SlideMenuItem {
		public String tag;
		public int iconRes;
		
		/**
		 * @brief Constructor of SlideMenuItem
		 * @param tag
		 * @param iconRes
		 * @details Constructor of SlideMenuItem
		 */
		public SlideMenuItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	/**
	 * @class MenuSlideAdapter
	 * @brief This is class with the Adapter of the lateral menu
	 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
	 * @extends ArrayAdapter<SlideMenuItem>
	 */
	public class MenuSlideAdapter extends ArrayAdapter<SlideMenuItem> {

		/**
		 * @brief Constructor of MenuSlideAdapter
		 * @param context The context
		 * @details Constructor of MenuSlideAdapter
		 */
		public MenuSlideAdapter(Context context) {
			super(context, 0);
		}

		/**
		 * @brief Returns the view clicked in the menu
		 * @param position The position on the list
		 * @param convertView The view
		 * @param parent The view group
		 * @return view The view
		 * @details Returns the view clicked in the menu
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_slide, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
						

			return convertView;
		}

	}
	
	
	/**
	 * @brief Method called when the user clicks on an item of the lateral menu
	 * @param listView The list view
	 * @param view The view
	 * @param position The position list
	 * @param id The identification
	 * @details Method called when the user clicks on an item of the lateral menu
	 */ 
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
    	

		super.onListItemClick(listView, view, position, id);
		
		switch (position) {
		case 0:
			//showKeyboard
			((CanvasActivity)getActivity()).showKeyboard();
			break;
		case 1:
			//comboKeys
			((CanvasActivity)getActivity()).showDialog(EnumDialogs.comboEventsDialog.ordinal());
			break;
		case 2:
			//centrar imagen
			((CanvasActivity)getActivity()).centerImageCanvas();
			break;
		case 3:
			//help
			((CanvasActivity)getActivity()).showDialog(EnumDialogs.openHelpDialog.ordinal());
			break;
		case 4:
			//exit?
			((CanvasActivity)getActivity()).showDialog(EnumDialogs.exitDialog.ordinal());
			break;
			
		}
	
	}
	
	
}
