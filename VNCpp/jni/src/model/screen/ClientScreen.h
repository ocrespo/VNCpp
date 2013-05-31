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

#ifndef CLIENTSCREENSDL_H_
#define CLIENTSCREENSDL_H_

using namespace std;

#include <jni.h>

#include <rfb/rfbclient.h>

#include <ObservableJNI.h>
#include <CommonType.h>

/**
 * @class ClientScreen
 * @brief Clase encargada de gestionar los eventos de rfb y transformar la imagen.
 *
 * Clase encargada de gestionar los eventos de rfb y transformar la imagen. Hereda la clase ObservableJNI
 * para asi poder notificar cambios a Java
 */
class ClientScreen : public  ObservableJNI {
private:
	int width;///< Anchura dela imagen
	int height;///< altura de la imagen

	int bytesPerPixel;///< bytes por pixel
	int size;///< tamaño del frameBuffer, se calcula con height*width*bytesPerPixel
	int depth;///< profundidad de la imagen, es igual que los bitsPerPixel


/*	static ClientScreen *screen;/**< Puntero estatico a la propia clase, necesario para acceder a los atributos
								en los metodos estaticos. Los metodos estaticos son los que son invocados
								por los eventos de rfb, deben ser estatico porque son punteros a funciones
								y es la forma de eliminar el parametro this del metodo.
								*/
	//static char *sdlPixels;
public:
	ClientScreen();
	virtual ~ClientScreen();

	void doMask(rfbClient *client);

	int iniScreen(const int width,const int height,const int bitsPerPixel);
	void updateScreen(uint8_t *frameBuffer,const int x,const int y,const int w,const int h);
};

#endif /* CLIENTSCREENSDL_H_ */
