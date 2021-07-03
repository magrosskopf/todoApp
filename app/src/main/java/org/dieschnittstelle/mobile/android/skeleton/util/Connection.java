package org.dieschnittstelle.mobile.android.skeleton.util;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class Connection extends AsyncTask<String, Boolean, Boolean> {

    @Override
    public Boolean doInBackground(String... strings) {
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL("http://10.0.2.2:8080").openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            conn.getInputStream();

            System.out.println("TRUE 123");
            return true;
        } catch (Exception e){
            System.out.println("FALSE 123" + e);
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
