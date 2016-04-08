package com.example.jacob.xkcdviewer;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by Jacob on 3/29/2016.
 * Fills the view of the View Pager and implements the adapter for the View Pager.
 */
public class MainActivity extends AppCompatActivity
{

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int newestComicNumber;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.random) {
            Random random = new Random();
            mPager.setCurrentItem(random.nextInt(newestComicNumber + 1));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
