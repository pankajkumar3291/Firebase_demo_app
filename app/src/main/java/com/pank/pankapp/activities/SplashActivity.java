package com.pank.pankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.pank.pankapp.R;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.SessionSecuredPreferences;

import static com.pank.pankapp.util.Constants.IS_LOGGED_IN;
import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.initView();
    }

    private void initView() {
        SessionSecuredPreferences loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        boolean isLoggedIn = loginPreferences.getBoolean(IS_LOGGED_IN, false);

        if (isLoggedIn) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent dashboardIntent = new Intent(SplashActivity.this, MainActivity.class);
                    //dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(dashboardIntent);
                    SplashActivity.this.finish();
                }
            }, 3000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }, 3000);
        }
    }


}

