package com.example.jgabaja.smartadaptor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    boolean stat_home = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_check= findViewById(R.id.btn_check);
        Button btn_toggle = findViewById(R.id.btn_toggle);
        btn_check.setText("Check Status");
        btn_toggle.setText("TOGGLE");

        Button btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new Button.OnClickListener() {
            TextView tv_home = findViewById(R.id.tv_home);
           public void onClick(View v) {
               if(stat_home) {
                   stat_home = false;
                   tv_home.setText("Not Home");
               }else {
                   stat_home = true;
                   tv_home.setText("Home");
               }
           }
        });

        btn_check.setOnClickListener(new Button.OnClickListener() {
            TextView tv_status = findViewById(R.id.tv_status);
            String tv_value;
            TextView tv_time;
            public void onClick(View v) {
                tv_value = tv_status.getText().toString();

                // Instantiate the RequestQueue.
                final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url ="http://206.189.92.99/adaptor/get_state?name=adaptor1";

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean state = response.getBoolean("state");
                                    if(state) {
                                        tv_status.setText("Status: ON");
                                    }else {
                                        tv_status.setText("Status: OFF");
                                    }
                                } catch (JSONException e) {
                                    tv_status.setText("Status: Server unavailable.");
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv_status.setText("Status: Server unavailable.");

                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
                Date currentTime = Calendar.getInstance().getTime();

                tv_time = findViewById(R.id.tv_time);
                tv_time.setText("Timestamp: " + currentTime.toString());
            }
        });

        btn_toggle.setOnClickListener(new Button.OnClickListener() {
            TextView tv_status = findViewById(R.id.tv_status);
            String tv_value;
            TextView tv_time;
            public void onClick(View v) {
                tv_value = tv_status.getText().toString();

                // Instantiate the RequestQueue.
                final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url_get = "http://206.189.92.99/adaptor/power_off?name=adaptor1";
                String url_post = "http://206.189.92.99/adaptor/power_off?name=adaptor1";

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url_get, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean state = response.getBoolean("state");
                                    if(state) {
                                        tv_status.setText("Status: ON");
                                    }else {
                                        tv_status.setText("Status: OFF");
                                    }
                                } catch (JSONException e) {
                                    tv_status.setText("Status: Server unavailable.");
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv_status.setText("Status: Server unavailable.");
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);

                tv_value = tv_status.getText().toString();
                if(tv_value.equals("Status: ON")) {
                    url_post = "http://206.189.92.99/adaptor/power_off?name=adaptor1";
                }else if(tv_value.equals("Status: OFF")) {
                    url_post = "http://206.189.92.99/adaptor/power_on?name=adaptor1";
                }

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequestPOST = new JsonObjectRequest
                        (Request.Method.POST, url_post, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean state = response.getBoolean("state");
                                    if(state) {
                                        tv_status.setText("Status: ON");
                                    }else {
                                        tv_status.setText("Status: OFF");
                                    }
                                } catch (JSONException e) {
                                    tv_status.setText("Status: Server unavailable.");
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv_status.setText("Status: Server unavailable.");
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequestPOST);

                Date currentTime = Calendar.getInstance().getTime();

                tv_time = findViewById(R.id.tv_time);
                tv_time.setText("Timestamp: " + currentTime.toString());
            }
        });
        /*while(true) {
            if(stat_home) continue;
            else {
                final TextView tv_status = findViewById(R.id.tv_status);

                // Instantiate the RequestQueue.
                final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url ="http://206.189.92.99/adaptor/get_state?name=adaptor1";

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean state = response.getBoolean("state");
                                    if(state) {
                                        tv_status.setText("Status: ON");
                                    }else {
                                        tv_status.setText("Status: OFF");
                                    }
                                } catch (JSONException e) {
                                    tv_status.setText("Status: Server unavailable.");
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv_status.setText("Status: Server unavailable.");

                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
                Date currentTime = Calendar.getInstance().getTime();

                TextView tv_time;
                tv_time = findViewById(R.id.tv_time);
                tv_time.setText("Timestamp: " + currentTime.toString());
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
