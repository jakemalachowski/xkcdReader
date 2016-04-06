package com.example.jacob.xkcdviewer;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;


/**
 * Created by Jacob on 3/29/2016.
 * Holds view of the main comic screen
 */
public class ScreenSlidePageFragment extends Fragment
{
    Document doc;
    String comicElement, title, altText, prevComicNum, callingLink;
    TouchImageView comicImg;
    TextView titleTv, comicNumTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.screen_slide_page, container, false);

        comicImg = (TouchImageView) rootView.findViewById(R.id.comicImg);
        titleTv = (TextView) rootView.findViewById(R.id.comicTitleTV);
        comicNumTv = (TextView) rootView.findViewById(R.id.comicNumTv);

        comicImg.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                try
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    builder.setCancelable(true);
                    builder.setMessage(altText);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GetPage getPage = new GetPage(callingLink);
        getPage.execute();
    }

    public class GetPage extends AsyncTask<Void, Void, Void>
    {
        String link;
        Bitmap comicBtmp;

        GetPage(String link)
        {
            this.link = link;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            try
            {
                doc = Jsoup.connect(link).get();
                title = doc.getElementById("ctitle").text();

                //Get Comic Element
                Element e = doc.getElementById("comic");
                Elements es = e.select("img");
                comicElement = es.get(0).absUrl("src");
                altText = es.get(0).attr("title");

                //Set proper density for imageView
                options.inDensity = DisplayMetrics.DENSITY_DEFAULT;

                //Download the image
                URL url = new URL(comicElement);
                comicBtmp = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

            }  catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            comicImg.setImageBitmap(comicBtmp);
            comicImg.resetZoom();
            titleTv.setText(title);
            String comicNumText = "xkcd: " + String.valueOf(Utils.getNumberFromUrl(callingLink, 0));
            comicNumTv.setText(comicNumText);
        }

    }

}
