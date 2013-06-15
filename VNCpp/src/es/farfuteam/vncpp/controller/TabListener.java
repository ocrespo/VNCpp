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




public class TabListener<T extends ListFragment> implements android.app.ActionBar.TabListener{
	  private Fragment mFragment;
	  private final FragmentActivity mActivity;
	  private final String mTag;
	  private final Class<T> mClass;
	  private FragmentTransaction mTransaction;

	  public TabListener(FragmentActivity activity, String tag, Class<T> clz) {
	    mActivity = activity;
	    mTag = tag;
	    mClass = clz;
	  }
	  

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

	  @Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		  
		  if (mFragment != null) {
	      // Detach the fragment, because another one is being attached
	      ft.detach(mFragment);
	    }
		  
	  }

	  @Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	    // User selected the already selected tab. Usually do nothing.
		  		  
	  }
	  

	public FragmentTransaction getmTransaction() {
		return mTransaction;
	}


	public void setmTransaction(FragmentTransaction mTransaction) {
		this.mTransaction = mTransaction;
	}


}