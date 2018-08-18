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
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lbtrace.ashmemservice.AshmemWrapper;
import com.lbtrace.ashmemservice.IAshmemReader;


/*
 * Using for communicating with Ashmem Server.
 */
public class AshmemProxy implements IAshmem {
    private static final String LOG_TAG = AshmemProxy.class.getSimpleName();
    private final IBinder mRemote;

    public AshmemProxy(IBinder remote) {
        mRemote = remote;
    }

    @Nullable
    @Override
    public IAshmemReader getAshmemReader() {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        IAshmemReader reader = null;

        try {
            data.writeInterfaceToken(DESCRIPTION);
            mRemote.transact(TRANSACTION_GET_ASHMEMREADER, data, reply, 0);
            reply.readException();
            reader = AshmemWrapper.newFromParcel(reply);

            return reader;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Remote process die");
            return reader;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public final IBinder asBinder() {
        return mRemote;
    }
}
