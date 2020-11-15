package org.starling.lghrunningapp;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewClient1 extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url)
    {
       return false;
    }


}
