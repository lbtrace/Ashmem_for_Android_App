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

#ifndef ASHMEMDEMO_ASHMEM_WRAPPER_H
#define ASHMEMDEMO_ASHMEM_WRAPPER_H

#include <stdint.h>

namespace ashmem_wrapper {
class AshmemWrapper {
 public:
  AshmemWrapper(int ashmem_fd, void *data, int size) : ashmem_fd_(ashmem_fd),
                                                       data_(data),
                                                       size_(size) {
  }
  ~AshmemWrapper();
  static int32_t CreateFromParcel(int fd, int size, AshmemWrapper **out);
  void *GetData() {
    return data_;
  }

 private:
  int ashmem_fd_;
  void *data_;
  int size_;
};
} // namespace ashmem_wrapper
#endif //ASHMEMDEMO_ASHMEM_WRAPPER_H
