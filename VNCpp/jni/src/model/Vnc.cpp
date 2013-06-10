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

#include "Vnc.h"

#include  <model/screen/ClientScreen.h>
#include  <model/comunication/ClientConnectionRFB.h>
#include  <model/comunication/HandlerRFB.h>


/**
 * Constructor por defecto
 */
Vnc::Vnc() {
	rfb = new ClientConnectionRFB;
	screen = new ClientScreen;
	HandlerRFB::setScreen(screen);

}
/**
 * destructor por defecto
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
 * Invoca rfb->iniConnect, para que inicie la conexion
 * @param host ip a la que se conecta
 * @param port el puerto mediante el que se realiza la conexion
 * @return devuelve si la conexion tuvo exito, mediante el enum ConnectionError
 */
ConnectionError Vnc::iniConnection(char *host,int port,char *pass,int quality,int compress,bool hide_mouse){
	ConnectionError error = rfb->iniConnection(host,port,pass,quality,compress,hide_mouse);
	if(DEBUG)
		LOGE("Return inicio de conexion");
	return error;
	//rfb->eventLoop();
}
/**
 * Invoca rfb->cleanRfb, para que cierra la conexion con el servidor
 */
void Vnc::closeConnection(){
	rfb->cleanRfb();
}
/**
 * Añade a screen el observer, para que este se comunique con java
 * @param observer objecto que sera observador, es un objeto JAVA
 * @param env entorno jni cuando se hace la llamada
 */
void Vnc::addObserver(jobject observer,JNIEnv *env){
	screen->addObserver(observer,env);

}
bool Vnc::sendMouseEvent(int x,int y,MouseEvent event){
	return rfb->sendMouseEvent(x,y,event);
}
bool Vnc::sendKeyEvent(int key,bool down){
	return rfb->sendKeyEvent(key,down);
}

