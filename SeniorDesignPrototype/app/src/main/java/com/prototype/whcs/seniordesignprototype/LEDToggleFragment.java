package com.prototype.whcs.seniordesignprototype;

/**
 * Created by Jimmy on 2/11/2015.
 */

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

/**
 * The LEDToggleFragment has a button that should toggle the LED attached to the ATMEGA88.
 *
 * When the fragment is created it checks to see if it was created with a
 * Bluetooth Device selected. If it wasn't it swaps itself with the bluetooth connect fragment.
 *
 * Once the LEDToggle fragment knows it has a target device it attempts to connect to it
 * It then opens input and output stream.]
 *
 * Pressing the toggle button sends either 'A' or 'B' to the bluetooth module.
 * 'A' for on 'B' for off.
 * Then the app reads whatever the bluetooth module sent back to it. BLOCKING CALL.
 * It makes a TOAST with this data and displays it to the user.
 *
 * It is important to close the input and output stream whenever leaving the app.
 * These resources tie up communication with the bluetooth module.
 */
public class LEDToggleFragment extends Fragment {

    // Well known SPP UUID
    //This SPP is a property of the bluetooth module.
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public LEDToggleFragment() {
    }

    @SuppressLint("ValidFragment")
    public LEDToggleFragment(BluetoothDevice pairedDevice)
    {
        this.pairedDevice = pairedDevice;
        try {
            btSock = pairedDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            btSock.connect();
            oStream = btSock.getOutputStream();
            iSR = new InputStreamReader(btSock.getInputStream());
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Couldn't get output stream", Toast.LENGTH_SHORT);
        }
    }

    private Button toggleButton;
    private BluetoothSocket btSock;
    private BluetoothDevice pairedDevice;
    private TextView btTextView;
    private OutputStream oStream;
    private InputStream iStream;
    private InputStreamReader iSR;
    private boolean toggleStateOn = true;
    private char buffer[] = new char[64];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ledtoggle, container, false);

        if(pairedDevice == null)
        {
            replaceWithConnectFragment();
        }

        toggleButton = (Button) rootView.findViewById(R.id.toggleButton);
        btTextView = (TextView) rootView.findViewById(R.id.btTextView);
        setupUI();

        return rootView;
    }

    private void replaceWithConnectFragment()
    {
        Fragment fg = new BluetoothConnectFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fg).commit();
    }

    private void setupUI()
    {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(toggleStateOn == true) {
                        oStream.write(new byte[]{'B'});
                        oStream.flush();
                        toggleStateOn = !toggleStateOn;
                        while(!iSR.ready());
                        iSR.read(buffer, 0,1);
                        Toast.makeText(getActivity().getApplicationContext(),buffer[0]+ " ",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        oStream.write(new byte[]{'A'});
                        oStream.flush();
                        toggleStateOn = !toggleStateOn;
                        while(!iSR.ready());
                        iSR.read(buffer, 0,1);
                        Toast.makeText(getActivity().getApplicationContext(),buffer[0]+ " ",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStop()
    {
        if(btSock != null)
        {
            try {
                btSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btSock = null;
        }
        if(oStream != null)
        {
            try {
                oStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            oStream = null;
        }
        if(iStream != null)
        {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            iStream = null;
        }
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(pairedDevice == null)
        {
            replaceWithConnectFragment();
        }
    }
}
