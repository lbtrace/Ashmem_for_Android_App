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

package com.lbtrace.ashmemservice;

import android.database.sqlite.SQLiteClosable;
import android.os.MemoryFile;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.FileDescriptor;
import java.io.IOException;

/*
 * AshmemWrapper represent Ashmem between Client process and Server process.
 * In Server process, AshmemWrapper just using MemoryFile. Now Client process
 * only read Ashmem.
 */
public class AshmemWrapper implements Parcelable, IAshmemReader {
    private static final String LOG_TAG = AshmemWrapper.class.getSimpleName();
    private final MemoryFile mMemoryFile;
    private final int mAshmemSize;
    // Native AshmemWrapper Object address
    private long mNativePtr;
    private static final Parcelable.Creator<AshmemWrapper> CREATOR =
            new Parcelable.Creator<AshmemWrapper>() {
                @Override
                public AshmemWrapper createFromParcel(Parcel source) {
                    return new AshmemWrapper(source);
                }

                @Override
                public AshmemWrapper[] newArray(int size) {
                    return new AshmemWrapper[size];
                }
            };

    private static native long nativeCreateFromParcel(int fd, int size);

    private static native int nativeRead(long nativePtr, byte[] buffer, int srcOffset, int destOffset, int count);

    private static native void nativeDispose(long nativePtr);

    static {
        System.loadLibrary("ashmemwrapper");
    }

    // Server process using.
    public AshmemWrapper(String name, int length) throws IOException {
        if (length <= 0)
            throw new IllegalArgumentException("Ashmem size " + length);
        mAshmemSize = length;
        mMemoryFile = new MemoryFile(name, length);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        FileDescriptor fileDescriptor = null;

        try {
            fileDescriptor = (FileDescriptor) ReflectUtil.invokeMethod(mMemoryFile,
                    ReflectUtil.getMethod(MemoryFile.class, "getFileDescriptor"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fileDescriptor == null) {
            dest.writeInt(-1);
        } else {
            dest.writeInt(0);
            dest.writeInt(mAshmemSize);
            dest.writeFileDescriptor(fileDescriptor);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static AshmemWrapper newFromParcel(Parcel parcel) {
        return CREATOR.createFromParcel(parcel);
    }

    /**
     * Write buffer byte array to Ashmem in Server process.
     *
     * @param buffer     byte array that write to Ashmem
     * @param srcOffset  offset position in buffer
     * @param destOffset offset position in Ashmem
     * @param count      byte count to write
     * @throws IOException If Ashmem has been closed
     */
    public void writeBytes(@NonNull byte[] buffer, int srcOffset, int destOffset, int count)
            throws IOException {
        mMemoryFile.writeBytes(buffer, srcOffset, destOffset, count);
    }

    @Override
    public byte[] read() {
        final byte[] buffer = new byte[mAshmemSize];
        readBytes(buffer, 0, 0, mAshmemSize);
        return buffer;
    }

    public void closeMemoryFile() {
        mMemoryFile.close();
    }

    @Override
    public void close() {
        dispose();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mMemoryFile == null) {
                dispose();
            } else {
                closeMemoryFile();
            }
        } finally {
            super.finalize();
        }
    }

    // Client process using
    private AshmemWrapper(Parcel parcel) {
        mMemoryFile = null;

        if (parcel.readInt() != 0) {
            throw new RuntimeException("Ashmem Service create fail!");
        }
        mAshmemSize = parcel.readInt();
        mNativePtr = nativeCreateFromParcel(parcel.readFileDescriptor().getFd(), mAshmemSize);
        if (mNativePtr == 0) {
            throw new RuntimeException("Ashmem create fail!");
        }
    }

    private void dispose() {
        nativeDispose(mNativePtr);
    }

    private int readBytes(@NonNull byte[] buffer, int srcOffset, int destOffset, int count) {
        if (!(srcOffset >= 0 && srcOffset < mAshmemSize &&
                destOffset >= 0 && destOffset < buffer.length &&
                count > 0 && count + destOffset <= buffer.length &&
                count + srcOffset <= mAshmemSize)) {
            throw new IndexOutOfBoundsException();
        }

        return nativeRead(mNativePtr, buffer, srcOffset, destOffset, count);
    }
}
