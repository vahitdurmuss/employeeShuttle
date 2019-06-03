package com.example.vahitdurmuss.employeeshuttle.Shuttle;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ShuttleViewActivity extends AppCompatActivity {

    private ListView shuttleListView;
    private ArrayList<Shuttle> servisler;
    private ShuttleAdapter shuttleAdapter;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuttle_activity_view);
        shuttleListView=(ListView)findViewById(R.id.shuttleListView);

        pd=new ProgressDialog(this);
        pd.setMessage("Servis Araçları yüklenmektedir. Lütfen bekleyiniz..");
        try {
            pd.show();
            getShuttles();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getShuttles() throws JSONException {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://"+ IpAdress.IP+"/android_api/getShuttles.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            list(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();



                    }
                }) {




        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void list(String response) throws JSONException {
        JSONObject object= null;
        object = new JSONObject(response);
        JSONArray jsonArray=object.getJSONArray("result");
        servisler=new ArrayList<Shuttle>();
        servisler=Shuttle.fromJson(jsonArray);
        shuttleAdapter=new ShuttleAdapter(ShuttleViewActivity.this,servisler);
        shuttleListView.setAdapter(shuttleAdapter);
        pd.dismiss();
    }
}
