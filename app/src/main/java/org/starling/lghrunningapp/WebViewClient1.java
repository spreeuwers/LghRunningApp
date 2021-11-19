package org.starling.lghrunningapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class WebViewClient1 extends WebViewClient {

    public static final String TAG = WebViewClient1.class.getCanonicalName();

    public boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected());
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        boolean connected = checkInternetConnection(view.getContext());
        return super.shouldOverrideUrlLoading(view, request);
//        if (!connected) {
//            view.loadUrl("file:///android_asset/filename.html");
//        } else {
//            view.loadUrl(url);
//        }
//        return true;
//    }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest (WebView view, WebResourceRequest request){
        Log.e(TAG, "in webview client. isMainFrame:" + request.isForMainFrame() + ": " + request.getUrl());
        WebResourceResponse wrr = super.shouldInterceptRequest(view, request);
        String path = request.getUrl().getPath();

//        if (wrr == null && path.toLowerCase().endsWith(".js")){
//            String text = readFromFile(view.getContext(),request.getUrl().getPath() );
//            String offLinePage = "offline";
//            InputStream data = new ByteArrayInputStream(text.getBytes());
//            wrr = new WebResourceResponse("text/html","utf8", data);
//        }
//
//        if (request.getMethod().equals("GET") && path.toLowerCase().endsWith(".js")){
//            File f = new File(request.getUrl().getPath()) ;
//            FileOutputStream fos = null;
//            String text = getStringFromInputStream(wrr.getData());
//            writeToFile(text,  view.getContext());
//        }

        return wrr;

    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context, String fName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private  String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }
}
