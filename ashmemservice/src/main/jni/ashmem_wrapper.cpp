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

#include <errno.h>
#include <unistd.h>
#include <sys/mman.h>
#include <stdlib.h>

namespace ashmem_wrapper {

AshmemWrapper::~AshmemWrapper() {
  ::munmap(data_, size_);
  ::close(ashmem_fd_);
}

int32_t AshmemWrapper::CreateFromParcel(int fd, int size, AshmemWrapper **out) {
  int32_t ret = EBADFD;

  LOG_I("%s %d %d %d", __FUNCTION__, __LINE__, fd, size);
  if (fd >= 0 && size > 0) {
    const int dup_ashmem_fd = ::dup(fd);
    if (dup_ashmem_fd >= 0) {
      void *data = ::mmap(NULL, size, PROT_READ, MAP_SHARED, dup_ashmem_fd, 0);
      if (data == MAP_FAILED) {
        ret = -errno;
      } else {
        AshmemWrapper *wrapper = new AshmemWrapper(dup_ashmem_fd, data, size);
        *out = wrapper;

        return EXIT_SUCCESS;
      }
    }
  }

  *out = nullptr;

  return ret;
}
} // namespace ashmem_wrapper
