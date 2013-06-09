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

#ifndef CLIENTCONNECTIONRFB_H_
#define CLIENTCONNECTIONRFB_H_

#include <string>

#include <rfb/rfbclient.h>

#include <pthread.h>

using namespace std;

#include <CommonType.h>


/**
 * @class ClientConnectionRFB
 * @brief Clase encargada de gestionar la conexion con el servidor mediante rfb.
 */
class HandlerRFB;
class ClientConnectionRFB {
private:
	rfbClient *clientRFB;///< Puntero a la estructura de rfb con la informacion del cliente.
	pthread_t mainThreadId;///< ID del hilo que ejecuta el bucle principal

	bool stop_connection;///< Indica si la conexion debe ser interrumpida

	bool thread_finish;

	int buttonMask;

	static void* eventLoop(void *This);

	rfbKeySym transformToRfbKey(int key);
public:
	ClientConnectionRFB();
	~ClientConnectionRFB();

	ConnectionError iniConnection(char* host,int port,char* pass,int picture_quality,int compress);
	void cleanRfb();

	bool sendMouseEvent(int x,int y,MouseEvent event);
	bool sendKeyEvent(int key,bool down);

	void stopConnection();

	void hideMouse();
	void showMouse();
};

#endif /* CLIENTCONNECTIONRFB_H_ */
