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

#include "Vnc.h"

#include  <model/screen/ClientScreen.h>
#include  <model/comunication/ClientConnectionRFB.h>
#include  <model/comunication/HandlerRFB.h>

/**
 * @brief The default constructor
 * @details The default constructor. Creates the ClientConnectionRFB and ClientScreen objects,
 * also sets the screen and the update as true in HandlerRFB
 */
Vnc::Vnc() {
	rfb = new ClientConnectionRFB;
	screen = new ClientScreen;
	HandlerRFB::setScreen(screen);
	HandlerRFB::setUpdate(true);

}

/**
 * @brief The default destroyer
 * @details The default destroyer. Free the password form HandlerRFB and also destroys the ClientConnectionRFB and
 * ClientScreen objects
 */
Vnc::~Vnc() {
	if(DEBUG){
		LOGE("Limpiando vnc");
	}
	HandlerRFB::freePass();
	delete rfb;
	delete screen;
}

/**
 * @brief Starts the connection
 * @param host The IP
 * @param port The port
 * @param pass The password
 * @param quality The image quality
 * @param compress the image has to be compress or not
 * @param hide_mouse the maouse has to be hide or not
 * @return ALLOK if everything ok, or an error
 * @details Calls the ClientConnectionRFB method iniConnect to start the connection
 */
ConnectionError Vnc::iniConnection(char *host,int port,char *pass,int quality,int compress,bool hide_mouse){
	ConnectionError error = rfb->iniConnection(host,port,pass,quality,compress,hide_mouse);
	if(DEBUG)
		LOGE("Return inicio de conexion");
	return error;
	//rfb->eventLoop();
}

/**
 * @brief Stops the connection
 * @details Calls the ClientConnectionRFB method stopConnection to close the connection
 */
void Vnc::closeConnection(){
	rfb->stopConnection();
}

/**
 * @brief Adds the observers to screen
 * @param observer The Java observer
 * @param env The JNI environment at this point
 * @details Adds the observer to screen. This allows screen to communicate with Java
 */
void Vnc::addObserver(jobject observer,JNIEnv *env){
	screen->addObserver(observer,env);

}

/**
 * @brief Sends a mouse event
 * @param x The x coordinate
 * @param y The y coordinate
 * @param event The event
 * @return if everything is ok or not
 */
bool Vnc::sendMouseEvent(int x,int y,MouseEvent event){
	return rfb->sendMouseEvent(x,y,event);
}

/**
 * @brief Sends a key event
 * @param key The key
 * @param down The key is down or not
 * @return If everything is ok or not
 */
bool Vnc::sendKeyEvent(int key,bool down){
	return rfb->sendKeyEvent(key,down);
}

/**
 * @brief Sets the update
 * @param update update or not
 * @details Sets the update to HandlerRFB
 */
void Vnc::setUpdate(bool update){
	HandlerRFB::setUpdate(update);
}
