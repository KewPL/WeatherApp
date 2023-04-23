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
        holder.bind(hourlyForecast);
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

            Glide.with(itemView.getContext())
                    .load(cacheBustingImageUrl) // Update this line
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.weather_icon_error)
                    .into(weatherIconImageView);
        }
    }

}
