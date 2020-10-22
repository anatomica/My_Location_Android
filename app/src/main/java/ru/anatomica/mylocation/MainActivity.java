package ru.anatomica.mylocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.decimal4j.util.DoubleRounder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    String nameProvider;
    TextView titleField;
    TextView addrField;
    TextView accField;
    TextView latField;
    TextView lngField;
    TextView altField;
    TextView spdField;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        nameProvider = locationManager.getBestProvider(new Criteria(), false);
        Toast.makeText(MainActivity.this, "onCreate", Toast.LENGTH_LONG).show();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, R.string.msg, Toast.LENGTH_LONG).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(nameProvider);
        if (location != null) {
            onLocationChanged(location);
        } else {
            locationManager.requestLocationUpdates(nameProvider, 1000, 0, this);
        }

    }

    private void initUI() {
        titleField = findViewById(R.id.title);
        titleField.setTypeface(Typeface.createFromAsset(getAssets(),"Comfortaa_Bold.ttf"));
        addrField = findViewById(R.id.address);
        addrField.setTypeface(Typeface.createFromAsset(getAssets(),"Comfortaa_Regular.ttf"));
        accField = findViewById(R.id.accuracy);
        accField.setTypeface(Typeface.createFromAsset(getAssets(),"Comfortaa_Regular.ttf"));
        latField = findViewById(R.id.latitude);
        latField.setTypeface(Typeface.createFromAsset(getAssets(),"Comfortaa_Regular.ttf"));
        lngField = findViewById(R.id.longitude);
        lngField.setTypeface(Typeface.createFromAsset(getAssets(),"Comfortaa_Regular.ttf"));
        altField = findViewById(R.id.altitude);
        altField.setTypeface(Typeface.createFromAsset(getAssets(),"Comfortaa_Regular.ttf"));
        spdField = findViewById(R.id.speed);
        spdField.setTypeface(Typeface.createFromAsset(getAssets(),"Comfortaa_Regular.ttf"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, R.string.msg, Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(nameProvider, 1000, 1, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, R.string.msg, Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.removeUpdates(MainActivity.this);
    }

    @Override
    public void onLocationChanged(Location location) {

        String provider = location.getProvider();
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        Double altitude = DoubleRounder.round(location.getAltitude(), 1);
        Double accuracy = DoubleRounder.round(location.getAccuracy(), 1);
        Float speed = location.getSpeed();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        StringBuilder address = new StringBuilder();

        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Log.i("address", addressList.get(0).toString());
                for (int i = 0; i <= addressList.get(0).getMaxAddressLineIndex(); i++) {
                    address.append(addressList.get(0).getAddressLine(i));
                }
                addrField.setText(getString(R.string.addr) + address.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        accField.setText(getString(R.string.acc) + " " + accuracy.toString() + " м.");
        latField.setText(getString(R.string.lat) + " " + latitude.toString());
        lngField.setText(getString(R.string.lng) + " " + longitude.toString());
        altField.setText(getString(R.string.alt) + " " + altitude.toString() + " м.");
        spdField.setText(getString(R.string.sdp) + " " + speed.toString() + " м/c");

        Log.i("provider", String.valueOf(provider));
        Log.i("latitude", String.valueOf(latitude));
        Log.i("longitude", String.valueOf(longitude));
        Log.i("altitude", String.valueOf(altitude));
        Log.i("speed", String.valueOf(speed));
        Log.i("accuracy", String.valueOf(accuracy));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
