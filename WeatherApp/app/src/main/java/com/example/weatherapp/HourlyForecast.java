package com.example.weatherapp;

public class HourlyForecast {

    private String time;
    private int temperature;
    private String icon;

    public HourlyForecast(String time, int temperature, String icon) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }
}
