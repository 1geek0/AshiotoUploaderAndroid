package xyz.ashioto.ashioto;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.ashioto.ashioto.retrofitClasses.GatesListGate;

public class HomeActivity extends AppCompatActivity {
    //Bluetooth variable declaration
    BluetoothSPP bluetoothSPP = new BluetoothSPP(HomeActivity.this);


    @BindView(R.id.bluetooth_list) //RecyclerView for bluetooth list
    RecyclerView bluetooth_list;

    @BindView(R.id.bluetooth_list_progressbar)
    ProgressBar bluetooth_list_progressbar;

    RecyclerView.Adapter bluetooth_list_adapter; //Adapter for bluetooth list

    //Arraylist for HC devices
    ArrayList<String> HCDeviceNameList = new ArrayList<>();
    ArrayList<String> HCDeviceAddressList = new ArrayList<>();

    //Set containing bonded devices
    Set<BluetoothDevice> BondedDeviceSet;

    ArrayList<GatesListGate> gatesListGates = new ArrayList<>();

    SharedPreferences sharedPreferences;
    Handler taskHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("sharedprefs", MODE_PRIVATE);
        // If auth-type is not set, redirect to Login activity
        if (Objects.equals(sharedPreferences.getString("auth-type", "na"), "na")){
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // No Need to add flags to intent
        }
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Enable bluetooth if not enabled already
        if (!bluetoothSPP.isBluetoothEnabled()){
            bluetoothSPP.enable();
        }
        /*Run bluetooth setup methods after 5 seconds of enabling bluetooth adapter.
        *Note: This is not a good way to do it
        * TODO 19/11/16: Find a better way to make sure bluetooth is access only after making sure it is turned on
        */
        taskHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpBluetoothList();
                setUpBluetoothAdapters();
            }
        },5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothSPP.getBluetoothAdapter().disable(); //Disable bluetooth once the app is closed
    }


    private void setUpBluetoothList(){ //Populates device list
        HCDeviceNameList.clear(); //Clear the list before starting to avoid repetitive list
        BondedDeviceSet = bluetoothSPP.getBluetoothAdapter().getBondedDevices();
        if (BondedDeviceSet.size() > 0){
            for (BluetoothDevice currentDevice: BondedDeviceSet){
                if (currentDevice.getName().startsWith("HC")){
                    HCDeviceNameList.add(currentDevice.getName());//Add device name to list for RecyclerView
                    HCDeviceAddressList.add(currentDevice.getAddress()); //Add device address for connection
                }
            }
        }
        if (bluetoothSPP.isDiscovery()){
            bluetoothSPP.cancelDiscovery();
        }
        bluetooth_list_progressbar.setVisibility(View.GONE);
        bluetoothSPP.startDiscovery(); //Start device discovery
    }

    private void setUpBluetoothAdapters(){ // Sets Listeners and adapters
        bluetooth_list.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        bluetooth_list.addOnItemTouchListener(new RecyclerItemClickListener(HomeActivity.this, new RecyclerItemClickListener.SimpleOnItemClickListener() {
            @Override
            public void onItemClick(AppCompatTextView childView, int position) {
                super.onItemClick(childView, position);
                Intent bluetoothConnectIntent = new Intent(HomeActivity.this, BluetoothActivity.class);
                bluetoothConnectIntent.putExtra("device-address", HCDeviceAddressList.get(position));
                startActivity(bluetoothConnectIntent);
            }
        }));
        bluetooth_list_adapter = new RecyclerAdapter_bluetooth(HCDeviceNameList); //Feed device names to RecyclerView
        bluetooth_list.setAdapter(bluetooth_list_adapter);
    }
}
