package com.example.vahitdurmuss.employeeshuttle.Employee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Home.EntryActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmployeeLoginActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String LOGIN_URL = "http://"+ IpAdress.IP+"/android_api/ArrangeemployeeLogin.php";

    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_ID="employee_id";
    public static final String KEY_EMPLOYEE_SHUTTLE_ID="employee_shuttle_id";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private String employee_id;
    private String employee_shuttle_ID;
    private String username;
    private String password;
    private TextView goRegisterActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_activity_login);


        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        goRegisterActivity = (TextView) findViewById(R.id.goRegisterActivity);

        buttonLogin.setOnClickListener(this);
        goRegisterActivity.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.buttonLogin:
                userLogin();
                break;
            case R.id.goRegisterActivity:
                startActivity(new Intent(this,EmployeeRegisterActivity.class));
                break;


            default:
                break;
        }


    }
    private void userLogin() {
        final ProgressDialog loading = ProgressDialog.show(this, "Giriş yapılıyor", "Lütfen bekleyiniz...", false, false);
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        if (username.matches("") || password.matches("")) {
            loading.dismiss();
            Toast.makeText(EmployeeLoginActivity.this, "tüm alanları giriniz", Toast.LENGTH_LONG).show();

        } else {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                loading.dismiss();
                                JSONObject obj=new JSONObject(response);
                                employee_id=String.valueOf(obj.getInt("employee_ID"));
                                employee_shuttle_ID=obj.getString("shuttle_ID");
                                Toast.makeText(EmployeeLoginActivity.this, "employee_id"+String.valueOf(employee_id)+"employee_shuttle_id"+employee_shuttle_ID, Toast.LENGTH_SHORT).show();
                                setLogInState();
                                openProfile();


                            } catch (JSONException e) {
                                e.printStackTrace();
                                loading.dismiss();
                                Toast.makeText(EmployeeLoginActivity.this,"Kullanici adi veya şifre yanlış!",Toast.LENGTH_SHORT).show();


                            }




                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            Toast.makeText(EmployeeLoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put(KEY_USERNAME, username);
                    map.put(KEY_PASSWORD, password);
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

    }
    private void openProfile(){
        Intent intent = new Intent(this,EmployeeMainActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_ID,employee_id);
        intent.putExtra(KEY_EMPLOYEE_SHUTTLE_ID,employee_shuttle_ID);
        startActivity(intent);
    }
    private  void setLogInState(){
        SharedPreferences.Editor editor= EntryActivity.preferences.edit();
        editor.putBoolean("session",true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ID,employee_id);
        editor.putString(KEY_EMPLOYEE_SHUTTLE_ID,employee_shuttle_ID);
        editor.apply();
    }
}
