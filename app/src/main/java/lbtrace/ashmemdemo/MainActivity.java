package lbtrace.ashmemdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lbtrace.ashmemservice.IAshmem.AshmemNative;
import com.lbtrace.ashmemservice.IAshmem.IAshmem;
import com.lbtrace.ashmemservice.IAshmemReader;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "TestAshmemDemo";
    private ServiceConnection mAshmemSC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAshmemSC = new AshmemServiceConnection();
        bindService(new Intent(this, TestAshmemService.class),
                mAshmemSC, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mAshmemSC);
        super.onDestroy();
    }

    private static class AshmemServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IAshmem ashmemProxy = AshmemNative.asInterface(service);
            IAshmemReader ashmemReader = ashmemProxy.getAshmemReader();
            if (ashmemReader != null) {
                byte[] result = ashmemReader.read();
                StringBuilder stringBuilder = new StringBuilder();

                for (byte value : result) {
                    stringBuilder.append(value).append(" ");
                }
                Log.i(LOG_TAG, stringBuilder.toString());
                ashmemReader.close();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

}
