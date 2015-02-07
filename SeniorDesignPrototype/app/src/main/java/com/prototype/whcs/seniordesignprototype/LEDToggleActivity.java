package com.prototype.whcs.seniordesignprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class LEDToggleActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    // Well known SPP UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter myBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledtoggle);


        if (savedInstanceState == null) {

            myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(myBluetoothAdapter == null) {

                Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                        Toast.LENGTH_LONG).show();
            }
            else {
                if(!myBluetoothAdapter.isEnabled())
                {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new EnableBluetoothFragment())
                            .commit();
                }
                else
                {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new BluetoothConnectFragment())
                            .commit();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ledtoggle, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Options include
    * "ledtoggle"
    * "bluetoothconnect"
    * "bluetoothenable"
     */
    private void changeFragment(String fragmentName)
    {
        Fragment fg;

        switch(fragmentName)
        {
            case "ledtoggle":
                fg = new LEDToggleFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.container, fg).commit();
                break;
            case "bluetoothconnect":
                fg = new BluetoothConnectFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.container, fg).commit();
                break;
            case "bluetoothenable":
                    break;
        }
    };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LEDToggleFragment extends Fragment {

        public LEDToggleFragment() {
        }

        public LEDToggleFragment(BluetoothDevice pairedDevice)
        {
            this.pairedDevice = pairedDevice;
            try {
                btSock = pairedDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                btSock.connect();
                oStream = btSock.getOutputStream();
                iSR = new InputStreamReader(btSock.getInputStream());
            } catch (IOException e) {
                Toast.makeText(getActivity(),"Couldn't get output stream", Toast.LENGTH_SHORT);
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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BluetoothConnectFragment extends Fragment {

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


                    Toast.makeText(getActivity().getApplicationContext(),"You clicked " + i + " " + selectedDevice.getName(),
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EnableBluetoothFragment extends Fragment {

        private Button enableButton;
        private BluetoothAdapter myBluetoothAdapter;

        public EnableBluetoothFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_enablebluetooth, container, false);

            myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            enableButton = (Button) rootView.findViewById(R.id.enableButton);
            addListeners();

            return rootView;
        }

        private void addListeners()
        {
            this.enableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enableBluetooth();
                }
            });
        }

        private void enableBluetooth()
        {
            if (!myBluetoothAdapter.isEnabled()) {
                Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

                Toast.makeText(this.getActivity().getApplicationContext(),"Bluetooth turned on" ,
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this.getActivity().getApplicationContext(),"Bluetooth is already on",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            if(requestCode == REQUEST_ENABLE_BT){
                if(myBluetoothAdapter.isEnabled()) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new BluetoothConnectFragment()).commit();
                }
            }
        }
    }
}
