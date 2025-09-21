
package com.example.voxignota;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient; // Added import
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class LearnActivity extends AppCompatActivity {

    private ImageView arrowGettingStarted, arrowAdvanced, arrowVideoLinks;
    private ImageView contentGettingStarted;
    private WebView contentAdvanced, contentVideoLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // Initialize Headers
        RelativeLayout headerGettingStarted = findViewById(R.id.headerGettingStarted);
        RelativeLayout headerAdvanced = findViewById(R.id.headerAdvanced);
        RelativeLayout headerVideoLinks = findViewById(R.id.headerVideoLinks);

        // Initialize Arrows
        arrowGettingStarted = findViewById(R.id.arrowGettingStarted);
        arrowAdvanced = findViewById(R.id.arrowAdvanced);
        arrowVideoLinks = findViewById(R.id.arrowVideoLinks);

        // Initialize Content Areas
        contentGettingStarted = findViewById(R.id.contentGettingStarted);
        contentAdvanced = findViewById(R.id.contentAdvanced);
        contentVideoLinks = findViewById(R.id.contentVideoLinks);

        // Setup WebViews
        setupWebView(contentAdvanced);
        setupWebView(contentVideoLinks);

        // Set Click Listeners
        headerGettingStarted.setOnClickListener(v -> {
            closeAllSectionsExcept(contentGettingStarted);
            toggleVisibility(contentGettingStarted, arrowGettingStarted);
            if (contentGettingStarted.getVisibility() == View.VISIBLE) {
                // Load image from assets
                try {
                    // Make sure you have an image file named "getting_started_image.jpg"
                    // in your app/src/main/assets/ folder.
                    InputStream ims = getAssets().open("getting_started_image.jpg");
                    Drawable d = Drawable.createFromStream(ims, null);
                    contentGettingStarted.setImageDrawable(d);
                    ims.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Optionally, set a placeholder or error image
                    // contentGettingStarted.setImageResource(R.drawable.ic_placeholder); 
                }
            }
        });

        headerAdvanced.setOnClickListener(v -> {
            closeAllSectionsExcept(contentAdvanced);
            toggleVisibility(contentAdvanced, arrowAdvanced);
            if (contentAdvanced.getVisibility() == View.VISIBLE) {
                // URLs from your previous code
                contentAdvanced.loadUrl("https://share.google/Xgj3VtikkWAzQVLf6");
            }
        });

        headerVideoLinks.setOnClickListener(v -> {
            closeAllSectionsExcept(contentVideoLinks);
            toggleVisibility(contentVideoLinks, arrowVideoLinks);
            if (contentVideoLinks.getVisibility() == View.VISIBLE) {
                // URLs from your previous code
                contentVideoLinks.loadUrl("https://youtube.com/playlist?list=PLFjydPMg4DapfRTBMokl09Ht-fhMOAYf6&si=SjGAOTs01GMfGy7S");
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript (optional, but often needed)
        webView.setWebViewClient(new WebViewClient()); // Ensures links open in the WebView
    }

    private void toggleVisibility(View contentView, ImageView arrowView) {
        if (contentView.getVisibility() == View.VISIBLE) {
            contentView.setVisibility(View.GONE);
            arrowView.setImageResource(R.drawable.ic_arrow_down);
        } else {
            contentView.setVisibility(View.VISIBLE);
            arrowView.setImageResource(R.drawable.ic_arrow_up);
        }
    }

    private void closeAllSectionsExcept(View currentContentToOpen) {
        if (contentGettingStarted != currentContentToOpen && contentGettingStarted.getVisibility() == View.VISIBLE) {
            contentGettingStarted.setVisibility(View.GONE);
            arrowGettingStarted.setImageResource(R.drawable.ic_arrow_down);
        }
        if (contentAdvanced != currentContentToOpen && contentAdvanced.getVisibility() == View.VISIBLE) {
            contentAdvanced.setVisibility(View.GONE);
            arrowAdvanced.setImageResource(R.drawable.ic_arrow_down);
        }
        if (contentVideoLinks != currentContentToOpen && contentVideoLinks.getVisibility() == View.VISIBLE) {
            contentVideoLinks.setVisibility(View.GONE);
            arrowVideoLinks.setImageResource(R.drawable.ic_arrow_down);
        }
    }
}
