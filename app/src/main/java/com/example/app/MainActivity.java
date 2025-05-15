package com.example.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private FrameLayout mProgressContainer;
    private static final String TAG = "MainActivity";
    private Handler mHandler;
    private static final int PROGRESS_DELAY = 100; // Small delay to avoid flashing for fast pages
    private static final int MIN_PROGRESS_DISPLAY_TIME = 500; // Minimum time to show progress for visual effect
    private long mLoadStartTime = 0;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.activity_main_webview);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressContainer = findViewById(R.id.progressContainer);
        mHandler = new Handler(Looper.getMainLooper());
        
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
            private boolean mIsInitialProgress = true;
            private int mLastProgress = 0;
            
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                
                // Show progress with a slight delay to avoid flashing for very fast loads
                if (mIsInitialProgress && newProgress > 0) {
                    mIsInitialProgress = false;
                    mHandler.postDelayed(() -> {
                        if (newProgress < 100) {
                            showProgressBar();
                        }
                    }, PROGRESS_DELAY);
                }
                
                // Don't let progress go backwards as it looks strange
                if (newProgress < mLastProgress && mLastProgress < 100) {
                    return;
                }
                mLastProgress = newProgress;
                
                // Update progress value with animation
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    ObjectAnimator animation = ObjectAnimator.ofInt(mProgressBar, "progress", mProgressBar.getProgress(), newProgress);
                    animation.setDuration(300);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }
                
                // Special handling for completion
                if (newProgress == 100) {
                    // Calculate elapsed time to ensure minimum display duration for visual feedback
                    long elapsedTime = System.currentTimeMillis() - mLoadStartTime;
                    long delayForMinDisplay = elapsedTime > MIN_PROGRESS_DISPLAY_TIME ? 0 : MIN_PROGRESS_DISPLAY_TIME - elapsedTime;
                    
                    mHandler.postDelayed(() -> {
                        hideProgressBar();
                        mIsInitialProgress = true;
                        mLastProgress = 0;
                    }, delayForMinDisplay);
                }
            }
        });
        
        // Set WebViewClient
        mWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoadStartTime = System.currentTimeMillis();
                // Reset progress and show after a small delay 
                // (avoids flashing for cache loads)
                mProgressBar.setProgress(0);
                mHandler.postDelayed(() -> {
                    showProgressBar();
                }, PROGRESS_DELAY);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page loaded: " + url);
            }
        });

        // REMOTE RESOURCE
        mWebView.loadUrl("https://slt.studysimplify.in/public/html/tuition_home.html");

        // LOCAL RESOURCE
        // mWebView.loadUrl("file:///android_asset/index.html");
    }
    
    private void showProgressBar() {
        if (mProgressContainer.getVisibility() != View.VISIBLE) {
            mProgressContainer.setAlpha(0f);
            mProgressContainer.setVisibility(View.VISIBLE);
            mProgressContainer.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null);
        }
    }
    
    private void hideProgressBar() {
        if (mProgressContainer.getVisibility() == View.VISIBLE) {
            mProgressContainer.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressContainer.setVisibility(View.GONE);
                    }
                });
        }
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
