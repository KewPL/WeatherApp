package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void submitName(View view) {
        EditText userNameEditText = findViewById(R.id.userNameEditText);
        String userName = userNameEditText.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }
}