LOCAL_PATH:= $(call my-dir)

LIB_PATH := ../lib
INCLUDE_PATH := $(LOCAL_PATH)/../include
SDL_PATH := ../SDL
#############################################
#########LIBRARIES###########################
#############################################
include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= c
LOCAL_SRC_FILES:= $(LIB_PATH)/libc.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= z
LOCAL_SRC_FILES:= $(LIB_PATH)/libz.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= nettle 
LOCAL_SRC_FILES:= $(LIB_PATH)/libnettle.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= hogweed 
LOCAL_SRC_FILES:= $(LIB_PATH)/libhogweed.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES :=
LOCAL_STATIC_LIBRARIES :=  gmp
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= gmp 
LOCAL_SRC_FILES:= $(LIB_PATH)/libgmp.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= tasn1
LOCAL_SRC_FILES:= $(LIB_PATH)/libtasn1.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= gnutls 
LOCAL_SRC_FILES:= $(LIB_PATH)/gnutls/libgnutls.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
LOCAL_STATIC_LIBRARIES := tasn1 nettle hogweed #gnutls_ext miniopencdk openpgp minitasn1 gnutls_auth gnutls_alg accelerated gnutlsxx crypto 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= gpg-error 
LOCAL_SRC_FILES:= $(LIB_PATH)/libgpg-error.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH) 
LOCAL_SHARED_LIBRARIES := 
LOCAL_STATIC_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= gcrypt 
LOCAL_SRC_FILES:= $(LIB_PATH)/libgcrypt.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
LOCAL_STATIC_LIBRARIES := gpg-error
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_LDLIBS :=
LOCAL_MODULE:= jpeg 
LOCAL_SRC_FILES:= $(LIB_PATH)/libjpeg.a
LOCAL_EXPORT_C_INCLUDES := $(INCLUDE_PATH)
LOCAL_SHARED_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS := 
LOCAL_MODULE:= vncclient 
LOCAL_SRC_FILES:= $(LIB_PATH)/libvncclient.a 
LOCAL_EXPORT_C_INCLUDES := \
	  	$(INCLUDE_PATH)\
		$(INCLUDE_PATH)/libvncclient \
		$(INCLUDE_PATH)/common \
		#$(INCLUDE_PATH)/lib #$(INCLUDE_PATH)/SDL
LOCAL_SHARED_LIBRARIES := 
include $(PREBUILT_STATIC_LIBRARY)

#############################################################################################
#############################FILE SRC########################################################
#############################################################################################

#include $(CLEAR_VARS)
#LOCAL_LDLIBS := 
#LOCAL_MODULE:= ClientConnectionRFB 
#LOCAL_SRC_FILES:= nativesrc/comunication/ClientConnectionRFB.h nativesrc/comunication/ClientConnectionRFB.cpp
#LOCAL_EXPORT_C_INCLUDES := 
#LOCAL_SHARED_LIBRARIES := 
#include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := vncmain

LOCAL_C_INCLUDES :=\
				 $(LOCAL_PATH)/include

# Add your application source files here...
LOCAL_SRC_FILES :=  \
				model/screen/ClientScreen.h \
				model/screen/ClientScreen.cpp \
				model/comunication/HandlerRFB.h \
				model/comunication/HandlerRFB.cpp \
				model/comunication/ClientConnectionRFB.h \
				model/comunication/ClientConnectionRFB.cpp \
				model/Vnc.h \
				model/Vnc.cpp \
				ObservableJNI.h \
				ObservableJNI.cpp \
				CommonType.h \
				JavaBridge.cpp

LOCAL_SHARED_LIBRARIES :=

#APP_OPTIM:= release
APP_CPPFLAGS := -fexceptions -frtti -O2 -g -Wall

LOCAL_STATIC_LIBRARIES := gnustl_static  vncclient  gcrypt  z jpeg gnutls tasn1 nettle hogweed

LOCAL_LDLIBS := -lGLESv1_CM -llog -lgcc -landroid -lstdc++

include $(BUILD_SHARED_LIBRARY)
