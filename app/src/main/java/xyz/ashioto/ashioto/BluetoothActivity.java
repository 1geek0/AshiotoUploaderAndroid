package xyz.ashioto.ashioto;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.ashioto.ashioto.retrofitClasses.CountUpdateResponse;

public class BluetoothActivity extends AppCompatActivity {

    //BluetoothSPP setup
    BluetoothSPP spp = new BluetoothSPP(BluetoothActivity.this);
    //ProgressDialog
    ProgressDialog progressDialog;
    //TextView for count
    @BindView(R.id.mainCount)
    AppCompatTextView countTextView;
    @BindView(R.id.gatesRadioGroup)
    RadioGroup gatesRadioGroup;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;
    Callback<CountUpdateResponse> countUpdateResponseCallback = new Callback<CountUpdateResponse>() {
        @Override
        public void onResponse(Response<CountUpdateResponse> response, Retrofit retrofit) {
            if (!response.body().error) {
                t("Data Synced");
            } else {
                t("Unable To Synced");
            }
        }

        @Override
        public void onFailure(Throwable t) {
            t("Data Sync Failed");
        }
    };
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
    private String current_event;
    //Device mac address
    private String mDeviceAddress;

    @OnClick(R.id.syncbutton)
    void syncData() {
        if (!current_event.equals("na")) {
            HashMap<String, String> countHashmap = new HashMap<>();
            countHashmap.put("count", countTextView.getText().toString());
            countHashmap.put("gateID", String.valueOf(gatesRadioGroup.indexOfChild(findViewById(gatesRadioGroup.getCheckedRadioButtonId())) + 1));
            countHashmap.put("eventCode", current_event);
            Call<CountUpdateResponse> updateResponseCall = ApplicationClass.getRetrofitInterface().syncData(countHashmap);
            updateResponseCall.enqueue(countUpdateResponseCallback);
        } else {
            t("No Event Set!");
        }
    }

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

        //Sharepreferences
        sharedPreferences = getSharedPreferences("sharedprefs", MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();
        current_event = sharedPreferences.getString("current_event", "na");
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
