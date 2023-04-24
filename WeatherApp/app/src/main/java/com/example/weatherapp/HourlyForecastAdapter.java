package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder> {

    private List<HourlyForecast> hourlyForecasts = new ArrayList<>();

    @NonNull
    @Override
    public HourlyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly_forecast, parent, false);
        return new HourlyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastViewHolder holder, int position) {
        HourlyForecast hourlyForecast = hourlyForecasts.get(position);
        holder.timeTextView.setText(hourlyForecast.getTime());
        holder.temperatureTextView.setText(String.valueOf(hourlyForecast.getTemperature()) + "°");

        // Load the local weather icon image
        int weatherCode = hourlyForecast.getWeatherCode();
        int imageResourceId = holder.itemView.getContext().getResources().getIdentifier("icon_" + weatherCode, "drawable", holder.itemView.getContext().getPackageName());
        if (imageResourceId != 0) {
            holder.weatherIconImageView.setImageResource(imageResourceId);
        } else {
            // Load the default image or handle the situation when the image is not found in the local resources
        }
    }


    @Override
    public int getItemCount() {
        return hourlyForecasts.size();
    }

    public void setHourlyForecasts(List<HourlyForecast> hourlyForecasts) {
        this.hourlyForecasts = hourlyForecasts;
        notifyDataSetChanged();
    }

    class HourlyForecastViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTextView;
        private TextView temperatureTextView;
        private ImageView weatherIconImageView;

        public HourlyForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);

        }

        public void bind(HourlyForecast hourlyForecast) {
            timeTextView.setText(hourlyForecast.getTime());
            temperatureTextView.setText(String.format(Locale.getDefault(), "%d°C", hourlyForecast.getTemperature()));

            String imageUrl = "https:" + hourlyForecast.getIcon();
            String cacheBustingImageUrl = imageUrl + "?timestamp=" + System.currentTimeMillis(); // Add cache-busting query parameter

            // Get the weather code from the hourlyForecast
            int weatherCode = hourlyForecast.getWeatherCode();

            // Get the corresponding image resource ID
            int iconResource = getWeatherIconResource(weatherCode);

            // Set the image using the resource ID
            weatherIconImageView.setImageResource(iconResource);
        }

        private int getWeatherIconResource(int weatherCode) {
            switch (weatherCode) {
                case 1003: // Clear/Sunny
                    return R.drawable.icon_0d;
                case 293: // Clear/Sunny
                    return R.drawable.icon_0d;
                case 176: // Partly Cloudy
                    return R.drawable.icon_0n;
                case 299: // Partly Cloudy
                    return R.drawable.icon_0n;
                case 353: // Partly Cloudy
                    return R.drawable.icon_0n;
                // Add more cases for other weather condition codes
                default:
                    return R.drawable.icon_1n;
            }
        }



    }

}
