package org.starling.lghrunningapp;

import android.content.Context;
import android.util.Log;
import android.webkit.ServiceWorkerClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ServiceWorkerClient1 extends ServiceWorkerClient {

    public static final String TAG = ServiceWorkerClient1.class.getCanonicalName();
    @Override
    public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
        Log.e(TAG, "in service worker. isMainFrame:" + request.isForMainFrame() +": " + request.getUrl());
        WebResourceResponse wrr = super.shouldInterceptRequest(request);
//        if (wrr == null){
//            String offLinePage = "offline";
//            InputStream data = new ByteArrayInputStream(offLinePage.getBytes());
//            wrr = new WebResourceResponse("text/html","utf8", data);
//        }

        String path = request.getUrl().getPath();

//        if (wrr == null && path.toLowerCase().endsWith(".jsx")){
//            try {
//                String text = readFromFile(MainActivity.getInstance(), request.getUrl().getPath());
//                String offLinePage = "offline";
//                InputStream data = new ByteArrayInputStream(text.getBytes());
//                wrr = new WebResourceResponse("text/html", "utf8", data);
//            } catch (Exception e){
//                Log.e(TAG, e.getMessage());
//            }
//        }
//
        if (request.getMethod().equals("GET") && path.toLowerCase().endsWith(".js")){
            try {
                File f = new File(request.getUrl().getPath()) ;
                FileOutputStream fos = null;
                String text = getStringFromInputStream(wrr.getData());
                writeToFile(text,  MainActivity.getInstance());
            } catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        }

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


}
