/*
 * Copyright (C) 2018 lb.wang(coder.wlb@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "local_log.h"
#include "ashmem_wrapper.h"

#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include <assert.h>

namespace ashmem_wrapper {
static jlong NativeCreateFromParcel(JNIEnv *env, jobject clazz, jint fd, jint size) {
  AshmemWrapper *wrapper;
  int32_t result = AshmemWrapper::CreateFromParcel(fd, size, &wrapper);

  if (result || !wrapper) {
    LOG_E("Could not create AshmemWrapper from Parcel");
    return 0;
  }

  return reinterpret_cast<jlong>(wrapper);
}

static void NativeDispose(JNIEnv *env, jobject clazz, jlong ashmem_ptr) {
  AshmemWrapper *wrapper = reinterpret_cast<AshmemWrapper *>(ashmem_ptr);

  if (wrapper) {
    delete wrapper;
  }
}

static int NativeRead(JNIEnv *env, jobject clazz, jlong ashmem_ptr,
                      jbyteArray buffer, jint srcOffset, jint destOffset, jint count) {
  AshmemWrapper *wrapper = reinterpret_cast<AshmemWrapper *>(ashmem_ptr);

  assert(wrapper);
  env->SetByteArrayRegion(buffer, destOffset, count,
                          reinterpret_cast<jbyte *>(wrapper->GetData()) + srcOffset);
  return count;
}

static const JNINativeMethod kJniMethods[] = {
    {"nativeCreateFromParcel", "(II)J",
     (void *)NativeCreateFromParcel},
    {"nativeDispose", "(J)V", (void *)NativeDispose},
    {"nativeRead", "(J[BIII)I", (void *)NativeRead},
};

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  JNIEnv *env;

  if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
    LOG_E("%s : line %d fail", __FUNCTION__, __LINE__);
    return EXIT_FAILURE;
  }

  if (env->RegisterNatives(env->FindClass("com/lbtrace/ashmemservice/AshmemWrapper"),
                           kJniMethods, sizeof(kJniMethods) / sizeof(JNINativeMethod)) < 0) {
    LOG_E("%s : line %d fail", __FUNCTION__, __LINE__);
    return EXIT_FAILURE;
  }

  return JNI_VERSION_1_6;
}
#ifdef __cplusplus
}
#endif
}

