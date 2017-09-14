package com.app.volleyappagain;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView mTxtDegrees, mTxtWeather, mTxtError;
    ImageView mImageView;
    MarsWeather helper = MarsWeather.getmInstance();
    final static String RECENT_API_ENDPOINT = "http://marsweather.ingenology.com/v1/latest/",
                        TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtDegrees = (TextView) findViewById(R.id.degrees);
        mTxtWeather = (TextView) findViewById(R.id.weather);
        mTxtError = (TextView) findViewById(R.id.error);
        mImageView = (ImageView) findViewById(R.id.main_bg);


        loadWeatherData();
        searchRandomImage();

    }

    @Override
    protected void onStop() {
        super.onStop();
        helper.cancel();
    }

    private void loadWeatherData(){

        CustomJsonRequest request = new CustomJsonRequest(Request.Method.GET, RECENT_API_ENDPOINT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String minTemp, maxTemp, atmo;
                            int avgTemp;

                            response = response.getJSONObject("report");
                            minTemp = response.getString("min_temp");
                            minTemp = minTemp.substring(0, minTemp.indexOf("."));
                            maxTemp = response.getString("max_temp");
                            maxTemp = maxTemp.substring(0, maxTemp.indexOf("."));
                            avgTemp = (Integer.valueOf(minTemp)+Integer.parseInt(maxTemp))/2;
                            atmo = response.getString("atmo_opacity");

                            mTxtDegrees.setText(avgTemp+"°");
                            mTxtWeather.setText(atmo);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTxtError.setVisibility(View.VISIBLE);
                error.printStackTrace();
            }
        });

        request.setPriority(Request.Priority.HIGH);
        helper.add(request);
    }

    private void searchRandomImage(){

        CustomJsonRequest request = new CustomJsonRequest(Request.Method.GET, "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            JSONArray images = new JSONArray(response.toString());
        /*                    int index = new Random().nextInt(images.length());
                            JSONObject imageItem = images.getJSONObject(index);
                            String imageUrl = imageItem.getString("url");*/

                            /*JSONArray images = response.getJSONObject("results").getJSONArray("url");
                            String imageUrl = images.getJSONObject(0).getString("url");
                            //待加载图片...
                            loadImg(imageUrl);*/
//                            String s = response.toString();
                            JSONArray array = response.getJSONArray("results");
                            long currentTime = System.currentTimeMillis();

                            int index = (int) currentTime%10;
                            JSONObject image = array.getJSONObject(index);
                            String imageUrl = image.getString("url");
                            loadImg(imageUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mImageView.setBackgroundColor(Color.parseColor("#FF5722"));
                error.printStackTrace();
            }
        });

        request.setPriority(Request.Priority.LOW);
        helper.add(request);
    }

    private void loadImg(String imageUrl){

        ImageRequest request = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mImageView.setImageBitmap(response);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mImageView.setBackgroundColor(Color.parseColor("#FF5722"));
                error.printStackTrace();
            }
        });
        helper.add(request);
    }
}
