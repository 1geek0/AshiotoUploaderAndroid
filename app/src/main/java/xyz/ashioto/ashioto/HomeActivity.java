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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.ashioto.ashioto.retrofitClasses.GatesListGate;
import xyz.ashioto.ashioto.retrofitClasses.GatesListResponse;

public class HomeActivity extends AppCompatActivity {
    //Bluetooth variable declaration
    BluetoothSPP bluetoothSPP = new BluetoothSPP(HomeActivity.this);


    @BindView(R.id.bluetooth_list) //RecyclerView for bluetooth list
    RecyclerView bluetooth_list;

    @BindView(R.id.bluetooth_list_progressbar)
    ProgressBar bluetooth_list_progressbar;

    @BindView(R.id.home_toolbar)
    Toolbar home_toolbar;

    RecyclerView.Adapter bluetooth_list_adapter; //Adapter for bluetooth list

    //Arraylist for HC devices
    ArrayList<String> HCDeviceNameList = new ArrayList<>();
    ArrayList<String> HCDeviceAddressList = new ArrayList<>();

    //Set containing bonded devices
    Set<BluetoothDevice> BondedDeviceSet;

    ArrayList<String> gatesListGates = new ArrayList<>();

    SharedPreferences sharedPreferences;
    Handler taskHandler = new Handler();
    Callback<GatesListResponse> gatesListResponseCallback = new Callback<GatesListResponse>() {
        @Override
        public void onResponse(Response<GatesListResponse> response, Retrofit retrofit) {
            gatesListGates.clear();
            for (GatesListGate listGate : response.body().Gates) {
                gatesListGates.add(listGate.getName());
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(HomeActivity.this, "Couldn't Fetch Gates", Toast.LENGTH_SHORT).show();
        }
    };
    private String event;

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
        setSupportActionBar(home_toolbar);
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

        event = sharedPreferences.getString("current_event", "na");
        getEventGates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothSPP.getBluetoothAdapter().disable(); //Disable bluetooth once the app is closed
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                sharedPreferences.edit().remove("current_event").commit();
                sharedPreferences.edit().remove("auth-type").commit();
                finish();
                return true;
        }
        return true;
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
                bluetoothConnectIntent.putStringArrayListExtra("event_gates", gatesListGates);
                startActivity(bluetoothConnectIntent);
            }
        }));
        bluetooth_list_adapter = new RecyclerAdapter_bluetooth(HCDeviceNameList); //Feed device names to RecyclerView
        bluetooth_list.setAdapter(bluetooth_list_adapter);
    }

    private void getEventGates() {
        if (!event.equals("na")) {
            Call<GatesListResponse> gatesListResponseCall = ApplicationClass.getRetrofitInterface().getGates(event);
            gatesListResponseCall.enqueue(gatesListResponseCallback);
        } else {
            Toast.makeText(HomeActivity.this, "No Event Set", Toast.LENGTH_SHORT).show();
        }
    }
}
