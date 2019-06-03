package com.example.vahitdurmuss.employeeshuttle.Employee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Home.EntryActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.HomeActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;
import com.example.vahitdurmuss.employeeshuttle.Shuttle.ShuttleViewActivity;
import com.example.vahitdurmuss.employeeshuttle.Shuttle.ViewShuttleMapsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class EmployeeMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener, View.OnClickListener {


    public static final String KEY_LOCATION_ADD_URL = "http://" + IpAdress.IP + "/android_api/employeeInsertlocation.php";
    public static final String KEY_LOCATION_UPDATE_URL = "http://" + IpAdress.IP + "/android_api/employeeUpdateLocation.php";
    public static final String KEY_LATITUDE = "enlem";
    public static final String KEY_LONGITUDE = "boylam";
    public static final String KEY_EMPLOYEE_ID = "id";
    public static final String KEY_EMPLOYEE_LOCATION_ID = "location_ID";


    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private Location currentLocation;

    private long updateTimeLocationRequest= 5000;

    private long secondsToUpdateLocationRequest[]={5000,10000,20000,60000,120000,180000};
    private CharSequence times[]={"5 saniye","10 saniye","20 saniye","1 dakika","2 dakika","3 dakika"};

    private Double shuttleLatitude=null;
    private Double shuttleLongitude=null;






    private String sonGuncellemeZamani;
    public static String employee_id; //giriş yapan employee id si. EmployeeLoginActivity den alınmıştır.
    public static String employee_location_id; // employee ye konum ekleme işlemi sonrası doldurulur.
    private boolean isGpsEnabled;
    public static String employee_shuttle_id; // login işleminde alınır yeni servis seçildiğinde ShuttleViewActity içerisinde dğiştirilir.


    private TextView distanceTextView;
    private TextView arrivalTimeTextView;
    private TextView enlemTextView;
    private TextView boylamTextView;
    private TextView sonGuncellemeTextView;
    private TextView employee_nameTextView;
    private ImageView openGpsButton;
    private ImageView goShuttleListButton;
    private ImageView selectUpdateTimeButton;
    private ImageView seeShuttleOnMapButton;
    private Button logOutButton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_activity_main);

        distanceTextView=(TextView)findViewById(R.id.distance_textView);
        arrivalTimeTextView=(TextView)findViewById(R.id.arrivalTime_textView);
        enlemTextView = (TextView) findViewById(R.id.enlem);
        boylamTextView = (TextView) findViewById(R.id.boylam);
        sonGuncellemeTextView = (TextView) findViewById(R.id.guncellemezamani);
        sonGuncellemeZamani = "";
        employee_nameTextView = (TextView) findViewById(R.id.userName);

        goShuttleListButton = (ImageView) findViewById(R.id.goShuttleList);
        seeShuttleOnMapButton = (ImageView) findViewById(R.id.seeShuttleOnMap);
        selectUpdateTimeButton = (ImageView) findViewById(R.id.selectUpdateTime);
        openGpsButton = (ImageView) findViewById(R.id.openGpsButton);
        logOutButton=(Button)findViewById(R.id.logOutEmplyee);


        logOutButton.setOnClickListener(this);
        goShuttleListButton.setOnClickListener(this);
        openGpsButton.setOnClickListener(this);
        selectUpdateTimeButton.setOnClickListener(this);
        seeShuttleOnMapButton.setOnClickListener(this);



        //Intent intent = getIntent();

        //employee_nameTextView.setText(intent.getStringExtra(EmployeeLoginActivity.KEY_USERNAME));
        //employee_id = intent.getStringExtra(EmployeeLoginActivity.KEY_ID);
        //employee_shuttle_id = intent.getStringExtra(EmployeeLoginActivity.KEY_EMPLOYEE_SHUTTLE_ID);

        saveUserDatasBySharedPreferences();


        //Toast.makeText(this, employee_shuttle_id, Toast.LENGTH_LONG).show(); // employeenin shuttle id sine göstermek içi yazdım hangisini seçiyorum anlamalıyım

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGpsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        buildGoogleApiClient();

        if (isGpsEnabled) {  // else if sonradan yazıldı çalışmayabilir

            openGpsButton.setImageResource(R.drawable.emagpson);
            createLocationRequest();

        }
        else
            toEnableGPSScreen();

        // employee_shuttle_id var ise bazı işlemler yapılmalı



        try {

            setRepeatingAsyncTask();
        } catch (JSONException e) {
            e.printStackTrace();
        }





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
                .setInterval(updateTimeLocationRequest)
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

    private void updateUI() {


        if (currentLocation != null) {
            enlemTextView.setText(String.valueOf(currentLocation.getLatitude()));
            boylamTextView.setText(String.valueOf(currentLocation.getLongitude()));
            sonGuncellemeTextView.setText(sonGuncellemeZamani);
        }
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
            openGpsButton.setImageResource(R.drawable.emagpson);


        }
        else {
            Toast.makeText(this, "GPS aktif değil", Toast.LENGTH_SHORT).show();
            openGpsButton.setImageResource(R.drawable.emagpsoff);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
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
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();

        }

        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (isGpsEnabled) {

            createLocationRequest();



                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                sonGuncellemeZamani = DateFormat.getTimeInstance().format(new Date());
                if (currentLocation!=null)
                {
                    try {
                        addLocationToUser(String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), employee_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



                updateUI();
                startLocationUpdates();
            }


        }





    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }


    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;
        sonGuncellemeZamani = DateFormat.getTimeInstance().format(new Date());
        if (isGpsEnabled) {
            if (currentLocation != null) {
                updateLocationToUser(String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), employee_location_id);
            }


            updateUI();
        }

    }


    private void addLocationToUser(final String latitude, final String longitude, final String id) throws JSONException {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, KEY_LOCATION_ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //employee nin location id si alınır
                            JSONObject obj = new JSONObject(response);
                            employee_location_id = String.valueOf(obj.getInt("location_ID"));
                            Toast.makeText(EmployeeMainActivity.this, "Konum Güncellenmiştir.", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EmployeeMainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);
                params.put(KEY_EMPLOYEE_ID, id);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateLocationToUser(final String latitude, final String longitude, final String location_ID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, KEY_LOCATION_UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(EmployeeMainActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(EmployeeMainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);
                params.put(KEY_EMPLOYEE_LOCATION_ID, location_ID);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void toEnableGPSScreen() {

        Toast.makeText(this, "GPS seçeneğinizi etkinleştiriniz..", Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1);

    }

    private void toSelectLocationUpdateTime() {
        AlertDialog.Builder selectTimeBuilder = new AlertDialog.Builder(this);
        selectTimeBuilder.setTitle("Konum Güncelleme zamanı seçiniz");

        selectTimeBuilder.setItems(times, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        selectTimeBuilder.show();

    }
    private void getShuttleLocationToCalculateDistance(final String p_employee_shuttle_id) throws JSONException {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, ViewShuttleMapsActivity.SHUTTLE_LAT_LNG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            if(object.isNull("location_ID")){
                                Toast.makeText(EmployeeMainActivity.this,"servisin konumu yok",Toast.LENGTH_SHORT).show();
                               //showDistanceandArrivalTimeOnWindow(0,false);
                            }
                            else {
                                Double latitude = Double.parseDouble(object.getString("latitude"));
                                Double longitude = Double.parseDouble(object.getString("longitude"));
                                shuttleLatitude = latitude;
                                shuttleLongitude = longitude;
                            //    Toast.makeText(EmployeeMainActivity.this,object.getString("latitude")+"*"+object.getString("longitude"),Toast.LENGTH_SHORT).show();
                               calculateDistanceBetweenShuttleAndEmployee();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(ViewShuttleMapsActivity.KEY_EMPLOYEE_SHUTTLE_ID, p_employee_shuttle_id);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private void calculateDistanceBetweenShuttleAndEmployee(){
        try {
            getResultsfromDirectionAPI();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyCzS0Qj_obihPv7esBl4gHsYDuT0Vdi1oY");
        return urlString.toString();
    }
    private void getResultsfromDirectionAPI() throws JSONException{
        //Getting the URL
        String url=makeURL(currentLocation.getLatitude(),currentLocation.getLongitude(),shuttleLatitude,shuttleLongitude);

        //Showing a dialog till we get the route
        //final ProgressDialog loading = ProgressDialog.show(this, "Hesaplama yapılıyor", "Lütfen bekleyiniz...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // loading.dismiss();

                        try {
                            JSONObject  json = new JSONObject(response);
                            JSONArray routeArray = json.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);

                            JSONArray newTempARr = routes.getJSONArray("legs");
                            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                            JSONObject timeOb = newDisTimeOb.getJSONObject("duration");
                            double distance=distOb.getDouble("value");
                            double duration=timeOb.getDouble("value");
                            showDistanceandArrivalTimeOnWindow(distance,duration,true);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
                        Toast.makeText(EmployeeMainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }






    private void showDistanceandArrivalTimeOnWindow(double pdistance,double pduration, Boolean isLocationExist){

        if(isLocationExist==true) {


            DecimalFormat decimalFormat= new DecimalFormat("#.##");
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);




            if(pdistance>=1000) {   // distance 1000 den eşit veya büyükse km tipinden hesaplamalar yapılacak ekranda göstermek için


               double distanceAsKilometers=pdistance/1000;

                distanceTextView.setText(String.valueOf(decimalFormat.format(distanceAsKilometers)) + "" + "KM");

                double arrivalTime = pduration/60;
                if (arrivalTime>=60)
                {
                    arrivalTime=arrivalTime/60;
                    arrivalTimeTextView.setText(String.valueOf(decimalFormat.format(arrivalTime)) + "" + "SA");

                }
                else
                    arrivalTimeTextView.setText(String.valueOf(decimalFormat.format(arrivalTime)) + "" + "DK");
            }
            else if(pdistance<1000){ //distance 1000 den küçükşe m tipinden hesaplamalar yapılacak ekranda göstermek için


                distanceTextView.setText(String.valueOf(decimalFormat.format(pdistance)) + "" + "M");
                double arrivalTime = pduration/60;

                arrivalTimeTextView.setText(String.valueOf(decimalFormat.format(arrivalTime)) + "" + "DK");


            }
        }
        else if (isLocationExist==false)
        {
            distanceTextView.setText("M");
            arrivalTimeTextView.setText("DK");
        }
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

                            try {
                                isGpsEnabled = locationManager
                                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                                if (isGpsEnabled)
                                    getShuttleLocationToCalculateDistance(employee_shuttle_id);
                                else
                                    showDistanceandArrivalTimeOnWindow(0,0,false);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            // error, do something
                        }


                    }
                });
            }
        };

        timer.schedule(task, 0,10000);  // interval of one minute
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.openGpsButton:
                toEnableGPSScreen();
                break;
            case R.id.goShuttleList:

                Intent intent=new Intent(this,ShuttleViewActivity.class);
                startActivity(intent);  // employee nin shuttleId sini almak için başlatıldı.
                break;
            case R.id.selectUpdateTime:
                toSelectLocationUpdateTime();
                break;
            case  R.id.seeShuttleOnMap:
                Intent seeShuttleOnMapIntent=new Intent(this, ViewShuttleMapsActivity.class);
                seeShuttleOnMapIntent.putExtra(EmployeeLoginActivity.KEY_EMPLOYEE_SHUTTLE_ID,employee_shuttle_id);
                startActivity(seeShuttleOnMapIntent);
                break;
            case R.id.logOutEmplyee:
                logoutScreen();


            default:
                break;

        }

    }

    private  void setLogOut(){
        SharedPreferences.Editor editor= EntryActivity.preferences.edit();
        editor.putBoolean("session",false);
        editor.putString(EmployeeLoginActivity.KEY_USERNAME,"");
        editor.putString(EmployeeLoginActivity.KEY_ID,"");
        editor.putString(EmployeeLoginActivity.KEY_EMPLOYEE_SHUTTLE_ID,"");
        editor.apply();
        Intent intent=new Intent(EmployeeMainActivity.this, HomeActivity.class);
        this.finish();
        startActivity(intent);
    }

    private void saveUserDatasBySharedPreferences(){
        employee_nameTextView.setText(EntryActivity.preferences.getString(EmployeeLoginActivity.KEY_USERNAME,""));
        employee_id = EntryActivity.preferences.getString(EmployeeLoginActivity.KEY_ID,"");
        employee_shuttle_id = EntryActivity.preferences.getString(EmployeeLoginActivity.KEY_EMPLOYEE_SHUTTLE_ID,"");
    }

    @Override
    public void onBackPressed() {

    }

    private void logoutScreen(){
        final AlertDialog.Builder builder=new  AlertDialog.Builder(EmployeeMainActivity.this);
        builder.setTitle("ÇIKIŞ");
        builder.setMessage("çıkış yapmak istediğinize emin misiniz?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setLogOut();

            }
        });
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }




    }



