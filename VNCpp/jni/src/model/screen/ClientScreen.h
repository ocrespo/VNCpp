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
#ifndef CLIENTSCREENSDL_H_
#define CLIENTSCREENSDL_H_

using namespace std;

#include <jni.h>

#include <rfb/rfbclient.h>

#include <ObservableJNI.h>
#include <CommonType.h>

/**
 * @class ClientScreen
 * @brief This Class handles the RFB events and the image
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends ObservableJNI
 * @details This Class handles the RFB events and the image. Extends ObservableJNI in order to report changes to Java
 */
class ClientScreen : public  ObservableJNI {
private:
	int width;///< The image width
	int height;///< The image height

	int bytesPerPixel;///< The bytes per pixel
	int size;///< The size of the frameBuffer, it is height*width*bytersPerPixel
	int depth;///< The image depth, it is equal to bitsPerPixel

public:
	ClientScreen();
	virtual ~ClientScreen();

	void doMask(rfbClient *client);

	int iniScreen(const int width,const int height,const int bitsPerPixel);
	void updateScreen(uint8_t *frameBuffer,const int x,const int y,const int w,const int h);
};

#endif /* CLIENTSCREENSDL_H_ */
