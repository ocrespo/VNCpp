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

#ifndef CLIENTCONNECTIONRFB_H_
#define CLIENTCONNECTIONRFB_H_

#include <string>

#include <rfb/rfbclient.h>

#include <pthread.h>

using namespace std;

#include <CommonType.h>

/**
 * @class ClientConnectionRFB
 * @brief This Class handles the RFB connection with the server
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @details This Class handles the RFB connection with the server
 */
class HandlerRFB;
class ClientConnectionRFB {
private:
	rfbClient *clientRFB;///< Pointer to the RFB structure with the client information
	pthread_t mainThreadId;///< Thread ID that handles the main loop

	bool stop_connection;///< Indicates if the connection has to be interrupted

	bool thread_finish;///< Indicates the end of the main thread

	int buttonMask;///<

	static void* eventLoop(void *This);

	rfbKeySym transformToRfbKey(int key);
public:
	ClientConnectionRFB();
	~ClientConnectionRFB();

	ConnectionError iniConnection(char* host,int port,char* pass,int picture_quality,int compress,bool hide_mouse);
	void cleanRfb();

	bool sendMouseEvent(int x,int y,MouseEvent event);
	bool sendKeyEvent(int key,bool down);

	void stopConnection();

};

#endif /* CLIENTCONNECTIONRFB_H_ */
