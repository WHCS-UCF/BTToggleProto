package com.prototype.whcs.seniordesignprototype;

import android.annotation.SuppressLint;
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
    //This SPP is a property of the bluetooth module.
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

            /*
            * SupportFragmentManagers are used to switch between SupportFragments (not the same as reg. fragments
            * because compatible with older APIs). You do the switching within a transaction. Common transactions
            * are adding a new fragment to the activity, replacing the fragment, adding fragment to the back stack.
             */
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


}
