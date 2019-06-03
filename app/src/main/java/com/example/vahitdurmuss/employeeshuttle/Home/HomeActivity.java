package com.example.vahitdurmuss.employeeshuttle.Home;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeLoginActivity;
import com.example.vahitdurmuss.employeeshuttle.R;
import com.example.vahitdurmuss.employeeshuttle.Shuttle.ShuttleLoginActivity;
import com.example.vahitdurmuss.employeeshuttle.Shuttle.ShuttleViewActivity;
import com.example.vahitdurmuss.employeeshuttle.Shuttle.ViewShuttleMapsActivity;
import com.google.firebase.appindexing.builders.MessageBuilder;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView goEmployee;
    private ImageView goShuttle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout
                .home_activity_main);
        goEmployee=(ImageView)findViewById(R.id.goEmployee);
        goShuttle=(ImageView)findViewById(R.id.goShuttle);


        goEmployee.setOnClickListener(this);
        goShuttle.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.goEmployee:
                startActivity(new Intent(this, EmployeeLoginActivity.class));
                break;
            case R.id.goShuttle:
                startActivity(new Intent(this, ShuttleLoginActivity.class));

            default:
                break;

        }


    }
}
