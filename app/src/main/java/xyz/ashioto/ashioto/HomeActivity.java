package xyz.ashioto.ashioto;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.tablayout_main) //TabLayout
            TabLayout tabLayout_main;
    @BindView(R.id.viewpager) // Viewpager (handles tabs)
            ViewPager viewPager;
    @BindView(R.id.start_toolbar) //Toolbar
            Toolbar start_toolbar;
    @BindView(R.id.eventsSpinner)//Spinner holding events list
            Spinner eventsSpinner;

    //Bluetooth variable declaration
    BluetoothSPP bluetoothSPP = new BluetoothSPP(HomeActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setUpBluetooth() {
        if (!bluetoothSPP.isBluetoothEnabled()) { //Check if bluetooth is enabled
            bluetoothSPP.enable(); //Enable bluetooth if not enabled already
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothSPP.getBluetoothAdapter().disable(); //Disable bluetooth once the app is closed
    }
}
