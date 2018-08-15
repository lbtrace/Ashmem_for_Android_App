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

#ifndef ASHMEMDEMO_LOCAL_LOG_H
#define ASHMEMDEMO_LOCAL_LOG_H

#include <android/log.h>

namespace ashmem_wrapper {
#undef LOG_TAG
#define LOG_TAG "ashmem_wrapper"
#define LOG_I(fmt, ...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##__VA_ARGS__);
#define LOG_E(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##__VA_ARGS__);
}
#endif //ASHMEMDEMO_LOCAL_LOG_H
