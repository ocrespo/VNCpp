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

#ifndef OBSERVABLEJNI_H_
#define OBSERVABLEJNI_H_

#include <jni.h>

#include <string>

#include <CommonType.h>

using namespace std;

/**
 * @class ObservableJNI
 * @brief This is the class which notifies to Java
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @details This class notifies all the changes to the Java code
 */
class ObservableJNI {
private:
	//JNIEnv *env;
	jobject observer_object;///< The Java object to which notifications will be sent
	jclass observer_class;///< The observer_object Class, it is needed for search the methods IDs

	jobject bitmap_object;///< The Java object that will be modified with the new information of pixels
	jclass bitmap_class;///< The bitmap_object Class, it is needed to search the array ID

	JavaVM *vm;///< A pointer to the JVM. It is needed to catch the current execution environment
	JNIEnv *env;///< A pointer of a structure that contains the interface to communicate with the JVM

	jintArray bitmap_data;///< The bitmap array

	void getEnviroment();
	void getBitmapObject();

public:
	ObservableJNI();
	virtual ~ObservableJNI();

	void addObserver(jobject observer,JNIEnv *env);


	void notifyIniFrame(int width,int height,int bpp,int depth);
	void notifyReDraw();
	void notifyFinishConnection();
	void finishByClient();


	void getBitmapData();
	jint* getArrayElements();
	void setArrayElements(jint *info);

	char* notifyAskPass();

};

#endif /* OBSERVABLEJNI_H_ */
