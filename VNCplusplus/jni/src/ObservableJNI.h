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

#ifndef OBSERVABLEJNI_H_
#define OBSERVABLEJNI_H_

#include <jni.h>

#include <string>

#include <CommonType.h>

using namespace std;
/**
 * @class ObservableJNI
 * @brief Clase que notifica a java.
 */
class ObservableJNI {
private:
	//JNIEnv *env;
	jobject observer_object;///< Objeto java al que seran enviadas las notificaciones
	jclass observer_class;///< Clase java de observer_object, necesaria para buscar el ID de los metodos

	jobject bitmap_object;///< Objeto java que sera modificado con la nueva informacion de pixeles
	jclass bitmap_class;///< Clase java de bitmap_object, necesaria para buscar el ID del array

	JavaVM *vm;///< Puntero a la maquina virtual java. Necesario para coger el entorno actual de ejecuccion.
	JNIEnv *env;

	jintArray bitmap_data;

	void getEnviroment();
	void getBitmapObject();

public:
	ObservableJNI();
	virtual ~ObservableJNI();

	void addObserver(jobject observer,JNIEnv *env);


	void notifyIniFrame(int width,int height,int bpp,int depth);
	void notifyReDraw(int x,int y,int width,int height);
	void notifyFinishConnection();
	void finishByClient();


	void getBitmapData();
	jint* getArrayElements();
	void setArrayElements(jint *info);




};

#endif /* OBSERVABLEJNI_H_ */
