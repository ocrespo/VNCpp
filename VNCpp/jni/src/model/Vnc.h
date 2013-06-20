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

#ifndef VNC_H_
#define VNC_H_

using namespace std;

#include <jni.h>
#include <android/log.h>

#include <CommonType.h>

/**
 * @class Vnc
 * @brief This Class is delegate to link the connection
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @details This Class is delegate to link the connection.
 */
class ClientConnectionRFB;
class ClientScreen;
class Vnc {
private:
	ClientConnectionRFB *rfb; ///< Reference to ClientConnection RFB, it manages the RFB connection
	ClientScreen *screen;///< Reference to ClientScreen, it handles the RFB events and also manage the image

public:
	Vnc();
	virtual ~Vnc();

	ConnectionError iniConnection(char* host,int port,char *pass,int quality,int compress,bool hide_mouse);
	void closeConnection();

	void addObserver(jobject observer,JNIEnv *env);
	//void rfbLoop(JNIEnv* env);

	bool sendMouseEvent(int x,int y,MouseEvent event);
	bool sendKeyEvent(int key,bool down);

	void setUpdate(bool update);

};

#endif /* VNC_H_ */
