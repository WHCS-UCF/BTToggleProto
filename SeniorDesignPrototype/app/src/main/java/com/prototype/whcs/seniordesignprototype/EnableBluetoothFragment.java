package com.prototype.whcs.seniordesignprototype;

/**
 * Created by Jimmy on 2/11/2015.
 */

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * The EnableBluetoothFragment is designed to appear when the
 * app is entered with Bluetooth currently disabled.
 * Once the user activates bluetooth with the button in the fragment
 * The fragment changes to the BluetoothConnect Fragment.
 */
public class EnableBluetoothFragment extends Fragment {

    private Button enableButton;
    private BluetoothAdapter myBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

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

            Toast.makeText(this.getActivity().getApplicationContext(), "Bluetooth turned on",
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