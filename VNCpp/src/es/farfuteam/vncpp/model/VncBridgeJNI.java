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
package es.farfuteam.vncpp.model;

import es.farfuteam.vncpp.controller.NewConnectionActivity.QualityArray;




/**
 * @class VncBridgeJNI
 * @brief This Class handles the communication with the native code
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends ObservableCanvas
 * @implements ObserverJNI
 * @details This Class handles the communication with the native code.
 */
public  class VncBridgeJNI extends ObservableCanvas implements ObserverJNI{
	
	/**
	 * @enum ConnectionError
	 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
	 * @details Indicates the different errors that you can get when you start the connection
	 */
	public enum ConnectionError{ALLOK,///< No error
								NoServerFound,///< No server found error
								NoFrameFound,///< No frameBuffer information error
								errorCreateThread///< Could not be created the thread to handle the request from RFB
								};
	
	/**
	 * @enum MouseEvent
	 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
	 * @details Indicates the different mouse event
	 *
	 */
	public enum MouseEvent{Click,///< leftClick
						   RightClick///< rightClick
						   };

	/**
	 * @brief Initializes the connection
	 * @param host the IP
	 * @param port the port
	 * @param pass the password
	 * @param quality the image quality
	 * @param compress the image has to be compress or not
	 * @param hideMouse the mouse has to be hide or not
	 * @return An connection error
	 * @details Calls to iniConnection method from Vnc to start the connection with the server
	 */
	private native int iniConnect(String host,int port,String pass,int quality,int compress,boolean hideMouse);
	
	/**
	 * @brief Closes the connection
	 * @details Calls to closeConnection method from Vnc to stop the connection with the server
	 */
	private native void closeConnection();
	
	/**
	 * @brief Stops the updates
	 * @details Stops the updates
	 */
	private native void stopUpdate();
	
	/**
	 * @brief Cleans the memory
	 * @details Destroys the Vnc object
	 */
	private native void finish();
	
	/**
	 * @brief Initializes the JNI
	 * @details Initializes the JNI, creates the Vnc object and adds env and javaThis as observers
	 */
	private native void iniJNI();
	
	/**
	 * @brief Sends a mouse event to the server
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param event the mouse event
	 * @return true if everything ok, false otherwise
	 * @details Sends a mouse event to the server from the client. Specifies the coordinates x and y of the event and also specifies the type
	 */
	private native boolean mouseEvent(int x,int y,int event);
	
	/**
	 * @brief Sends a key event to the server
	 * @param key the key
	 * @param down the key is down or not
	 * @return true if everything ok, false otherwise
	 * @details Sends a key event to the server from the client
	 */
	private native boolean keyEvent(int key,boolean down);
	
	/**
	 * Super high image quality
	 */
	private static int sQuality=9;
	
	/**
	 * High image quality
	 */
	private static int hQuality=7;
	
	/**
	 * Medium image quality
	 */
	private static int mQuality=5;
	
	/**
	 * Low image quality
	 */
	private static int lQuality=2;
	
	/**
	 * The wifi image compress
	 */
	private static int wifiCompress= 3;
	
	/**
	 * The 3g image compress
	 */
	private static int gCompress = 5;
	
	/**
	 * A Runnable to create the screen data
	 */
	private Runnable createScreen;

	/**
	 * A thread to create the screen data
	 */
	private Thread iniBitmapData;

	/**
	 * The Screen object
	 */
	private Screen screen;
	
	/**
	 * If true it indicates that there are an error, false otherwise
	 */
	private boolean error = false;

	static {  
		System.loadLibrary("vncmain");  
	}
	
	/**
	 * @brief The default constructor
	 * @details the default constructor. Creates the screen data. If there are not enough memory, 
	 * notifies to CanvasActivity the error and sets the error attribute as true
	 */
	public VncBridgeJNI() {

		
		createScreen = new Runnable() {
			
			@Override
			public void run() {
				try{
					screen.createData();
				}catch (OutOfMemoryError e) {
					error = true;
					notifyOutOfMemory();
				}
				
			}
		};
		
		
	}

	/**
	 * @brief Starts the connection
	 * @param host The IP
	 * @param port The port
	 * @param pass The password
	 * @param quality The image quality
	 * @param wifi is a wifi connection or not
	 * @param hideMouse the mouse has to be hide or not
	 * @return A connection error
	 * @details Starts the connection. First starts JNI and then calls to the native function iniConnect to starts the connection
	 */
	public ConnectionError startConnect(String host,int port,String pass,QualityArray quality,boolean wifi,boolean hideMouse){

		iniJNI();
		
		
		int compress;
		if(wifi){
			compress = wifiCompress;
		}
		else{
			compress = gCompress;
		}
		
		int aux_quality;
		if(quality == QualityArray.SuperHigh){
			aux_quality = sQuality;
		}
		else if(quality  == QualityArray.High){
			aux_quality = hQuality;
		}
		else if(quality == QualityArray.Medium){
			aux_quality = mQuality;
		}
		else{
			aux_quality = lQuality;
		}
		

		int int_error = iniConnect(host, port,pass,aux_quality,compress,hideMouse);
		ConnectionError error= ConnectionError.values()[int_error];
		
		if(error != ConnectionError.ALLOK){
			finish();
		}
		return error;
		
		
	}

	/**
	 * @brief Closes the connection
	 * @details Calls to the native function to close the connection
	 */
	public void finishVnc(){
		finish();
		screen = null;
	}
	
	/**
	 * @brief Closes the connection if the server has been disconnected
	 * @details Close the connection if the server has been disconnected and notifies to CanvasActity
	 */
	@Override
	public void updateFinishConnection(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				finish();	
				screen = null;
			}
		}).start();
		
		if(!error)
			notifyFinish();
	}
	
	/**
	 * @brief Initializes the bitmap
	 * @details Initializes the bitmap and notifies to CanvasActivity
	 */
	@Override
	public void updateIniFrame(int width, int height, int bpp, int depth) {
		screen = new Screen(width, height, bpp);
		
		iniBitmapData = new Thread(createScreen);
		iniBitmapData.start();
		while(iniBitmapData.isAlive() && !error);
		if(!error)
			notifyIniFrame(screen.getData(),0,0,0,width,height);
		else{
			stopUpdate();
		}
		
		
		
		
	}
	
	/**
	 * @brief Notifies a redraw to CanvasActivity
	 * @details Notifies a redraw to CanvasActivity
	 */
	@Override
	public void updateReDraw() {
		
		notifyRedraw();
	}
	
	/**
	 * @brief Sends a mouse event
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param event The mouse event
	 * @details Sends a mouse event to the native code
	 */
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
	
	/**
	 * @brief Sends a key event
	 * @param key The key
	 * @param down the key is down or not
	 * @details Sends a key event to the native code
	 */
	public void sendKey(int key,boolean down){
		keyEvent(key,down);
	}
	
	/**
	 * @brief Asks for the password
	 * @details Asks for the password to CanvasActivity
	 */
	@Override
	public String updateAskPass() {
		return notifyPass();
		
	}
}
