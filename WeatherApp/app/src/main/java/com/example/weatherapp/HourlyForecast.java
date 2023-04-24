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

    public int getWeatherCode() {
        if (icon != null && icon.length() >= 7) {
            try {
                return Integer.parseInt(icon.substring(icon.length() - 7, icon.length() - 4));
            } catch (NumberFormatException e) {
                // Log the exception or handle it
            }
        }
        return -1; // Return -1 if the weather code cannot be extracted
    }
}


