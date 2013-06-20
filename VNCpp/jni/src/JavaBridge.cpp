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
#include <string.h>
#include <jni.h>
#include <android/log.h>

#include <string>

#include <model/Vnc.h>
#include <CommonType.h>

using namespace std;

Vnc *vnc;///< Referencia al objecto Vnc

extern "C" {
	JNIEXPORT jint JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_iniConnect(JNIEnv* env,jobject javaThis,jstring host_java,jint port_java,jstring pass_java,
			jint quality_java,jint conpress_java,jboolean hide_mouse);
	JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_closeConnection(JNIEnv* env,jobject javaThis);
	JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_stopUpdate(JNIEnv* env,jobject javaThis);
	JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_finish(JNIEnv* env,jobject javaThis);
	JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_iniJNI(JNIEnv* env,jobject javaThis);
	JNIEXPORT jboolean JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_mouseEvent(JNIEnv* env,jobject javaThis,int x,int y,int event);
	JNIEXPORT jboolean JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_keyEvent(JNIEnv* env,jobject javaThis,int key,jboolean down);
};

/**
 * @brief Initializes the JNI
 * @param env pointer of a structure that contains the interface to the JVM
 * @param javaThis the java object observable
 * @details Initializes the JNI, creates the Vnc object and adds env and javaThis as observers
 */
JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_iniJNI(JNIEnv* env,jobject javaThis){
	vnc = new Vnc();
	vnc->addObserver(javaThis,env);
}

/**
 * @brief Initializes the connection
 * @param env pointer of a structure that contains the interface to the JVM
 * @param javaThis the java object observable
 * @param host_java the IP
 * @param port_java the port
 * @param pass_java the password
 * @param quality_java the image quality
 * @param compress_java the image has to be compress or not
 * @param hide_mouse the mouse has to be hide or not
 * @return An connection error
 * @details Calls to iniConnection method from Vnc to start the connection with the server
 */
JNIEXPORT jint JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_iniConnect(JNIEnv* env,jobject javaThis,
		jstring host_java,jint port_java,jstring pass_java,jint quality_java,jint conpress_java,jboolean hide_mouse){

	int port;
	int quality;
	int compress;

	const char* aux_host = env->GetStringUTFChars(host_java,0);
	const char* aux_pass= env->GetStringUTFChars(pass_java,0);
	port = port_java;


	quality = quality_java;
	compress = conpress_java;

	ConnectionError error = vnc->iniConnection((char*)aux_host,port,(char*)aux_pass,quality,compress,(bool)hide_mouse);


	if(DEBUG)
		LOGE("FIN INICIAR CONEXION");

	return (jint)error;
}

/**
 * @brief Closes the connection
 * @param env pointer of a structure that contains the interface to the JVM
 * @param javaThis the java object observable
 * @details Calls to closeConnection method from Vnc to stop the connection with the server
 */
JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_closeConnection(JNIEnv* env,jobject javaThis){
	vnc->closeConnection();
}

/**
 * @brief Cleans the memory
 * @param env pointer of a structure that contains the interface to the JVM
 * @param javaThis the java object observable
 * @details Destroys the Vnc object
 */
JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_finish(JNIEnv* env,jobject javaThis){
	if(DEBUG){
		LOGE("Limpiando JNI");
	}
	delete vnc;
	if(DEBUG){
		LOGE("FIN limpiando JNI");
	}
	vnc = NULL;
}

/**
 * @brief Sends a mouse event to the server
 * @param env pointer of a structure that contains the interface to the JVM
 * @param javaThis the java object observable
 * @param x the x coordinate
 * @param y the y coordinate
 * @param event the mouse event
 * @details Sends a mouse event to the server from the client. Specifies the coordinates x and y of the event and also specifies the type
 */
JNIEXPORT jboolean JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_mouseEvent(JNIEnv* env,jobject javaThis,int x,int y,int event){
	return vnc->sendMouseEvent(x,y,static_cast<MouseEvent>(event));
}

/**
 * @brief Sends a key event to the server
 * @param env pointer of a structure that contains the interface to the JVM
 * @param javaThis the java object observable
 * @param key the key
 * @param down the key is down or not
 * @details Sends a key event to the server from the client.
 */
JNIEXPORT jboolean JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_keyEvent(JNIEnv* env,jobject javaThis,int key,jboolean down){
	return vnc->sendKeyEvent(key,(bool)down);
}

/**
 * @brief Stops the updates
 * @param env pointer of a structure that contains the interface to the JVM
 * @param javaThis the java object observable
 * @details Stops the updates
 */
JNIEXPORT void JNICALL Java_es_farfuteam_vncpp_model_VncBridgeJNI_stopUpdate(JNIEnv* env,jobject javaThis){
	vnc->setUpdate(false);
}
