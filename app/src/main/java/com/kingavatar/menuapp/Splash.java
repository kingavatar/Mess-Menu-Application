package com.kingavatar.menuapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(100);
                    Intent menuIntent = new Intent(Splash.this, MainActivity.class);
                    startActivity(menuIntent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        logoTimer.start();
    }
}
