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

/**
 * @class ObservableCanvas
 * @brief This Class is a observable class for CanvasActivity
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @details This Class is a observable class for CanvasActivity
 */
public class ObservableCanvas {
	/**
	 * The Observer
	 */
	private ObserverCanvas observer;
	
	/**
	 * @brief The default constructor
	 * @details the default constructor
	 */
	public ObservableCanvas(){
		
	}
	
	/**
	 * @brief Sets the observer
	 * @param observer The observer
	 * @details Sets the observer attribute with the observer parameter
	 */
	public void addObserver(ObserverCanvas observer){
		this.observer = observer;
	}
	
	/**
	 * @brief Notifies the frame initialization
	 * @param data The image
	 * @param offset The offset
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param width The image width
	 * @param height The image height
	 * @details Calls to the observer updateIniFrame method
	 */
	public void notifyIniFrame(int[] data,int offset,int x,int y,int width,int height){
		observer.updateIniFrame(data,offset,x,y,width,height);
	}
	
	/**
	 * @brief Notifies the screen redraw
	 * @details Notifies the screen redraw. Calls the observer updateReDraw method
	 */
	public void notifyRedraw(){
		observer.updateRedraw();
	}
	
	/**
	 * @brief Notifies the end of the update
	 * @details Notifies the end of the update. Calls the observer updateFinish method
	 */
	public void notifyFinish(){
		observer.updateFinish();
	}
	
	/**
	 * @brief Notifies the password request
	 * @return The password
	 * @details Notifies the password request
	 */
	public String notifyPass(){
		return observer.updatePass();
	}
	
	/**
	 * @brief Notifies that does not have enough memory
	 * @details Notifies that the terminal does not have enough memory
	 */
	public void notifyOutOfMemory(){
		observer.updateOutOfMemory();
	}
}
