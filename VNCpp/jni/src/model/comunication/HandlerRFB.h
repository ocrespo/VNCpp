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

#ifndef HANDLERRFB_H_
#define HANDLERRFB_H_

#include <rfb/rfbclient.h>

#include <ObservableJNI.h>

using namespace std;

/**
 * @class HandlerRFB
 * @brief This Class handles the RFB events
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @details This Class handles the RFB events
 */
class ClientScreen;
class ObservableJNI;
class HandlerRFB{
private:
	static ClientScreen *screen;///< A pointer to the ClientScreen object

	static char* pass;///< The password

	static bool update;///< A boolean which indicates if HandlerRFb has to manage the events or not

public:
	HandlerRFB();
	virtual ~HandlerRFB();

	static void setScreen(ClientScreen *screen);
	static void setUpdate(bool aux_update);

	static rfbBool iniFrameBuffer(rfbClient* client_screen);
	static void updateScreen(rfbClient *client,int x,int y,int w,int h);
	static void finishUpdate(rfbClient *client);

	static void finishConnection();
	static void finishClient();

	static void setPass(char *aux_pass);
	static char* getPass(rfbClient* client);
	static void freePass();


};

#endif /* HANDLERRFB_H_ */
