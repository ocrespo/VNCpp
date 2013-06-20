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

#include "ClientScreen.h"

#include <android/log.h>

#define  main_red_mask 0x00ff0000
#define main_green_mask 0x0000ff00
#define main_blue_mask 0x000000ff

/**
 * @brief The default constructor
 * @details Sets all the attributes as 0
 */
ClientScreen::ClientScreen() {
	width = 0;
	height = 0;

	bytesPerPixel = 0;
	depth = 0;
	size = 0;
}

/**
 * @brief The default destroyer
 * @details The default destroyer
 */
ClientScreen::~ClientScreen() {
	if(DEBUG){
		LOGE("Limpiando screen");
	}
}

/**
 * @brief Sets the color mask
 * @param client Pointer to the RFB structure with the client information
 * @details Sets the color mask
 */
void ClientScreen::doMask(rfbClient *client) {

	while (! (main_red_mask & (1 << client->format.redShift))) {
		client->format.redShift++;
	}
	while (! (main_green_mask & (1 << client->format.greenShift))) {
		client->format.greenShift++;
	}
	while (! (main_blue_mask & (1 << client->format.blueShift))) {
		client->format.blueShift++;
	}
}

/**
 * @brief Initializes the frameBuffer
 * @param width The width
 * @param height The height
 * @param bitsPerPixel The bits per pixel
 * @return The size of the frameBuffer
 * @details This is an event form RFB, initializes the frameBuffers dimensions, and also sets all the attributes
 */
int ClientScreen::iniScreen(const int width,const int height,const int bitsPerPixel) {
	if(DEBUG)
		LOGE("JNI iniFrameBuffer");

	this->width=width;
	this->height=height;
	this->bytesPerPixel=bitsPerPixel/8;//se pasa de bits a byte
	this->depth = bitsPerPixel;




	this->size = this->height * this->width * this->bytesPerPixel;//se calcula el tamaño total del buffer


	if(DEBUG)
		LOGE("FIN JNI iniFrameBuffer");

	//SetFormatAndEncodings(client);

	//notificamos la inicializacion a java
	notifyIniFrame(this->width,this->height,this->bytesPerPixel,this->depth);


	return size;
}

/**
 * @brief Updates a section of the image
 * @param frameBuffer The frameBuffer
 * @param x The initial x coordinate of the section
 * @param y The initial y coordinate of the section
 * @param w The section width
 * @param h The section height
 * @brief Updates a section of the image. This is an event from RFB. Indicates an image update in the buffer.
 */
void  ClientScreen::updateScreen(uint8_t *frameBuffer,const int x,const int y,const int w,const int h){
	if(DEBUG)
		LOGE("JNI Update");

	int pixel;
	int pos;
	int pos_col;

	int pos_array;
	/*for(int i=x;i < w;i++ ){
		pos_col = (i*screen->bytesPerPixel);
		for(int j=y;j<h;j++){
			pos = ((screen->bytesPerPixel*screen->width) * j) + pos_col;

			memcpy(&pixel,&cl->frameBuffer[pos],screen->bytesPerPixel);

			pos_array = (screen->width *j) + i;
			screen->notifyUpdateScreen(pixel,pos_array,y,w,h);
		}
	}*/
	/*int real_i,real_j;
	for(int i=0;i < w;i++ ){
		real_i = i + x;
		pos_col = (real_i*screen->bytesPerPixel);
		for(int j=0;j<h;j++){
			real_j = j + y;

			pos = ((screen->bytesPerPixel*screen->width) * real_j) + pos_col;

			memcpy(&pixel,&cl->frameBuffer[pos],screen->bytesPerPixel);

			pos_array = (screen->width *real_j) + real_i;
			screen->notifyUpdateScreen(pixel,pos_array,y,w,h);
		}
	}*/
	getBitmapData();
	jint *info = getArrayElements();
	int real_i,real_j;
	for(int i=0;i < w;i++ ){
		real_i = i + x;
		pos_col = (real_i*bytesPerPixel);
		for(int j=0;j<h;j++){
			real_j = j + y;

			pos = ((bytesPerPixel*width) * real_j) + pos_col;

			memcpy(&pixel,&frameBuffer[pos],bytesPerPixel);

			pos_array = (width *real_j) + real_i;
			//screen->notifyUpdateScreen(pixel,pos_array,y,w,h);
			info[pos_array] = pixel;
		}
	}
	if(DEBUG)
		LOGE("JNI notify reDraw");
	setArrayElements(info);
	//notifyReDraw();

}

