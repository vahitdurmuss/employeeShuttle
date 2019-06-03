package com.example.vahitdurmuss.employeeshuttle.Shuttle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeMainActivity;
import com.example.vahitdurmuss.employeeshuttle.Home.IpAdress;
import com.example.vahitdurmuss.employeeshuttle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vahitdurmuss on 14/04/2017.
 */
public class ShuttleAdapter extends ArrayAdapter<Shuttle> {

    final String KEY_SHUTTLE_ID="shuttle_ID";
    final String KEY_EMPLOYEE_ID="employee_ID";
    final String CHOOSE_SHUTTLE_URL="http://"+ IpAdress.IP+"/android_api/chooseShuttle.php";
    final String RE_CHOOSE_SHUTTLE_URL="http://"+ IpAdress.IP+"/android_api/reChooseShuttle.php";
    private  String temporary_shuttle_ID;

    Boolean isShuttle=false;



    public ShuttleAdapter(Context context, ArrayList<Shuttle> shuttles) {
        super(context, 0, shuttles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Shuttle servis = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shuttleviewactivity_template, parent, false);
        }
        // Lookup view for data population
        TextView plate = (TextView) convertView.findViewById(R.id.Plate);
        TextView from = (TextView) convertView.findViewById(R.id.From);
        TextView to = (TextView) convertView.findViewById(R.id.To);
        Button  selectShuttle=(Button)convertView.findViewById(R.id.selectShuttle);

        // Populate the data into the template view using the data object
        plate.setText(servis.shuttle_Plate);
        from.setText(servis.shuttle_From);
        to.setText(servis.shuttle_To);
        selectShuttle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    //Servisler listesindeki butona tıklanınca yapılacak işlemler.

                temporary_shuttle_ID=String.valueOf(servis.shuttle_ID);    //buttona tıklandığında alınan shuttle_ID
                try {
                    chooseShuttleToEmployee(temporary_shuttle_ID, EmployeeMainActivity.employee_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        // Return the completed view to render on screen
        return convertView;
    }


    // servis listesinden seçilen servisi eğer var olan bir servis yoksa employee ye servis olarak ekler.
    public void chooseShuttleToEmployee(final String p_shuttle_ID, final String p_employee_ID) throws JSONException {



        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHOOSE_SHUTTLE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        makeSureToChooseShuttle(response);

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
                params.put(KEY_EMPLOYEE_ID,p_employee_ID );
                params.put(KEY_SHUTTLE_ID,p_shuttle_ID );
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }


    // chooseShuttleToEmployee den responseyi alır ve servis değişikliği için Alertdialog sayesinde seçenek sunar.
    public void makeSureToChooseShuttle(String response){
        JSONObject object;

        try {
            object=new JSONObject(response);
            isShuttle=object.getBoolean("deger");
            String plate=object.getString("plate");

            if (isShuttle==true){  //mevcut servis varsa gösterilir kullanıcıya ve değiştirmek isterse evete tıklar.
                final AlertDialog.Builder builder=new  AlertDialog.Builder(getContext());
                builder.setTitle("Uyarı");
                builder.setMessage(plate+" "+"plakasına sahip servisiniz seçili bulunmaktadır." +
                        "Değiştirmek istiyormusunuz?");
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeExistShuttleBelongEmployee(EmployeeMainActivity.employee_id,temporary_shuttle_ID);

                    }
                });
                builder.setNegativeButton("Hayır, Değişmesin.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                builder.show();

            }
            else {
                Toast.makeText(getContext(), plate, Toast.LENGTH_SHORT).show();

            }


        } catch (JSONException e) {
            e.printStackTrace();

        }


    }



    public void changeExistShuttleBelongEmployee(final String p_Employee_ID, final String p_Shuttle_ID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RE_CHOOSE_SHUTTLE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                         Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();
                        EmployeeMainActivity.employee_shuttle_id=p_Shuttle_ID;  //yeni shuttle id atanır.

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
                params.put(KEY_EMPLOYEE_ID,p_Employee_ID );
                params.put(KEY_SHUTTLE_ID,p_Shuttle_ID );
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

}



