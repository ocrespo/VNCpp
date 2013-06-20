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

#ifndef COMMONTYPE_H_
#define COMMONTYPE_H_
/**
 * @def DEBUG
 * @brief Indicates if it is debug mode to show the logs or not
 */
#define DEBUG 0
/**
 * @def LOG_TAG
 * @brief Tag for the logcat
 */
#define  LOG_TAG    "VNC_JNI"
/**
 * @def LOGI
 * @brief Shows the message as info in the logcat
 */
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
/**
 * @def LOGE
 * @brief Shows the message as error in the logcat
 */
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
/**
 * @enum ConnectionError
 * @brief Indicates the different errors that you can get when you start the connection
 */
enum ConnectionError{
			ALLOK = 0,             ///< No error
			NoServerFound = 1,     ///< No server found error
			NoFrameFound = 2,      ///< No frameBuffer information error
			errorCreateThread = 3///< Could not be created the thread to handle the request from RFB
};
/**
 * @enum MouseEvent
 * @brief Indicates the different mouse event
 */
enum MouseEvent{
			leftClick = 1,  //!< leftClick
			wheelClick = 2, //!< wheelClick
			doubleClick = 3,//!< doubleClick
			rightClick = 4, //!< rightClick
			wheelUp = 8,    //!< wheelUp
			wheelDown = 16, //!< wheelDown
};

#endif /* COMMONTYPE_H_ */
