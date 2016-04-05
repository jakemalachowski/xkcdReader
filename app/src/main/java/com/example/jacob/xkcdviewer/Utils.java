package com.example.jacob.xkcdviewer;


import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jacob on 3/31/2016.
 * Holds basic utilities used throughout the app
 */
public class Utils
{
    public static int getNumberFromUrl(String url, int defaultNumber) {
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
