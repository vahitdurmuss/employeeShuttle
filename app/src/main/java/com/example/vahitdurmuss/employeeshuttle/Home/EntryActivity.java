package com.example.vahitdurmuss.employeeshuttle.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vahitdurmuss.employeeshuttle.Employee.EmployeeMainActivity;
import com.example.vahitdurmuss.employeeshuttle.R;

public class EntryActivity extends FragmentActivity {
    private static final String TERCIH_ADI="Tercihlerim";
   public static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);
        preferences=getSharedPreferences(TERCIH_ADI,MODE_PRIVATE);
        Boolean session=preferences.getBoolean("session",false);
        if (session){
            EmployeeMainActivityeGit();
        }
        else
            anaEkranaGec();
    }

    private void anaEkranaGec() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        ImageView esIcon = (ImageView) findViewById(R.id.esText);
        anim.reset();
        esIcon.clearAnimation();
        esIcon.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(EntryActivity.this, HomeActivity.class);
                startActivity(intent);
                EntryActivity.this.finish();
            }
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
    private void EmployeeMainActivityeGit(){
        Intent intent = new Intent(EntryActivity.this, EmployeeMainActivity.class);
        startActivity(intent);
        EntryActivity.this.finish();


    }

}
