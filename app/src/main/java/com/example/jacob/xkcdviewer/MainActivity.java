package com.example.jacob.xkcdviewer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_slide_page);

        Intent i = new Intent(getApplicationContext(), ScreenSlidePagerActivity.class);
        startActivity(i);
        finish();

    }
}
