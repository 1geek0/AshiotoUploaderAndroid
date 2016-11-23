package xyz.ashioto.ashioto;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import butterknife.ButterKnife;

public class BluetoothActivity extends AppCompatActivity {

    //BluetoothSPP setup
    BluetoothSPP spp = new BluetoothSPP(BluetoothActivity.this);
    //ProgressDialog
    ProgressDialog progressDialog = new ProgressDialog(BluetoothActivity.this);
    //Connection listener to handle bluetooth related tasks
    BluetoothSPP.BluetoothConnectionListener bluetoothConnectionListener = new BluetoothSPP.BluetoothConnectionListener() {
        @Override
        public void onDeviceConnected(String name, String address) {
            //If connected successfully, dismiss ProgressDialog and give a toast to user
            progressDialog.dismiss();
            t("Connected");
        }

        @Override
        public void onDeviceDisconnected() {
            //If disconnected, finish activity and notify user
            // TODO: 21/11/16 Give reconnection option
            t("Disconnected");
            finish();
        }

        @Override
        public void onDeviceConnectionFailed() {
            //If connection fails, finish activity and give a toast to the user
            t("Conncetion Failed");
            finish();
        }
    };
    //Device mac address
    private String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);
        //Get device address to connect to
        mDeviceAddress = getIntent().getStringExtra("device-address");

        //Setup bluetooth service
        spp.setupService();
        spp.startService(BluetoothState.DEVICE_OTHER);

        //ProgressDialog setup
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Connecting To Ashioto");
    }

    @Override
    protected void onStart() {
        super.onStart();
        spp.setBluetoothConnectionListener(bluetoothConnectionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        spp.connect(mDeviceAddress);
    }

    //Easier toast
    private void t(String message) {
        Toast.makeText(BluetoothActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
