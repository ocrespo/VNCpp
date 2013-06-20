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
 * @interface ObserverJNI
 * @brief This is the observer interface for JNI
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @details This is the observer interface for JNI
 */
public interface ObserverJNI {
	void updateFinishConnection();
	void updateIniFrame(int width,int height,int bpp,int depth);
	void updateReDraw();
	String updateAskPass();
}
