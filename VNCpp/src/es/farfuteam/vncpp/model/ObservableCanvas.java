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

/**
 * @author roni
 *
 */
public class ObservableCanvas {
	private ObserverCanvas observer;
	public ObservableCanvas(){
		
	}
	public void addObserver(ObserverCanvas observer){
		this.observer = observer;
	}
	public void notifyIniFrame(int[] data,int offset,int x,int y,int width,int height,int realWidth,int realHeight){
		observer.updateIniFrame(data,offset,x,y,width,height,realWidth,realHeight);
	}
	public void notifyRedraw(){
		observer.updateRedraw();
	}
	public void notifyFinish(){
		observer.updateFinish();
	}
	public String notifyPass(){
		return observer.updatePass();
	}
	public void notifyOutOfMemory(){
		observer.updateOutOfMemory();
	}
}
