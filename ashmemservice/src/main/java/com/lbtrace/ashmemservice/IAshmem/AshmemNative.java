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

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lbtrace.ashmemservice.AshmemWrapper;
import com.lbtrace.ashmemservice.IAshmemReader;

/*
 * File Description
 */
public abstract class AshmemNative extends Binder implements IAshmem {
    private static final String LOG_TAG = AshmemNative.class.getSimpleName();

    public AshmemNative() {
        attachInterface(this, DESCRIPTION);
    }

    public static IAshmem asInterface(IBinder binder) {
        if (binder == null) {
            return null;
        }

        IAshmem in = (IAshmem) binder.queryLocalInterface(DESCRIPTION);
        if (in != null) {
            return in;
        }

        return new AshmemProxy(binder);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case TRANSACTION_GET_ASHMEMREADER:
                data.enforceInterface(DESCRIPTION);

                AshmemWrapper ashmem = (AshmemWrapper) getAshmemReader();
                reply.writeNoException();
                if (ashmem == null) {
                    reply.writeInt(-1);
                } else {
                    ashmem.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                }

                return true;
            default:
                break;
        }

        return super.onTransact(code, data, reply, flags);
    }
}
