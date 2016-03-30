package com.example.jacob.xkcdviewer;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Jacob on 3/29/2016.
 */
public class ScreenSlidePageFragment extends Fragment
{

    Document doc;
    String comicElement, title, altText, prevComicNum;
    Elements images;
    TouchImageView comicImg;
    TextView titleTv, comicNumTv;
    Button nextBt, prevBt, randBt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.screen_slide_page, container, false);

        comicImg = (TouchImageView) rootView.findViewById(R.id.comicImg);
        titleTv = (TextView) rootView.findViewById(R.id.comicTitleTV);
        nextBt = (Button) rootView.findViewById(R.id.nextBt);
        prevBt = (Button) rootView.findViewById(R.id.prevBt);
        randBt = (Button) rootView.findViewById(R.id.randBt);
        comicNumTv = (TextView) rootView.findViewById(R.id.comicNumTv);

        comicImg.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                builder.setCancelable(true);
                builder.setMessage(altText);
                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });
        prevBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Get all link tags
                Elements links = doc.getElementsByTag("a");
                //Get the link to the previous comic
                prevComicNum = links.get(11).attr("href");

                GetPage getPrevPage = new GetPage("http://www.xkcd.com" + prevComicNum);
                getPrevPage.execute();
            }
        });

        nextBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Get all link tags
                Elements links = doc.getElementsByTag("a");
                //Get the link to the previous comic
                String nextComicNum = links.get(13).attr("href");

                GetPage getPrevPage = new GetPage("http://www.xkcd.com" + nextComicNum);
                getPrevPage.execute();
            }
        });

        randBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GetPage getRandomPage = new GetPage("http://c.xkcd.com/random/comic");
                getRandomPage.execute();
            }
        });

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GetPage getPage = new GetPage("http://www.xkcd.com");

        getPage.execute();

    }

    public class GetPage extends AsyncTask
    {
        String link;
        Bitmap comicBtmp;

        GetPage(String link)
        {
            this.link = link;
        }

        @Override
        protected Object doInBackground(Object[] params)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            try
            {
                doc = Jsoup.connect(link).get();
                title = doc.getElementById("ctitle").text();
                images = doc.getElementsByTag("img");

                //Get Comic Element
                comicElement = images.get(1).absUrl("src");
                altText = images.get(1).attr("title");

                //Set the previous comic link so the number can be extracted later in order to set the current comic number
                //Get all link tags
                Elements links = doc.getElementsByTag("a");
                //Get the link to the previous comic
                prevComicNum = links.get(11).attr("href");

                //Set proper density for imageView
                options.inDensity = DisplayMetrics.DENSITY_DEFAULT;

                //Download the image
                URL url = new URL(comicElement);
                comicBtmp = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            comicImg.setImageBitmap(comicBtmp);
            comicImg.resetZoom();
            titleTv.setText(title);
            Log.d("Comic URL", prevComicNum);
            String comicNumText = "xkcd: " + String.valueOf(getNumberFromUrl(prevComicNum, 0) + 1);
            comicNumTv.setText(comicNumText);
        }
    }

    //SOURCE: http://stackoverflow.com/questions/6407324/how-to-get-image-from-url-in-android
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");

            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private int getNumberFromUrl(String url, int defaultNumber) {
        //Extracts the comic number from xkcd urls
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < url.length(); i++) {
            char c = url.charAt(i);
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        try {
            return Integer.parseInt(sb.toString());
        } catch (NumberFormatException e) {
            return defaultNumber;
        }
    }
}
