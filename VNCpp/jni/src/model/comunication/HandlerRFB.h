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

#ifndef HANDLERRFB_H_
#define HANDLERRFB_H_

#include <rfb/rfbclient.h>

using namespace std;

class ClientScreen;
class HandlerRFB {
private:
	static ClientScreen *screen;
public:
	HandlerRFB();
	virtual ~HandlerRFB();

	static void setScreen(ClientScreen *screen);

	static rfbBool iniFrameBuffer(rfbClient* client_screen);
	static void updateScreen(rfbClient *client,int x,int y,int w,int h);

	static void finishConnection();
	static void finishClient();

	static char* getPass(rfbClient* client);
};

#endif /* HANDLERRFB_H_ */
