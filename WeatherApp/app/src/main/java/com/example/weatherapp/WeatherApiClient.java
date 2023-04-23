package com.example.weatherapp;

import static com.example.weatherapp.BuildConfig.WEATHER_API_KEY;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.util.Locale;
import java.util.function.Consumer;
import com.example.weatherapp.WeatherApiResponseHandler;
import com.example.weatherapp.BuildConfig;


public class WeatherApiClient {

    public static void getWeather(Context context, String location, WeatherApiResponseHandler handler) {
        String url = String.format(Locale.getDefault(), "https://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&days=1&hourly=24", WEATHER_API_KEY, location);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (handler != null) {
                        handler.handleResponse(response);
                    }
                },
                error -> {
                    if (handler != null) {
                        handler.handleError(error);
                    }
                });

        getRequestQueue(context).add(request);
    }

    private static RequestQueue requestQueue;

    private static RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

}


