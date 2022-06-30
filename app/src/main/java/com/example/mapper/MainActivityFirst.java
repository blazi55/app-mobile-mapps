package com.example.mapper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivityFirst extends AppCompatActivity {

    private Button btDrawPolygon, btDrawPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btDrawPolygon = findViewById(R.id.bt_draw_polygon);
        btDrawPolyline = findViewById(R.id.bt_draw_polyline);

        btDrawPolygon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent newPage = new Intent(MainActivityFirst.this, MainActivitySecond.class);
                startActivity(newPage);
            }
        });

    }
}