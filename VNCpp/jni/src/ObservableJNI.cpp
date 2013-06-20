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

#include "ObservableJNI.h"

#include <android/log.h>

/**
 * @def CLASS_PATH
 * @brief The Java path which the Classes are located
 */
#define CLASS_OBSERVABLE_PATH "es/farfuteam/vncpp/model/VncBridgeJNI"
#define CLASS_BITMAP_PATH "es/farfuteam/vncpp/model/Screen"

/**
 * @brief The default constructor
 * @details Do not initializes anything
 */
ObservableJNI::ObservableJNI() {
	bitmap_object = NULL;
	bitmap_data = NULL;
}

/**
 * @brief The default destroyer
 * @details Frees the global reference to the observer_object
 */
ObservableJNI::~ObservableJNI() {

	if(DEBUG){
		LOGE("Limpiando ObservableJNI");
	}

	//Tomamos el entorno (env) del hilo actual
	getEnviroment();

	env->DeleteGlobalRef(observer_object);
	env->DeleteGlobalRef(bitmap_object);
	env->DeleteGlobalRef(bitmap_data);

	//vm->DetachCurrentThread();
	//vm = NULL;

}

/**
 * @brief Adds the Java object that will receive the notifications
 * @param observer The Java object to which notifications will be sent
 * @param env A pointer of a structure that contains the interface to communicate with the JVM
 * @details The observer parameter becomes a global variable to survive the JNI environment changes.
 * Also the JVM reference is taken
 */
void ObservableJNI::addObserver(jobject observer,JNIEnv *env){


	//vm->AttachCurrentThread(&env,NULL);



	this->observer_object = env->NewGlobalRef(observer);
	env->DeleteLocalRef(observer);
	//observer_class  = env->GetObjectClass(this->observer_object);

	int error = env->GetJavaVM(&vm);
	if(error != 0){
		LOGE("Error take JavaVM");
	}

}

/**
 * @brief Places the Java runtime environment to the current thread
 * @return A pointer to the current runtime environment
 * @details Places the Java runtime environment to the current thread
 */
void ObservableJNI::getEnviroment(){
	int getEnv = vm->GetEnv((void**)&env,JNI_VERSION_1_6);

	if(getEnv == JNI_EDETACHED){
		vm->AttachCurrentThread(&env,NULL);
	}



}

/**
 * @brief Invokes the observer method updateIniFrame
 * @param width The image width
 * @param height The image height
 * @param bpp Bytes per pixel
 * @param depth Depth (bits per pixel)
 * @details Calls to the observer method updateIniFrame
 */
void ObservableJNI::notifyIniFrame(int width,int height,int bpp,int depth){

	getEnviroment();
	if(DEBUG)
		LOGE("Take method iniFrame");
	//env->MonitorEnter(observer_object);
	observer_class  = env->GetObjectClass(this->observer_object);
	//cogemos el ID del metodo a invocar
	jmethodID updateScreen = env->GetMethodID(observer_class,"updateIniFrame","(IIII)V");
	if(DEBUG)
		LOGE("Launch method iniFrame");



	env->CallVoidMethod(observer_object,updateScreen,width,height,bpp,depth);

	if(DEBUG)
		LOGE("Finish launch method iniFrame");
	//env = NULL;
	env->DeleteLocalRef(observer_class);
	//vm->DetachCurrentThread();
}

/**
 * @brief Notifies a redraw of the image
 * @details Calls to the observer method updateReDraw
 */
void ObservableJNI::notifyReDraw(){
	if(DEBUG)
		LOGE("Take method reDraw");

	getEnviroment();
	//env->MonitorEnter(observer_object);
	observer_class  = env->GetObjectClass(this->observer_object);

	jmethodID updateScreen = env->GetMethodID(observer_class,"updateReDraw","()V");

	if(DEBUG)
		LOGE("Launch method reDraw");

	env->CallVoidMethod(observer_object,updateScreen);

	if(DEBUG)
		LOGE("Finish launch method reDraw");

	env->DeleteLocalRef(observer_class);
	//env = NULL;
	//vm->DetachCurrentThread();
}

/**
 * @brief Notifies the end of the connection
 * @details Calls to the observer method updateFinishConnection
 */
