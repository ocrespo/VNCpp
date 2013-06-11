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

#include "ClientScreen.h"

#include <android/log.h>

#define  main_red_mask 0x00ff0000
#define main_green_mask 0x0000ff00
#define main_blue_mask 0x000000ff

/**
 * Constructor por defecto.
 *
 * Inicia todos los parametros a 0 y screen apunta a la propia clase (this)
 */
ClientScreen::ClientScreen() {
	width = 0;
	height = 0;

	bytesPerPixel = 0;
	depth = 0;
	size = 0;
}
/**
 * Destructor por defecto.
 *
 * Pone screen a NULL
 */
ClientScreen::~ClientScreen() {
	if(DEBUG){
		LOGE("Limpiando screen");
	}
}
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
 * Inicializa frameBuffer.
 *
 * Evento generado por rfb, inicializa las dimensiones del frameBuffer a size y se toman el resto de
 * atributos, para almacenarlos en la clase.
 * @param client
 * @return
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
 * Actualiza la regin de la imagen especificados por el rectangulo.
 *
 * Evento generado por rfb, indica la actualizacion de la imagen en el buffer. La seccion formada por
 * el rectandulo, siendo x e y las coordenadas iniciales, y w y h la anchura y altura, es la que ha recibido
 * cambios.
 * @param cl cliente rfb que se actualiza
 * @param x coordenada x inicial del rectangulo
 * @param y coordenada y inicial del rectangulo
 * @param w anchura del rectangulo
 * @param h altura del rectangulo
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

