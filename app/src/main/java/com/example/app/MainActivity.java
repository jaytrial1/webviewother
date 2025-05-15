package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private static final String TAG = "MainActivity";

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.activity_main_webview);
        mProgressBar = findViewById(R.id.progressBar);
        
        // Configure WebView settings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // Enable DOM storage
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        
        // Set up WebChromeClient for progress tracking
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // Show progress bar when loading starts
                if (newProgress < 100 && mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                
                // Update progress value
                mProgressBar.setProgress(newProgress);
                
                // Hide progress bar when loading is complete
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        
        // Set WebViewClient
        mWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Show progress bar when page starts loading
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(0);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide progress bar when page finishes loading
                mProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "Page loaded: " + url);
            }
        });

        // REMOTE RESOURCE
        mWebView.loadUrl("https://slt.studysimplify.in/public/html/tuition_home.html");

        // LOCAL RESOURCE
        // mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            Log.d(TAG, "Going back in WebView history");
            mWebView.goBack();
        } else {
            Log.d(TAG, "No more back history, exiting app");
            super.onBackPressed();
        }
    }
}
