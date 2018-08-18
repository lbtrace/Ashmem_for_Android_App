/*
 * Copyright (C) 2018 lbtrace(coder.wlb@gmail.com)
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

package com.lbtrace.ashmemservice.IAshmem;

import android.os.IBinder;
import android.os.IInterface;

import com.lbtrace.ashmemservice.AshmemWrapper;
import com.lbtrace.ashmemservice.IAshmemReader;

/*
 * API for communicating with Ashmem Server process.
 */
public interface IAshmem extends IInterface {
    static final String DESCRIPTION = "IAshmem";
    static final int TRANSACTION_GET_ASHMEMREADER = IBinder.FIRST_CALL_TRANSACTION;

    /**
     * Get AshmemWrapper for access Ashmem in Ashmem Client.
     *
     * @return AshmemWrapper
     */
    IAshmemReader getAshmemReader();
}
