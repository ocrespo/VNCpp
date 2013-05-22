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

#include "ObservableJNI.h"

#include <android/log.h>

/**
 * @def CLASS_PATH
 * @brief Ruta java en el que se encuentra la clase.
 */
#define CLASS_OBSERVABLE_PATH "es/farfuteam/vncplusplus/model/VncBridgeJNI"
#define CLASS_BITMAP_PATH "es/farfuteam/vncplusplus/model/Screen"


/**
 * Constructor por efecto, no inicializa nada
 */
ObservableJNI::ObservableJNI() {
	bitmap_object = NULL;
	bitmap_data = NULL;
}
/**
 * Destructor por defecto.
 *
 *Elimina la referencia global de observer_object
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
 * Añade el objecto java que recibira las notificaciones.
 *
 * Observer se transforma en un variabel global, para que perdure a los cambios de entorno de jni.
 * Ademas se toma la referencia de la maquina virtual de java (javaVM).
 * @param observer Objecto java que recibira las notificaciones.
 * @param env Entorno actual en el que se hace la llamada, necesario para coger la referencia a javaVM.
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
 * Coloca el entorno de ejecucion de java al hilo actual
 *
 * @return puntero al entorno actual de ejecucion
 */
void ObservableJNI::getEnviroment(){
	int getEnv = vm->GetEnv((void**)&env,JNI_VERSION_1_6);

	if(getEnv == JNI_EDETACHED){
		vm->AttachCurrentThread(&env,NULL);
	}



}
/**
 * Invoca el metodo updateIniFrame del observer
 * @param width anchura de la imagen
 * @param height altura de la imagen
 * @param bpp Bytes por pixel
 * @param depth Profundidad (bits por pixel)
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

void ObservableJNI::notifyReDraw(int x,int y,int width,int height){
	if(DEBUG)
		LOGE("Take method reDraw");

	getEnviroment();
	//env->MonitorEnter(observer_object);
	observer_class  = env->GetObjectClass(this->observer_object);

	jmethodID updateScreen = env->GetMethodID(observer_class,"updateReDraw","(IIII)V");

	if(DEBUG)
		LOGE("Launch method reDraw");

	env->CallVoidMethod(observer_object,updateScreen,x,y,width,height);

	if(DEBUG)
		LOGE("Finish launch method reDraw");

	env->DeleteLocalRef(observer_class);
	//env = NULL;
	//vm->DetachCurrentThread();
}
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
void ObservableJNI::getBitmapObject(){
	if(bitmap_object == NULL){

		if(DEBUG)
			LOGE("Take bitmap_class getBitmapObject");

		//bitmap_class = env->FindClass(CLASS_BITMAP_PATH);
		observer_class  = env->GetObjectClass(this->observer_object);
		//cogemos el id del atributo screen
		if(DEBUG)
			LOGE("Take screen_fieldID getBitmapObject");
		jfieldID screen_fieldID = env->GetFieldID(observer_class,"screen","Les/farfuteam/vncplusplus/model/Screen;");
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
jint* ObservableJNI::getArrayElements(){

	jint *info = env->GetIntArrayElements(bitmap_data,0);

	return info;
}
void ObservableJNI::setArrayElements(jint *info){

	env->ReleaseIntArrayElements(bitmap_data,info,0);

	//env->DeleteLocalRef(bitmap_data);

	//env->DeleteGlobalRef(bitmap_object);
	//bitmap_object = NULL;
	//bitmap_data = NULL;


	//env = NULL;
}
