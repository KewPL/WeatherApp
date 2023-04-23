package com.example.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;


public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView userNameTextView;
    private EditText locationEditText;
    private Button searchButton;
    private TextView currentTemperatureTextView;
    private ImageView currentWeatherIconImageView;
    private RecyclerView hourlyForecastRecyclerView;
    private Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        timeUpdateHandler = new Handler(Looper.getMainLooper());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.userNameTextView);
        locationEditText = findViewById(R.id.locationEditText);
        searchButton = findViewById(R.id.searchButton);
        currentTemperatureTextView = findViewById(R.id.currentTemperatureTextView);
        currentWeatherIconImageView = findViewById(R.id.currentWeatherIconImageView);
        hourlyForecastRecyclerView = findViewById(R.id.hourlyForecastRecyclerView);

        String userName = getIntent().getStringExtra("userName");
        userNameTextView.setText("Welcome, " + userName);

        searchButton.setOnClickListener(view -> {
            String location = locationEditText.getText().toString();
            if (!location.isEmpty()) {
                startWeatherUpdates(location);
            }
        });

        hourlyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hourlyForecastRecyclerView.setAdapter(new HourlyForecastAdapter());

        requestLocationPermission();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with fetching the location and weather data
            } else {
                // Permission denied, show a message or handle the situation
            }
        }
    }

    private void updateWeather(String location) {
        fetchWeather(location);
        fetchCurrentTime(location);
    }

    private void fetchWeather(String location) {
        WeatherApiClient.getWeather(this, location, new WeatherApiResponseHandler() {
            @Override
            public void handleResponse(JSONObject response) {
                try {
                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONArray forecastday = forecast.getJSONArray("forecastday");
                    JSONObject firstForecast = forecastday.getJSONObject(0);

                    JSONObject day = firstForecast.getJSONObject("day");
                    double temperature = day.getDouble("avgtemp_c");
                    String iconUrl = day.getJSONObject("condition").getString("icon");

                    Log.d("MainActivity", "Weather icon URL: " + iconUrl);

                    runOnUiThread(() -> {
                        currentTemperatureTextView.setText(String.format(Locale.getDefault(), "%.1f", temperature) + "°C");
                        Glide.with(MainActivity.this)
                                .load("https:" + iconUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Add this line
                                .skipMemoryCache(true) // Add this line
                                .into(currentWeatherIconImageView);


                    });



                    JSONArray hourlyForecastsJsonArray = firstForecast.getJSONArray("hour");
                    List<HourlyForecast> hourlyForecastsList = new ArrayList<>();
                    for (int i = 0; i < hourlyForecastsJsonArray.length(); i++) {
                        JSONObject hourlyForecastJson = hourlyForecastsJsonArray.getJSONObject(i);
                        String rawTime = hourlyForecastJson.getString("time");
                        String time = formatHourlyTime(rawTime);

                        int hourlyTemperature = (int) Math.round(hourlyForecastJson.getDouble("temp_c"));
                        String hourlyIconUrl = hourlyForecastJson.getJSONObject("condition").getString("icon");
                        hourlyForecastsList.add(new HourlyForecast(time, hourlyTemperature, hourlyIconUrl));
                    }

                    // Filter the list to contain only the next 24 hours of forecasts
                    List<HourlyForecast> next24HourForecasts = new ArrayList<>();
                    for (HourlyForecast forecast24 : hourlyForecastsList) {
                        if (next24HourForecasts.size() < 24) {
                            next24HourForecasts.add(forecast24);
                        } else {
                            break;
                        }
                    }

                    runOnUiThread(() -> {
                        HourlyForecastAdapter adapter = (HourlyForecastAdapter) hourlyForecastRecyclerView.getAdapter();
                        if (adapter != null) {
                            adapter.setHourlyForecasts(next24HourForecasts);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void handleError(Exception error) {
                // Handle errors
            }
        });

        startUpdatingTime(location);
    }

    private String formatHourlyTime(String rawTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date parsedTime = inputFormat.parse(rawTime);
            if (parsedTime != null) {
                return outputFormat.format(parsedTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rawTime; // Return rawTime as a fallback in case of errors
    }




    private int getWeatherIconResourceId(String icon) {
        String resourceName = "icon_" + icon.substring(icon.length() - 3);
        return getResources().getIdentifier(resourceName, "drawable", getPackageName());
    }

    private void fetchCurrentTime(String location) {
        new Thread(() -> {
            try {
                String formattedLocation = location.replace(" ", "_");
                String url = "https://time.is/" + formattedLocation;
                Document doc = Jsoup.connect(url).get();
                Element timeElement = doc.getElementById("clock");
                if (timeElement != null) {
                    String currentTime = timeElement.text();
                    runOnUiThread(() -> {
                        // Update the UI with the fetched time
                        TextView updateTimeTextView = findViewById(R.id.updateTimeTextView);
                        updateTimeTextView.setText(currentTime);
                    });

                } else {
                    Log.e("MainActivity", "Could not find the desired element using the selector");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MainActivity", "Error fetching current time");
            }
        }).start();
    }

    private void startWeatherUpdates(String location) {
        fetchWeather(location);
        startUpdatingTime(location);
    }

    private void startUpdatingTime(String location) {
        if (timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        }

        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                fetchCurrentTime(location);
                timeUpdateHandler.postDelayed(this, 1000); // Update the time every second
            }
        };
        timeUpdateHandler.post(timeUpdateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeUpdateHandler != null && timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        }
    }



}
