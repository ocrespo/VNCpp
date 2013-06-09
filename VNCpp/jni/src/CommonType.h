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

#ifndef COMMONTYPE_H_
#define COMMONTYPE_H_
/**
 * @def DEBUG
 * @brief Indica si se esta en modo debug para mostrar los logs o no.
 */
#define DEBUG 0

/**
 * @def LOG_TAG
 * @brief Etiqueta que sera mostrada en el logcat junto con el mensaje.
 */
#define  LOG_TAG    "VNC_JNI"
/**
 * @def LOGI
 * @brief Muestra un mensaje en el logcat como info.
 */
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
/**
 * @def LOGE
 * @brief Muestra un mensaje en el logcat como error.
 */
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
/**
 * @enum ConnectionError
 * @brief Indica diferentes errores que puedes sucederse al iniciar la conexion.
 */
enum ConnectionError{
			ALLOK = 0,             ///< No ha habido ningun error
			NoServerFound = 1,     ///< No se ha podido conectar con ningun servidor
			NoFrameFound = 2,      ///< No hay ninguna informacion en el frameBuffer
			errorCreateThread = 3///< No se ha podido crear el hilo para atender las peticiones de rfb
};
enum MouseEvent{
			leftClick = 1,
			wheelClick = 2,
			doubleClick = 3,
			rightClick = 4,
			wheelUp = 8,
			wheelDown = 16,
};

#endif /* COMMONTYPE_H_ */
