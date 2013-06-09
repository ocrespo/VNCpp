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
ConnectionError ClientConnectionRFB::iniConnection(char* host,int port,char* pass,int picture_quality,int compress){
	if(DEBUG)
		LOGE("JNI Iniciando conexion");


	clientRFB=rfbGetClient(bitsPerSample,samplesPerPixel,bytesPerPixel);

	clientRFB->serverPort = port;
	clientRFB->serverHost = host;

	clientRFB->programName = "VNC++";

	HandlerRFB::setPass(pass);

	clientRFB->GetPassword = HandlerRFB::getPass;

	clientRFB->appData.qualityLevel = picture_quality;
	clientRFB->appData.compressLevel = compress;
	//clientRFB->appData.useRemoteCursor = true;


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
		if(DEBUG)
			LOGE("No server found");
		clientRFB = NULL;
	}
	else if( !clientRFB->frameBuffer){
		if(DEBUG)
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
		if(DEBUG)
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
void ClientConnectionRFB::hideMouse(){
	clientRFB->appData.useRemoteCursor = true;
}
void ClientConnectionRFB::showMouse(){
	clientRFB->appData.useRemoteCursor = false;
}
bool ClientConnectionRFB::sendMouseEvent(int x,int y,MouseEvent event){
	bool ok;

	ok = SendPointerEvent(clientRFB,x,y,event);
	SendPointerEvent(clientRFB,x,y,0 );


	return ok;
}
bool ClientConnectionRFB::sendKeyEvent(int key){
	rfbKeySym rfbKey = transformToRfbKey(key);
	SendKeyEvent(clientRFB,rfbKey,true);
	SendKeyEvent(clientRFB,rfbKey,false);
}
rfbKeySym ClientConnectionRFB::transformToRfbKey(int key){
	 rfbKeySym rfbKey = 0;
	 switch (key) {
	        case 67: rfbKey = XK_BackSpace; break;
	       // case SDLK_TAB: rfbKey = XK_Tab; break;
	        //case SDLK_CLEAR: rfbKey = XK_Clear; break;
	        //case SDLK_RETURN: rfbKey = XK_Return; break;
	        //case SDLK_PAUSE: rfbKey = XK_Pause; break;
	        //case SDLK_ESCAPE: rfbKey = XK_Escape; break;
	        //case SDLK_SPACE: rfbKey = XK_space; break;
	        //case SDLK_DELETE: rfbKey = XK_Delete; break;

	        case 29: rfbKey = XK_a; break;
	        case 30: rfbKey = XK_b; break;
	        case 31: rfbKey = XK_c; break;
	        case 32: rfbKey = XK_d; break;
	        case 33: rfbKey = XK_e; break;
	        case 34: rfbKey = XK_f; break;
	        case 35: rfbKey = XK_g; break;
	        case 36: rfbKey = XK_h; break;
	        case 37: rfbKey = XK_i; break;
	        case 38: rfbKey = XK_j; break;
	        case 39: rfbKey = XK_k; break;
	        case 40: rfbKey = XK_l; break;
	        case 41: rfbKey = XK_m; break;
	        case 42: rfbKey = XK_n; break;
	        case 43: rfbKey = XK_o; break;
	        case 44: rfbKey = XK_p; break;
	        case 45: rfbKey = XK_q; break;
	        case 46: rfbKey = XK_r; break;
	        case 47: rfbKey = XK_s; break;
	        case 48: rfbKey = XK_t; break;
	        case 49: rfbKey = XK_u; break;
	        case 50: rfbKey = XK_v; break;
	        case 51: rfbKey = XK_w; break;
	        case 52: rfbKey = XK_x; break;
	        case 53: rfbKey = XK_y; break;
	        case 54: rfbKey = XK_z ; break;

	        case 7: rfbKey = XK_KP_0; break;
	        case 8: rfbKey = XK_KP_1; break;
	        case 9: rfbKey = XK_KP_2; break;
	        case 10: rfbKey = XK_KP_3; break;
	        case 11: rfbKey = XK_KP_4; break;
	        //case 12: rfbKey = XK_KP_5; break;
	        case 13: rfbKey = XK_KP_6; break;
	        case 14: rfbKey = XK_KP_7; break;
	        //case 12: rfbKey = XK_KP_8; break;
	        case 16: rfbKey = XK_KP_9; break;
	        case 55: rfbKey = XK_KP_Decimal; break;
	        //case 12: rfbKey = XK_KP_Divide; break;
	        //case SDLK_KP_MULTIPLY: rfbKey = XK_KP_Multiply; break;
	       // case SDLK_KP_MINUS: rfbKey = XK_KP_Subtract; break;
	        //case SDLK_KP_PLUS: rfbKey = XK_KP_Add; break;
	        //case SDLK_KP_ENTER: rfbKey = XK_KP_Enter; break;
	        //case SDLK_KP_EQUALS: rfbKey = XK_KP_Equal; break;
	        //case SDLK_UP: rfbKey = XK_Up; break;
	        //case SDLK_DOWN: rfbKey = XK_Down; break;
	        //case SDLK_RIGHT: rfbKey = XK_Right; break;
	        //case SDLK_LEFT: rfbKey = XK_Left; break;
	       // case SDLK_INSERT: rfbKey = XK_Insert; break;
	        //case SDLK_HOME: rfbKey = XK_Home; break;
	        //case SDLK_END: rfbKey = XK_End; break;
	        //case SDLK_PAGEUP: rfbKey = XK_Page_Up; break;
	        //case SDLK_PAGEDOWN: rfbKey = XK_Page_Down; break;

	        /*case SDLK_F1: rfbKey = XK_F1; break;
	        case SDLK_F2: rfbKey = XK_F2; break;
	        case SDLK_F3: rfbKey = XK_F3; break;
	        case SDLK_F4: rfbKey = XK_F4; break;
	        case SDLK_F5: rfbKey = XK_F5; break;
	        case SDLK_F6: rfbKey = XK_F6; break;
	        case SDLK_F7: rfbKey = XK_F7; break;
	        case SDLK_F8: rfbKey = XK_F8; break;
	        case SDLK_F9: rfbKey = XK_F9; break;
	        case SDLK_F10: rfbKey = XK_F10; break;
	        case SDLK_F11: rfbKey = XK_F11; break;
	        case SDLK_F12: rfbKey = XK_F12; break;
	        case SDLK_F13: rfbKey = XK_F13; break;
	        case SDLK_F14: rfbKey = XK_F14; break;
	        case SDLK_F15: rfbKey = XK_F15; break;*/

	        /*case SDLK_NUMLOCK: rfbKey = XK_Num_Lock; break;
	        case SDLK_CAPSLOCK: rfbKey = XK_Caps_Lock; break;
	        case SDLK_SCROLLOCK: rfbKey = XK_Scroll_Lock; break;
	        case SDLK_RSHIFT: rfbKey = XK_Shift_R; break;
	        case SDLK_LSHIFT: rfbKey = XK_Shift_L; break;
	        case SDLK_RCTRL: rfbKey = XK_Control_R; break;
	        case SDLK_LCTRL: rfbKey = XK_Control_L; break;
	        case SDLK_RALT: rfbKey = XK_Alt_R; break;
	        case SDLK_LALT: rfbKey = XK_Alt_L; break;
	        case SDLK_RMETA: rfbKey = XK_Meta_R; break;
	        case SDLK_LMETA: rfbKey = XK_Meta_L; break;
	        case SDLK_LSUPER: rfbKey = XK_Super_L; break;*/

	        /*case SDLK_RSUPER: rfbKey = XK_Super_R; break;
	        case SDLK_COMPOSE: rfbKey = XK_Compose; break;

	        case SDLK_MODE: rfbKey = XK_Mode_switch; break;
	        case SDLK_HELP: rfbKey = XK_Help; break;
	        case SDLK_PRINT: rfbKey = XK_Print; break;
	        case SDLK_SYSREQ: rfbKey = XK_Sys_Req; break;
	        case SDLK_BREAK: rfbKey = XK_Break; break;*/
	        default: break;
	 }
	 return rfbKey;
}
