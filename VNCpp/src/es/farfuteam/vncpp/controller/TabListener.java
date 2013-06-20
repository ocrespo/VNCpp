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


import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;


import android.support.v4.app.FragmentActivity;



/**
 * @class TabListener<T>
 * @brief This is the TabListener
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends ListFragment
 * @implements TabListener
 */
public class TabListener<T extends ListFragment> implements android.app.ActionBar.TabListener{
	
	/** fragment of the activity*/
	  private Fragment mFragment;
	  /**fragment activity to control the tabs */
	  private final FragmentActivity mActivity;
	  /**tag to find the tab */
	  private final String mTag;
	  /**the class of the listener */
	  private final Class<T> mClass;
	  /**transaction between tabs */
	  private FragmentTransaction mTransaction;

	/**
	 * @brief Constructor of TabListener
	 * @param activity
	 * @param tag
	 * @param clz
	 * @details Constructor of TabListener
	 */
	  public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
	    mActivity = activity;
	    mTag = tag;
	    mClass = clz;
	  }
	  
	/**
	 * @brief Method called when the tab is selected
	 * @param tab
	 * @param ft
	 * @details Method called when the tab is selected
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    // Check if the fragment is already initialized
		  mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
		  mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
	    if (mFragment == null) {
	      // If not, instantiate and add it to the activity
	      mFragment = Fragment.instantiate(
	                        mActivity, mClass.getName());
	      //mFragment.setProviderId(mTag); // id for event provider
	      setmTransaction(ft);
	      ft.add(android.R.id.content, mFragment, mTag);
	    } else {
	      // If it exists, simply attach it in order to show it
	    	setmTransaction(ft);
	      ft.attach(mFragment);
	    }

	    
	  }
	
	/**
	 * @brief Method called when the tab is unselected
	 * @param tab
	 * @param ft
	 * @details Method called when the tab is unselected
	 */
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		  
		  if (mFragment != null) {
	      // Detach the fragment, because another one is being attached
	      ft.detach(mFragment);
	    }
		  
	  }

	/**
	 * @brief Method called when the tab is reselected
	 * @param tab
	 * @param ft
	 * @details Method called when the tab is reselected
	 */
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	    // User selected the already selected tab. Usually do nothing.
		  		  
	  }
	  
	/**
	 * @brief Returns the mTransaction attribute
	 * @return mTransaction
	 * @details Returns the mTransaction attribute
	 */
	public FragmentTransaction getmTransaction() {
		return mTransaction;
	}

	/**
	 * @brief Sets the mTransaction attribute
	 * @param mTransaction 
	 * @details Sets the mTransaction attribute
	 */
	public void setmTransaction(FragmentTransaction mTransaction) {
		this.mTransaction = mTransaction;
	}


}