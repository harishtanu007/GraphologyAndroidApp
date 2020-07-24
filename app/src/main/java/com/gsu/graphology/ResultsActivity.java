package com.gsu.graphology;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultsActivity extends AppCompatActivity {

    String htmlContent;
    private static final String TAG = ResultsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setTitle("Graphology Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        htmlContent = intent.getStringExtra("html");


        if(htmlContent != null) {
            Document document = Jsoup.parse(htmlContent);
            document.select("img").remove();
            document.select("nav").remove();
            htmlContent = document.toString();
        }

        WebView htmlView = findViewById(R.id.htmlview);
        htmlView.clearCache(true);
        htmlView.clearHistory();
        htmlView.getSettings().setJavaScriptEnabled(true);
        htmlView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        Log.e(TAG,htmlContent);

        htmlView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


}