void ObservableJNI::notifyFinishConnection(){
	getEnviroment();
	observer_class  = env->GetObjectClass(this->observer_object);
	jmethodID updateFinishConnection = env->GetMethodID(observer_class,"updateFinishConnection","()V");

	if(DEBUG)
		LOGE("Launch method updateFinishConnection");

	env->CallVoidMethod(observer_object,updateFinishConnection);

	if(DEBUG)
		LOGE("Finish launch method updateFinishConnection");
	//env = NULL;

	env->DeleteLocalRef(observer_class);

	int error = vm->GetEnv((void**)&env,JNI_VERSION_1_6);
	if(error == JNI_OK){
		if(DEBUG)
			LOGE("Eliminado hilo");
		vm->DetachCurrentThread();
	}

}

/**
 * @brief Finishes the current thread
 * @details Finishes the current thread
 */
void ObservableJNI::finishByClient(){
	if(DEBUG)
		LOGE("Fin por usuario");
	getEnviroment();

	if(vm->GetEnv((void**)&env,JNI_VERSION_1_6) == JNI_OK){
		if(DEBUG)
			LOGE("Eliminado hilo");
		vm->DetachCurrentThread();

	}
}

/**
 * @brief Gets the current bitmap object from the observer
 * @details Gets the current bitmap object from the observer
 */
void ObservableJNI::getBitmapObject(){
	if(bitmap_object == NULL){

		if(DEBUG)
			LOGE("Take bitmap_class getBitmapObject");

		//bitmap_class = env->FindClass(CLASS_BITMAP_PATH);
		observer_class  = env->GetObjectClass(this->observer_object);
		//cogemos el id del atributo screen
		if(DEBUG)
			LOGE("Take screen_fieldID getBitmapObject");
		jfieldID screen_fieldID = env->GetFieldID(observer_class,"screen","Les/farfuteam/vncpp/model/Screen;");
		//cogemos el objecto screen
		if(DEBUG)
			LOGE("Take aux_bitmap_object getBitmapObject");
		jobject aux_bitmap_object = env->GetObjectField(observer_object,screen_fieldID);

		//bitmap_object = aux_bitmap_object;
		bitmap_object = env->NewGlobalRef(aux_bitmap_object);
		env->DeleteLocalRef(aux_bitmap_object);
		env->DeleteLocalRef(observer_class);
	}
	//jclass bitmap_class = env->GetObjectClass(CLASS_BITMAP_PATH);

}

/**
 * @brief Gets the current bitmap data
 * @details Gets the current bitmap data
 */
void  ObservableJNI::getBitmapData(){
	getEnviroment();
	if(bitmap_data != NULL){
		return;
	}
	getBitmapObject();
	if(DEBUG)
		LOGE("Take dataID getBitmapData");

	bitmap_class = env->GetObjectClass(bitmap_object);
	if(bitmap_class == NULL){
		LOGE("ES NULL");
	}
	jfieldID dataID = env->GetFieldID(bitmap_class,"data","[I");

	if(DEBUG)
		LOGE("Take jintArray getBitmapData");

	//bitmap_data = (jintArray)env->NewGlobalRef(env->GetObjectField(bitmap_object,dataID));
	bitmap_data = (jintArray)env->NewGlobalRef(env->GetObjectField(bitmap_object,dataID));

	env->DeleteLocalRef(bitmap_class);

	//return bitmap_data;

}

/**
 * @brief Gets the elements of the bitmap data
 * @return The elements of the bitmap data
 * @details Gets the elements of the bitmap data
 */
jint* ObservableJNI::getArrayElements(){

	jint *info = env->GetIntArrayElements(bitmap_data,0);

	return info;
}

/**
 * @brief Sets the bitmap data elements
 * @param info the elements
 * @details Sets the bitmap data elements
 */
void ObservableJNI::setArrayElements(jint *info){

	env->ReleaseIntArrayElements(bitmap_data,info,0);

	//env->DeleteLocalRef(bitmap_data);

	//env->DeleteGlobalRef(bitmap_object);
	//bitmap_object = NULL;
	//bitmap_data = NULL;


	//env = NULL;
}

/**
 * @brief Notifies a password request
 * @return the password
 * @details class to the observer method updateAskPass
 */
char* ObservableJNI::notifyAskPass(){
	getEnviroment();
	observer_class  = env->GetObjectClass(this->observer_object);
	jmethodID updateAskPass = env->GetMethodID(observer_class,"updateAskPass","()Ljava/lang/String;");

	jstring aux_pass = (jstring)env->CallObjectMethod(observer_object,updateAskPass);

	if(DEBUG)
		LOGE("Finish launch method reDraw");

	 env->DeleteLocalRef(observer_class);

	const char* pass = env->GetStringUTFChars(aux_pass,0);
	return (char*)pass;
}
