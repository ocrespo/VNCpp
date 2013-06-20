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

#include "HandlerRFB.h"

#include <model/screen/ClientScreen.h>

#include <CommonType.h>
#include <android/log.h>

ClientScreen* HandlerRFB::screen = NULL;
char* HandlerRFB::pass = NULL;
bool HandlerRFB::update = true;

/**
 * @brief The default constructor
 * @details The default constructor
 */
HandlerRFB::HandlerRFB() {
	// TODO Auto-generated constructor stub

}

/**
 * @brief The default destroyer
 * @details The default destroyer
 */
HandlerRFB::~HandlerRFB() {
	// TODO Auto-generated destructor stub
}

/**
 * @brief Sets the ClientScreen attribute
 * @param client_screen The ClientScreen
 * @details Sets the ClientScreen attribute
 */
void HandlerRFB::setScreen(ClientScreen *client_screen){
	screen = client_screen;
}

/**
 * @brief Initializes the frameBuffer
 * @param client Pointer to the RFB structure with the client information
 * @return true
 * @details Calls the ClientScreen method iniScreen to initialize the frameBuffer, and also
 * sets the structure rfbClient
 */
rfbBool HandlerRFB::iniFrameBuffer(rfbClient* client) {
	int size = screen->iniScreen(client->width,client->height,client->format.bitsPerPixel);

	client->updateRect.x = client->updateRect.y = 0;
	client->updateRect.w = client->width;
	client->updateRect.h = client->height;

	screen->doMask(client);


	client->frameBuffer = (uint8_t*)malloc(size);




	return true;
}

/**
 * @brief Sets the password
 * @param aux_pass The password
 * @details Sets the password
 */
void HandlerRFB::setPass(char *aux_pass){
	if(pass == NULL){
		pass = (char*)malloc(250*sizeof(char));
	}
	if(aux_pass != NULL){
		pass = strcpy(pass,aux_pass);
	}
}

/**
 * @brief Sets the update attribute
 * @param aux_update A boolean
 * @details Sets the update attribute
 */
void HandlerRFB::setUpdate(bool aux_update){
	update = aux_update;
}

/**
 * @brief Frees the password
 * @details Frees the password
 */
void HandlerRFB::freePass(){
	if(pass != NULL){
		free(pass);
		pass = NULL;
	}
}

/**
 * @brief Gets the password from rfbClient
 * @param client Pointer to the RFB structure with the client information
 * @return the password
 */
char* HandlerRFB::getPass(rfbClient* client){
	if(pass == NULL || strcmp(pass,"") == 0){
		char* aux_pass =screen->notifyAskPass();
		setPass(aux_pass);
	}
	return strdup(pass);
}

/**
 * @brief The RFB event updateScreen
 * @param client Pointer to the RFB structure with the client information
 * @param x The initial x coordinate of the section
 * @param y The initial y coordinate of the section
 * @param w The section width
 * @param h The section height
 * @details Calls to the ClientScreen method updateScreen to update the section of the image that has changes
 */
void HandlerRFB::updateScreen(rfbClient *client,int x,int y,int w,int h){
	if(update)
		screen->updateScreen(client->frameBuffer,x,y,w,h);
}

/**
 * @brief The RFB event finishUpdate
 * @param client Pointer to the RFB structure with the client information
 * @details Calls to the ClientScreen method notifyReDraw to indicate the end of the update
 */
void HandlerRFB::finishUpdate(rfbClient *client){
	if(update)
		screen->notifyReDraw();
}

/**
 * @brief The RFB event fihishConnection
 * @details Calls to the ClientScreen method notifyFinishConnection to indicate the end of the connection
 */
void HandlerRFB::finishConnection(){
	screen->notifyFinishConnection();
}

/**
 * @brief Finish the current thread
 * @details Calls to the ClientScreen method finishByClient that ends the current thread
 */
void HandlerRFB::finishClient(){
	screen->finishByClient();
}


