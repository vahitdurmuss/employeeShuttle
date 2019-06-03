package com.example.vahitdurmuss.employeeshuttle.Shuttle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeLoginActivity;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeMainActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ViewShuttleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static String KEY_EMPLOYEE_SHUTTLE_ID = "shuttle_ID";
    public static String SHUTTLE_LAT_LNG_URL = "http://" + IpAdress.IP + "/android_api/getShuttleLtLgToShowOnMap.php";


    private GoogleMap mMap;
    LatLng shuttleLatLng = null;
    String employee_shuttle_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shuttle_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        employee_shuttle_id = getIntent().getStringExtra(EmployeeLoginActivity.KEY_EMPLOYEE_SHUTTLE_ID);
        try {
            setRepeatingAsyncTask();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        // Add a marker in Sydney and move the camera

    }


    private void setRepeatingAsyncTask() throws JSONException {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            getLocationToShowMap(employee_shuttle_id);

                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 5000);  // interval of one minute

    }

    private void getLocationToShowMap(final String p_employee_shuttle_id) throws JSONException {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SHUTTLE_LAT_LNG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            if (!object.isNull("latitude") || !object.isNull("longitude")) {


                                Double latitude = Double.parseDouble(object.getString("latitude"));
                                Double longitude = Double.parseDouble(object.getString("longitude"));
                                shuttleLatLng = new LatLng(latitude, longitude);

                                mMap.clear();

                                mMap.addMarker(new MarkerOptions().position(shuttleLatLng).title("Servis Aracı"));

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(shuttleLatLng));
                                doCameraPositionSettings();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewShuttleMapsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_EMPLOYEE_SHUTTLE_ID,p_employee_shuttle_id);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void doCameraPositionSettings(){
        CameraPosition cameraPosition=new CameraPosition.Builder()
                .target(shuttleLatLng)
                .zoom(17)
                .bearing(90)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }





}



