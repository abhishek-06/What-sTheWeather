package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText =findViewById(R.id.editText);
        resultTextView =findViewById(R.id.resultTextView);
    }

    public void getWeather(View view){


        try {
            DownloadTask task =new DownloadTask();

            String encodeCityname = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            Log.i("City Name", encodeCityname);
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodeCityname + "&appid=b6907d289e10d714a6e88b30761fae22");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("App Crashed","Could Not find weather");
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls){
            String result ="";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url =new URL(urls[0]);
                urlConnection =(HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader =new InputStreamReader(in);
                int data =reader.read();

                while (data!=-1){
                    char current =(char) data;
                    result += current;
                    data =reader.read();
                }

                return result;

            } catch (Exception e)  {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                Log.i("App Crashed","Could Not find weather");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i("JSON",s);

            try {
                String message ="";

                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo =jsonObject.getString("weather");
                Log.i("Weather Content", weatherInfo);

                JSONArray arr= new JSONArray(weatherInfo);

                for(int i=0 ;i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    Log.i("main",jsonPart.getString("main"));
                    Log.i("Description",jsonPart.getString("description"));

                    if(!main.equals("") && !description.equals("")){
                        message +=main + ": " + description +"\r\n";
                    }
                }
                if(!message.equals("")){

                    resultTextView.setText(message);
                    Log.i("Weather ",message);
                }
                else{
                    Log.i("App Crashed","Could Not find weather");
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.i("App Crashed","Could Not find weather");
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
