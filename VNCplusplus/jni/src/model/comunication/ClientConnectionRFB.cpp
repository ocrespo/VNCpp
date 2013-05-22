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

#include "ClientConnectionRFB.h"

#include <jni.h>
#include <android/log.h>


#include "HandlerRFB.h"


#define bitsPerSample 8
#define samplesPerPixel 3
#define bytesPerPixel 4
#define timeWait 500



/**
 * Constructor por defecto.
 *
 * Inicializa clientRFB a NULL y stop_connection a false
 */
ClientConnectionRFB::ClientConnectionRFB() {
	//cl = NULL;
	//clientRFB=rfbGetClient(8,3,4);
	clientRFB = NULL;
	stop_connection = false;
	thread_finish = true;

	//buttonMask = 0;

}
/**
 * Destructor por defecto.
 *
 * Pone stop_connection a true, el hilo se parara y limpiara la inforacion de la conexion
 */
ClientConnectionRFB::~ClientConnectionRFB() {
	if(DEBUG){
		LOGE("Limpianndo rfb");
	}
	stop_connection = true;

	while(thread_finish == false){
		sleep(1);
	}

	cleanRfb();

	/*if(clientRFB != NULL){
		rfbClientCleanup(clientRFB);

		delete clientRFB;
		clientRFB = NULL;
	}*/

}
/**
 * Inicia la conexion.
 *
 * Inicia la conexion con el servidor indicado, configura las funciones (handler)que deben ser invocadas por los
 * eventos de rfb. Una vez verificada que la conexion es satisfactoria invoca eventLoop en otro hilo.
 * @param host IP del servidor a la que se conectara.
 * @param port Puerto por el que se realizara la conexion.
 * @return devuelve si la conexion tuvo exito, mediante el enum ConnectionError.
 */
ConnectionError ClientConnectionRFB::iniConnection(char* host,int port){
	if(DEBUG)
		LOGE("JNI Iniciando conexion");


	clientRFB=rfbGetClient(bitsPerSample,samplesPerPixel,bytesPerPixel);

	clientRFB->serverPort = port;
	clientRFB->serverHost = host;

	clientRFB->MallocFrameBuffer=HandlerRFB::iniFrameBuffer;
	clientRFB->canHandleNewFBSize = TRUE;
	clientRFB->GotFrameBufferUpdate=HandlerRFB::updateScreen;
	//clientRFB->HandleKeyboardLedState=kbd_leds;
	//clientRFB->HandleTextChat=text_chat;
	//clientRFB->GotXCutText = got_selection;
	clientRFB->listenPort = LISTEN_PORT_OFFSET;
	clientRFB->listen6Port = LISTEN_PORT_OFFSET;

	ConnectionError error_connect;

	if(!rfbInitClient(clientRFB,0,NULL)){
		error_connect = NoServerFound;
		LOGE("No server found");
		clientRFB = NULL;
	}
	else if( !clientRFB->frameBuffer){
		LOGE("No Frame Found");
		error_connect = NoFrameFound;
		cleanRfb();

	}

	if(error_connect == NoFrameFound || error_connect == NoServerFound){
		return error_connect;
	}
	stop_connection = false;

	int error_thread;

	error_thread = pthread_create(&mainThreadId,NULL,eventLoop,this);
	if(error_thread){
		LOGE("Error create thread");
		error_connect = errorCreateThread;
	}

	error_connect = ALLOK;
	//si hubo un error se finaliza.
	if(error_connect != ALLOK){
		stop_connection = true;
		cleanRfb();
	}
	else{
		pthread_detach(mainThreadId);
	}
	//eventLoop(this);


	if(DEBUG)
		LOGE("Inicio de conexion OK");

	return error_connect;
}
/**
 * Limpia la informacion de clientRFB y lo pone a NULL. Si hubiese una conexion activa esta se cerraria.
 */
void ClientConnectionRFB::cleanRfb(){
	if(DEBUG)
		LOGE("Close connection");


	if(clientRFB != NULL){

		close(clientRFB->sock);
		/*if(!clientRFB->listenSpecified){
			free((void*)clientRFB->appData.encodingsString);
		}*/
		if(clientRFB->frameBuffer){
			free(clientRFB->frameBuffer);
			clientRFB->frameBuffer = NULL;
		}

		rfbClientCleanup(clientRFB);
		if(DEBUG)
			LOGE("Fin Limpiando Rfb");
		//delete clientRFB;


		clientRFB = NULL;
	}
}
/**
 * Bucle principal de rfb.
 *
 * Bucle que atiende a los eventos de rfb. No parara hasta que stop_connection sea true.
 * @param This Puntero al objeto.
 */
void* ClientConnectionRFB::eventLoop(void *This){

	int mes;
	ClientConnectionRFB *aux_this = (ClientConnectionRFB*)This;
	if(DEBUG)
		LOGE("LOOP");
	aux_this->thread_finish = false;
	bool serverOut = false;
	while(!aux_this->stop_connection) {
		mes=WaitForMessage(aux_this->clientRFB,timeWait);

		if(mes<0){
			aux_this->stop_connection = true;
			serverOut = true;
		}
		if(mes){
			if(!HandleRFBServerMessage(aux_this->clientRFB)){
				aux_this->stop_connection = true;
				serverOut = true;
			}
		}
	}

	//aux_this->cleanRfb();
	if(DEBUG)
		LOGE("Fin cleanRfb");


	if(serverOut == true){
		HandlerRFB::finishConnection();
	}
	else{
		if(DEBUG)
			LOGE("JNI FinishClient");
		HandlerRFB::finishClient();
	}
	aux_this->thread_finish = true;
	//pthread_detach(pthread_self());
	//pthread_exit(NULL);
}
/**
 * Para la conexion con el servidor.
 */
void ClientConnectionRFB::stopConnection(){
	stop_connection = true;
}
bool ClientConnectionRFB::sendMouseEvent(int x,int y,MouseEvent event){
	bool ok;

	ok = SendPointerEvent(clientRFB,x,y,event);
	SendPointerEvent(clientRFB,x,y,0 );


	return ok;
}
bool ClientConnectionRFB::sendKeyEvent(int key){
	SendKeyEvent(clientRFB,XK_A,true);
	SendKeyEvent(clientRFB,XK_A,false);
}
