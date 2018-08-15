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

package lbtrace.ashmemdemo;

import com.lbtrace.ashmemservice.AshmemService;

import java.io.IOException;

/*
 * File Description
 */
public class TestAshmemService extends AshmemService {
    private final byte[] dataBuffer = new byte[] {
            (byte) 0x30, (byte) 0x80, (byte) 0x02, (byte) 0x01, (byte) 0x03,
            (byte) 0x30, (byte) 0x80, (byte) 0x06, (byte) 0x09, (byte) 0x2A,
            (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D,
            (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0xA0, (byte) 0x80,
            (byte) 0x00, (byte) 0x00};

    @Override
    public void onCreate() {
        setmAshmemSize(dataBuffer.length);
        super.onCreate();
        try {
            writeBytes(dataBuffer, 0, 0, dataBuffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
