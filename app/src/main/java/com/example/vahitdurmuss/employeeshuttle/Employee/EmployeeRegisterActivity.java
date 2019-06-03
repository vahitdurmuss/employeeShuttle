package com.example.vahitdurmuss.employeeshuttle.Employee;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class EmployeeRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String REGISTER_URL = "http://"+ IpAdress.IP+"/android_api/employeeRegister.php";
    public static final String KEY_NAME = "Name";
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_PASSWORD = "Password";
    public static final String KEY_EMAIL = "Email";



    private EditText editTextName;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button buttonRegister;
    private TextView goLoginActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_activity_register);

        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        goLoginActivity=(TextView)findViewById(R.id.goLoginActivity);

        buttonRegister.setOnClickListener(this);
        goLoginActivity.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonRegister:
                try {
                    registerUser();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.goLoginActivity:
                startActivity(new Intent(this, EmployeeLoginActivity.class));
                break;
            default:
                break;

        }


    }
        

    
    private void registerUser() throws JSONException {
        final String name = editTextName.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        if (name.matches("") || username.matches("")|| password.matches("")|| email.matches("")) {
           Toast.makeText(EmployeeRegisterActivity.this,"alanların tamamını doldurunuz",Toast.LENGTH_LONG).show();
        } else {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(EmployeeRegisterActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(EmployeeRegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_NAME, name);
                    params.put(KEY_USERNAME, username);
                    params.put(KEY_PASSWORD, password);
                    params.put(KEY_EMAIL, email);
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    }





