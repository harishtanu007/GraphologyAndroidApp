package com.gsu.graphology;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    String htmlContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        htmlContent = intent.getStringExtra("html");

        WebView htmlView = findViewById(R.id.htmlview);

        htmlView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);

    }
}