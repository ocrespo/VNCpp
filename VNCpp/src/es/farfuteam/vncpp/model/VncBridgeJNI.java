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
package es.farfuteam.vncpp.model;

import android.os.Debug;
import android.util.Log;




/**
 * @author roni
 *
 */

public  class VncBridgeJNI extends ObservableCanvas implements ObserverJNI{
	
	
	public enum ConnectionError{ALLOK,NoServerFound ,NoFrameFound,errorCreateThread};
	public enum MouseEvent{Click ,RightClick};
	
	private native int iniConnect(String host,int port,String pass,int quality,int compress,boolean hideMouse);
	private native void closeConnection();
	private native void finish();
	private native void iniJNI();
	private native void rfbLoop();
	private native boolean mouseEvent(int x,int y,int event);
	private native boolean keyEvent(int key,boolean down);
	
	private Runnable createScreen;

	private Thread iniBitmapData;

	private Screen screen;
	

	static {  
		System.loadLibrary("vncmain");  
	}
	public VncBridgeJNI() {

		
		createScreen = new Runnable() {
			
			@Override
			public void run() {
				screen.createData();
				
			}
		};
		
		
	}

	public ConnectionError startConnect(String host,int port,String pass,String quality,boolean wifi,boolean hideMouse){

		iniJNI();
		
		
		int compress;
		if(wifi){
			compress = 3;
		}
		else{
			compress = 5;
		}
		
		int aux_quality;
		if(quality.equals("Super-alta")){
			aux_quality = 9;
		}
		else if(quality.equals("Alta")){
			aux_quality = 7;
		}
		else if(quality.equals("Media")){
			aux_quality = 5;
		}
		else{
			aux_quality = 2;
		}
		

		int int_error = iniConnect(host, port,pass,aux_quality,compress,hideMouse);
		ConnectionError error= ConnectionError.values()[int_error];
		
		if(error != ConnectionError.ALLOK){
			finish();
		}
		return error;
		
		
	}

	public void finishVnc(){
		finish();
		screen = null;
	}

	
	private static void memoryProbe() {
		System.gc();
		System.gc();
		Runtime runtime = Runtime.getRuntime();
		Double allocated = new Double(Debug.getNativeHeapAllocatedSize()) / 1048576.0;
		Double available = new Double(Debug.getNativeHeapSize()) / 1048576.0;
		Double free = new Double(Debug.getNativeHeapFreeSize()) / 1048576.0;
		long maxMemory = runtime.maxMemory();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		
		Log.e("VncBridgeJNI", "allocated:"+allocated+" available:"+available+" free:"+free);
		Log.e("VncBridgeJNI", "maxMem:"+maxMemory+" totalMem:"+totalMemory+" freeMem:"+freeMemory);
	}
	 
	@Override
	public void updateFinishConnection(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				finish();	
				screen = null;
			}
		}).start();
		
		
		notifyFinish();
	}
	@Override
	public void updateIniFrame(int width, int height, int bpp, int depth) {
		screen = new Screen(width, height, bpp);
		iniBitmapData = new Thread(createScreen);
		iniBitmapData.start();
		
		while(iniBitmapData.isAlive());
		
		notifyIniFrame(screen.getData(),0,0,0,width,height,screen.getWidth(),screen.getHeight());
		
	}
	@Override
	public void updateReDraw(int x,int y,int width,int height) {
		
		notifyRedraw(x, y, width, height);
	}
	
	public void sendMouseEvent(int x,int y,MouseEvent event){
		int aux_event = 0;
		if(event == MouseEvent.Click){
			aux_event = 1;
		}
		else if(event == MouseEvent.RightClick){
			aux_event = 4;
		}
		mouseEvent(x, y, aux_event);
	}
	public void sendKey(int key,boolean down){
		keyEvent(key,down);
	}
}
