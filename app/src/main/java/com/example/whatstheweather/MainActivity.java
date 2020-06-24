package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity {


    EditText cityName;
    TextView resultTextView;


    public void findWeather(View view) {

        Log.i("cityName", cityName.getText().toString());

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        DownloadTask task = new DownloadTask();

        String result = null;

        try {
            result = task.execute("https://api.openweathermap.org/data/2.5/weather?q="+cityName.getText().toString()+"&appid=2de46543ae87c1f7bb24b990c6d47e1c").get();
        }catch (Exception e){

            e.printStackTrace();
            Log.i("why????","why");
        }
        Log.i("Result", result);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cityName = (EditText)findViewById(R.id.cityName);
        resultTextView=(TextView)findViewById(R.id.resultTextView);



    }


    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                String inputLine;

                while ((inputLine = br.readLine()) != null) {
                    // System.out.println(inputLine);
                    result += inputLine;
                }
                br.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {String message ="";


                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main ="";
                    String description="";

                    main=jsonPart.getString("main");
                    description= jsonPart.getString("description");

                    if(main !="" && description !=""){
                        message+= main + " :" +description +"\r\n";
                    }

                }
                if(message != ""){
                    resultTextView.setText(message);

                }else{
                    Toast.makeText(getApplicationContext(), "Could find weather", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}