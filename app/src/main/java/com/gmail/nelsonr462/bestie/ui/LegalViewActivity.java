package com.gmail.nelsonr462.bestie.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gmail.nelsonr462.bestie.R;

public class LegalViewActivity extends AppCompatActivity {
    private String TAG = LegalViewActivity.class.getSimpleName();
    private String mLegalUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_view);

        final ProgressBar webProgressBar = (ProgressBar) findViewById(R.id.webViewProgressBar);
        final WebView legalWebView = (WebView) findViewById(R.id.legalWebView);

        webProgressBar.setVisibility(View.VISIBLE);
        mLegalUrl = getIntent().getStringExtra("legalUrl");

        legalWebView.getSettings().setBuiltInZoomControls(true);
        legalWebView.setVisibility(View.INVISIBLE);

        legalWebView.loadUrl(mLegalUrl);
        legalWebView.getSettings().setUseWideViewPort(true);
        legalWebView.getSettings().setLoadWithOverviewMode(true);
        legalWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webProgressBar.setVisibility(View.INVISIBLE);
                legalWebView.setVisibility(View.VISIBLE);
            }
        });

    }

}
