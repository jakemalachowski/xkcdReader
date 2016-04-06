package com.example.jacob.xkcdviewer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jacob on 3/29/2016.
 * Fills the view of the View Pager and implements the adapter for the View Pager.
 */
public class MainActivity extends FragmentActivity{

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int newestComicNumber;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeActivity mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        try {
            GetJSON getJSON = new GetJSON();
            getJSON.execute();
            getJSON.get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeActivity();
/*        mShakeDetector.setOnShakeListener(new ShakeActivity.OnShakeListener() {

            @Override
            public void onShake(int count) {
                ScreenSlidePageFragment getRandomPage = new ScreenSlidePageFragment();
                getRandomPage.callingLink = "http://c.xkcd.com/random/comic";
                getRandomPage.GetPage().execute();
                mPager.setCurrentItem(mPagerAdapter);
            }
        });
*/
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
            screenSlidePageFragment.callingLink = "http://www.xkcd.com/" + String.valueOf(position);
            return screenSlidePageFragment;
        }


        @Override
        public int getCount()
        {
            return newestComicNumber + 1;
        }
    }

    public class GetJSON extends AsyncTask <Void, Void, Void>
    {
        String json;

        @Override
        protected Void doInBackground(Void... params)
        {
            HttpURLConnection urlConnection = null;
            try
            {
                URL url = new URL("http://xkcd.com/info.0.json");
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            try {
                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bf.readLine()) != null)
                {
                    sb.append(line);
                    sb.append("\n");
                }
                json = sb.toString();

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            finally {
                urlConnection.disconnect();
            }

            try
            {
                JSONObject jsonObject = new JSONObject(json);
                newestComicNumber = Integer.parseInt(jsonObject.getString("num"));

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mPager.setCurrentItem(newestComicNumber);
        }
    }
}
