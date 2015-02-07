package com.prototype.whcs.seniordesignprototype;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Jimmy on 2/7/2015.
 */
public class BluetoothConnectClass extends AsyncTask<Void, Void, BluetoothSocket> {

    private BluetoothDevice device;
    // Well known SPP UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothConnectClass(BluetoothDevice device)
    {
        this.device = device;
    }

    @Override
    protected BluetoothSocket doInBackground(Void... voids) {

        try {
            return device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
