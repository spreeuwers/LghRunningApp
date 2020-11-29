package org.starling.lghrunningapp;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewClient1 extends WebViewClient {

    public static final String TAG = WebViewClient1.class.getCanonicalName();
//    @Override
//    public boolean shouldOverrideUrlLoading(final WebView view, final String url)
//    {
//       return false;
//    }

    @Override
    public WebResourceResponse shouldInterceptRequest (WebView view, WebResourceRequest request){
        Log.e(TAG, "in webview client. isMainFrame:" + request.isForMainFrame() + ": " + request.getUrl());
        return super.shouldInterceptRequest(view, request);

    }


}
