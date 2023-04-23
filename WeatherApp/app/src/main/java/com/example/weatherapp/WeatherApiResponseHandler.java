package com.example.weatherapp;

import org.json.JSONObject;

public interface WeatherApiResponseHandler {
    void handleResponse(JSONObject response);
    void handleError(Exception error);
}
