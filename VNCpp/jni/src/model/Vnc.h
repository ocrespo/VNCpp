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

#ifndef VNC_H_
#define VNC_H_

using namespace std;

#include <jni.h>
#include <android/log.h>

#include <CommonType.h>

/**
 * @class Vnc
 * @brief Clase encargada de enlazar la conexion, hecha en la clase ClientConnectionRFB
 * con la clase encargada de convertir la imagen y gestionar eventos de rfb
 */
class ClientConnectionRFB;
class ClientScreen;
class Vnc {
private:
	ClientConnectionRFB *rfb; ///< referencia a ClientConnectionRfb, se encarga de gestionar la conexion rfb
	ClientScreen *screen;///< referencia a ClientScreen, se encarga de gestion los eventos rfb y tratar la imagen

public:
	Vnc();
	virtual ~Vnc();

	ConnectionError iniConnection(char* host,int port,char *pass,int quality,int compress,bool hide_mouse);
	void closeConnection();

	void addObserver(jobject observer,JNIEnv *env);
	//void rfbLoop(JNIEnv* env);

	bool sendMouseEvent(int x,int y,MouseEvent event);
	bool sendKeyEvent(int key,bool down);


};

#endif /* VNC_H_ */
