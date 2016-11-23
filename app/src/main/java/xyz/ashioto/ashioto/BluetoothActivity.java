package xyz.ashioto.ashioto;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothActivity extends AppCompatActivity {

    //BluetoothSPP setup
    BluetoothSPP spp = new BluetoothSPP(BluetoothActivity.this);
    //ProgressDialog
    ProgressDialog progressDialog;
    //TextView for count
    @BindView(R.id.mainCount)
    AppCompatTextView countTextView;
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
            t("Connection Failed");
            finish();
        }
    };
    //DataReceive listener
    BluetoothSPP.OnDataReceivedListener dataReceivedListener = new BluetoothSPP.OnDataReceivedListener() {
        @Override
        public void onDataReceived(byte[] data, String message) {
            String count = message.substring(message.lastIndexOf('#') + 1, message.lastIndexOf('$')); //Get the subtring between # and $ (logic from old code)
            countTextView.setText(count);
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
        progressDialog = new ProgressDialog(BluetoothActivity.this); //Assignment done here because doing it with declaration would crash the app
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        // TODO: 23/11/16 The spinner is not showing. Fix it.
        progressDialog.setTitle("Connecting To Ashioto");
        progressDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Set connection listener for bluetooth spp
        spp.setBluetoothConnectionListener(bluetoothConnectionListener);
        //Set data receiver for bluetooth spp
        spp.setOnDataReceivedListener(dataReceivedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        spp.connect(mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        spp.disconnect();
    }

    //Easier toast
    private void t(String message) {
        Toast.makeText(BluetoothActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
