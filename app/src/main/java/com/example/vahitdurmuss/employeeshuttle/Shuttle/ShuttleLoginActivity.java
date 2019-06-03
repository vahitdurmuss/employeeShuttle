package com.example.vahitdurmuss.employeeshuttle.Shuttle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeLoginActivity;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeMainActivity;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeRegisterActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShuttleLoginActivity extends AppCompatActivity  implements View.OnClickListener{

    public static final String SHUTTLE_LOGIN_URL = "http://"+ IpAdress.IP+"/android_api/shuttleLogin.php";

    public static final String KEY_SHUTTLE_PLATE="shuttle_Plate";
    public static final String KEY_SHUTTLE_PASSWORD="shuttle_Password";



    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_SHUTTLE_ID="shuttle_id";    // shuttlemain e gönderilicek
    public static final String KEY_SHUTTLE_LOCATION_ID="shuttle_location_id"; // // shuttlemain e gönderilicek

   

    private EditText editTextShuttlePlate;
    private EditText editTextShuttlePassword;
    
    private Button buttonLogin;
    private String shuttle_id;
    private String shuttle_location_id;
    private String Plate;
    private String Password;
    
   
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuttle_activity_login);

       


        editTextShuttlePlate = (EditText) findViewById(R.id.editTextPlateNumber);
        editTextShuttlePassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        

        buttonLogin.setOnClickListener(this);
       

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.buttonLogin:
                shuttleLogin();
                break;

            default:
                break;
        }


    }
    private void shuttleLogin() {
        Plate = editTextShuttlePlate.getText().toString().trim();
        Password = editTextShuttlePassword.getText().toString().trim();
        if (Plate.matches("") || Password.matches("")) {
            Toast.makeText(ShuttleLoginActivity.this, "tüm alanları giriniz", Toast.LENGTH_LONG).show();
        } else {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SHUTTLE_LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj=new JSONObject(response);
                                shuttle_id=String.valueOf(obj.getInt("shuttle_ID"));
                                shuttle_location_id=obj.getString("location_ID");
                                Toast.makeText(ShuttleLoginActivity.this, shuttle_id+"*"+shuttle_location_id, Toast.LENGTH_LONG).show();
                                openShuttleMainActivity();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ShuttleLoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put(KEY_SHUTTLE_PLATE, Plate);
                    map.put(KEY_SHUTTLE_PASSWORD, Password);
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

    }
    private void openShuttleMainActivity(){


        Intent intent = new Intent(this,ShuttleMainActivity.class);
        intent.putExtra(KEY_SHUTTLE_ID, shuttle_id);
        intent.putExtra(KEY_SHUTTLE_LOCATION_ID,shuttle_location_id);
        intent.putExtra(KEY_SHUTTLE_PLATE,Plate);

        startActivity(intent);



    }





}

