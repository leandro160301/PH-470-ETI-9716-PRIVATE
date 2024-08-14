LOCAL_PATH:= $(call my-dir)

#######################################
# verity_key
include $(CLEAR_VARS)

LOCAL_MODULE := verity_key
LOCAL_SRC_FILES := $(LOCAL_MODULE)
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_ROOT_OUT)
LOCAL_SANITIZE:=unsigned-integer-overflow signed-integer-overflow
LOCAL_SANITIZE_DIAG:=unsigned-integer-overflow signed-integer-overflow

include $(BUILD_PREBUILT)
