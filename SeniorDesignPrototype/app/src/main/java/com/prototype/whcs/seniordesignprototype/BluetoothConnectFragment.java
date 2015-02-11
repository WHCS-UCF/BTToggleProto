package com.prototype.whcs.seniordesignprototype;

/**
 * Created by Jimmy on 2/11/2015.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

/**
 * The bluetooth connect fragment scans bluetooth devices that
 * you are paired too or optionally allows pairing of
 * new devices ( not implemented)
 * You can select one of the paired devices that you want to communicate with.
 * This brings up the LEDToggleFragment which tries to communicate with the
 * selected device.
 *
 * Maybe When the user picks a paired device from this fragment it
 * should be verified before being sent to LEDToggle.
 */
public class BluetoothConnectFragment extends Fragment {

    private ListView bluetoothListView;
    private Button showPairedButton;
    private Button searchNewButton;
    private TextView connectedStatusTV;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> btArrayAdapter;
    BluetoothSocket bSock;

    public BluetoothConnectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bluetoothconnect, container, false);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothListView = (ListView) rootView.findViewById(R.id.bluetoothListView);
        showPairedButton = (Button) rootView.findViewById(R.id.showPairedButton);
        searchNewButton = (Button) rootView.findViewById(R.id.searchNewButton);
        connectedStatusTV = (TextView) rootView.findViewById(R.id.connectedStatusTV);

        btArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        bluetoothListView.setAdapter(btArrayAdapter);

        addListeners();


        return rootView;
    }

    private void addListeners()
    {
        showPairedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list(view);
            }
        });
        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final BluetoothDevice selectedDevice = myBluetoothAdapter.getBondedDevices().toArray(new BluetoothDevice[myBluetoothAdapter.getBondedDevices().size()])[i];


                Toast.makeText(getActivity().getApplicationContext(), "You clicked " + i + " " + selectedDevice.getName(),
                        Toast.LENGTH_SHORT).show();

//                    try {
                //bSock = selectedDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                //bSock.connect();
                Fragment fg = new LEDToggleFragment(selectedDevice);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fg).commit();

//                    } catch (final IOException e) {
//                        bSock = null;
//                        e.printStackTrace();
//                        Toast.makeText(getActivity().getApplicationContext(),"fail connecting",
//                                Toast.LENGTH_SHORT).show();
//                    }


                    /*
                    * Code used for debugging to see if a connection is being made to the
                    * targetted blue tooth device. This allows us to create a connection without leaving
                    * BluetoothConnect Fragment.
                     */
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                bSock = selectedDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//                                bSock.connect();
//                                //Toast.makeText(getActivity().getApplicationContext(),bSock.isConnected() + " ",
//                                        //Toast.LENGTH_SHORT).show();
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getActivity().getApplicationContext(),"success connecting",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                            } catch (final IOException e) {
//                                try {
//                                    bSock.close();
//                                }
//                                catch (IOException e2) {
//                                    e.printStackTrace();
//                                }
//                                bSock = null;
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getActivity().getApplicationContext(),"fail connecting",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                //btArrayAdapter.add(e.toString());
//                                e.printStackTrace();
//                            }
//                        }
//                    }).run();


            }
        });
    }

    public void list(View view){
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        //Clear everything currently in the list's adapter
        btArrayAdapter.clear();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
            btArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

        Toast.makeText(getActivity().getApplicationContext(),"Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onDestroy()
    {
        if(bSock != null)
        {
            try {
                bSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bSock = null;
        }
        super.onDestroy();
    }
}
