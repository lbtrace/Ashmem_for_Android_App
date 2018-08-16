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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lbtrace.ashmemservice.IAshmem.AshmemNative;

import java.io.IOException;

/*
 * Usually, AshmemService should run in separate process as Server.
 * You can extends AshmemService. Ashmem create in AshmemService onCreate() and
 * close in onDestory()
 */
public class AshmemService extends Service {
    private static final String LOG_TAG = AshmemService.class.getSimpleName();
    // default name
    private String mAshmemName = "App:ashmem";
    // default is 2MB
    private int mAshmemSize = 2 * 1024 * 1024;

    private AshmemWrapper mAshmem;
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mAshmem = new AshmemWrapper(mAshmemName, mAshmemSize);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Create Ashmem fail!");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AshmemNativeImpl();
    }

    @Override
    public void onDestroy() {
        mAshmem.closeMemoryFile();
        super.onDestroy();
    }

    public class AshmemNativeImpl extends AshmemNative {
        @Override
        public AshmemWrapper getAshmemReader() {
            return mAshmem;
        }
    }

    protected void writeBytes(byte[] buffer, int srcOffset, int destOffset, int count)
            throws IOException {
        mAshmem.writeBytes(buffer, srcOffset, destOffset, count);
    }

    /**
     * Set name of Ashmem. Must invoke it before onCreate().
     *
     * @param ashmemName Ashmem name
     */
    protected void setmAshmemName(String ashmemName) {
        this.mAshmemName = ashmemName;
    }

    /**
     * Set size of Ashmem. Must invoke it before onCreate().
     *
     * @param ashmemSize Ashmem size
     */
    protected void setmAshmemSize(int ashmemSize) {
        this.mAshmemSize = ashmemSize;
    }
}
