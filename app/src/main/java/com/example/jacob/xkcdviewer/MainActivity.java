package com.example.jacob.xkcdviewer;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{

    Document doc;
    String comicElement, title, prevButtonLink, nextButtonLink;
    Elements images;
    ImageView comicImg;
    TextView titleTv;
    Button nextBt, prevBt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        comicImg = (ImageView) findViewById(R.id.comicImg);
        titleTv = (TextView) findViewById(R.id.comicTitleTV);
        nextBt = (Button) findViewById(R.id.nextBt);
        prevBt = (Button) findViewById(R.id.prevBt);

        GetPage getPage = new GetPage("http://www.xkcd.com");

        getPage.execute();

        prevBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Get all link tags
                Elements links = doc.getElementsByTag("a");
                //Get the link to the previous comic
                String prevComicNum = links.get(11).attr("href");

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


                Log.d("Images", images.toString());

                //Get Comic Element
                comicElement = images.get(1).absUrl("src");

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
            titleTv.setText(title);
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
}
