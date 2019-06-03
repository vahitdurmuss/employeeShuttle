package com.example.vahitdurmuss.employeeshuttle.Shuttle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeLoginActivity;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeMainActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.HomeActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShuttleMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener, View.OnClickListener  {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private Location currentLocation;




    public static final String KEY_SHUTTLE_LOCATION_ADD_URL = "http://" + IpAdress.IP + "/android_api/shuttleInsertlocation.php";
    public static final String KEY_SHUTTLE_LOCATION_UPDATE_URL = "http://" + IpAdress.IP + "/android_api/shuttleUpdateLocation.php";
    public static final String KEY_LATITUDE = "enlem";
    public static final String KEY_LONGITUDE = "boylam";
    public static final String KEY_SHUTTLE_ID = "id";
    public static final String KEY_SHUTTLE_LOCATION_ID = "location_ID";



    private String sonGuncellemeZamani;
    private static String shuttle_id; //giriş yapan shuttle id si. ShuttleLoginActivity den alınmıştır.
    private static String shuttle_location_id; // employee ye konum ekleme işlemi sonrası doldurulur.
    private static String shuttle_Plate;

    private boolean isGpsEnabled;
    private TextView shuttlePlateTextView;
    private Button logOutButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuttle_activity_main);


        shuttlePlateTextView=(TextView)findViewById(R.id.shuttlePlateTextView); // shuttle plakası gösterilmek içi oluşturuldu
        logOutButton=(Button)findViewById(R.id.logOutShttle);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGpsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        shuttle_id = getIntent().getStringExtra(ShuttleLoginActivity.KEY_SHUTTLE_ID);
        shuttle_location_id = getIntent().getStringExtra(ShuttleLoginActivity.KEY_SHUTTLE_LOCATION_ID);
        shuttle_Plate=getIntent().getStringExtra(ShuttleLoginActivity.KEY_SHUTTLE_PLATE); // shuttlelogin giriş yapılan aracın plakası alındı


        shuttlePlateTextView.setText(shuttle_Plate); //shuttle main acticitysinde imagevewın altında plakası yazıldı.
        buildGoogleApiClient();
        logOutButton.setOnClickListener(this);


        if (isGpsEnabled) {  // else if sonradan yazıldı çalışmayabilir


            createLocationRequest();

        }
        else
            toEnableGPSScreen();







    }

    protected void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create()
                .setInterval(20000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void toEnableGPSScreen() {

        Toast.makeText(this, "GPS seçeneğinizi etkinleştiriniz..", Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1);


    }

    private void addLocationToShuttle(final String latitude, final String longitude, final String id) throws JSONException {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, KEY_SHUTTLE_LOCATION_ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //employee nin location id si alınır
                            JSONObject obj = new JSONObject(response);
                            shuttle_location_id = String.valueOf(obj.getInt("location_ID"));
                            Toast.makeText(ShuttleMainActivity.this, "Konum Güncellenmiştir.", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ShuttleMainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);
                params.put(KEY_SHUTTLE_ID, id);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateLocationToShuttle(final String latitude, final String longitude, final String location_ID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, KEY_SHUTTLE_LOCATION_UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(ShuttleMainActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ShuttleMainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);
                params.put(KEY_SHUTTLE_LOCATION_ID, location_ID);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }




    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.logOutShttle:
                Intent logOutIntent=new Intent(this, HomeActivity.class);
                startActivity(logOutIntent);

        }



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (isGpsEnabled) {
            createLocationRequest();
            if (currentLocation == null) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                sonGuncellemeZamani = DateFormat.getTimeInstance().format(new Date());
                if (currentLocation!=null) {
                    try {
                        addLocationToShuttle(String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), shuttle_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;
        sonGuncellemeZamani = DateFormat.getTimeInstance().format(new Date());
        if (isGpsEnabled) {
            if (currentLocation != null) {
                updateLocationToShuttle(String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), shuttle_location_id);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

            if (googleApiClient.isConnected()) {
                stopLocationUpdates();

            }

        googleApiClient.disconnect();

    }

    @Override
    protected void onStart() {
        super.onStart();
        isGpsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGpsEnabled) {
            googleApiClient.connect();

        }




    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {

        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGpsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGpsEnabled) {
            googleApiClient.connect();
            createLocationRequest();
            Toast.makeText(this, "GPS aktif", Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(this, "GPS aktif değil", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {

    }
}