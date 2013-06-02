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

#include "HandlerRFB.h"

#include <model/screen/ClientScreen.h>

#include <CommonType.h>
#include <android/log.h>

ClientScreen* HandlerRFB::screen = NULL;

HandlerRFB::HandlerRFB() {
	// TODO Auto-generated constructor stub

}

HandlerRFB::~HandlerRFB() {
	// TODO Auto-generated destructor stub
}
void HandlerRFB::setScreen(ClientScreen *client_screen){
	screen = client_screen;
}
rfbBool HandlerRFB::iniFrameBuffer(rfbClient* client) {
	int size = screen->iniScreen(client->width,client->height,client->format.bitsPerPixel);

	client->updateRect.x = client->updateRect.y = 0;
	client->updateRect.w = client->width;
	client->updateRect.h = client->height;

	screen->doMask(client);


	client->frameBuffer = (uint8_t*)malloc(size);




	return true;
}
char* HandlerRFB::getPass(rfbClient* client){
	return "1234";
}
void HandlerRFB::updateScreen(rfbClient *client,int x,int y,int w,int h){
	screen->updateScreen(client->frameBuffer,x,y,w,h);
}
void HandlerRFB::finishConnection(){
	screen->notifyFinishConnection();
}
void HandlerRFB::finishClient(){
	screen->finishByClient();
}
