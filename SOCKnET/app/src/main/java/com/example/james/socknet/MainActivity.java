package com.example.james.socknet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView tv_status, tv_time, tv_coord;
    private Button btn_check, btn_toggle, btn_home, btn_coord;
    private ImageButton ibtn_status, ibtn_home;
    private Boolean isHome = true;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isOn = true;
    private boolean hasNotified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_status = findViewById(R.id.tv_status);
        tv_time = findViewById(R.id.tv_time);
        btn_check = findViewById(R.id.btn_check);
        btn_toggle = findViewById(R.id.btn_toggle);
        btn_home = findViewById(R.id.btn_home);
        ibtn_home = findViewById(R.id.ibtn_home);
        ibtn_status = findViewById(R.id.ibtn_status);
        btn_coord = findViewById(R.id.btn_coord);
        tv_coord = findViewById(R.id.tv_coord);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ibtn_home.setClickable(false);
        ibtn_status.setClickable(false);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tv_coord.setText("Coordinates: \n" + location.getLatitude() + " " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        } else {
            configureButton();
        }


        btn_home.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (isHome) {
                    isHome = false;
                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                    grayscaleMatrix.setSaturation(0);

                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                    ibtn_home.getBackground().setColorFilter(filter);
                } else {
                    isHome = true;
                    ibtn_home.getBackground().setColorFilter(null);
                }
            }
        });

        btn_check.setOnClickListener(new Button.OnClickListener() {
            String tv_value;

            public void onClick(View v) {
                tv_value = tv_status.getText().toString();

                // Instantiate the RequestQueue.
                final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://206.189.92.99/adaptor/get_state?name=adaptor1";

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean state = response.getBoolean("state");
                                    if (state) {
                                        tv_status.setText("Status: ON");
                                        ibtn_home.getBackground().setColorFilter(null);
                                        isOn = true;
                                    } else {
                                        tv_status.setText("Status: OFF");
                                        final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                        grayscaleMatrix.setSaturation(0);

                                        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                        ibtn_status.getBackground().setColorFilter(filter);
                                        isOn = false;
                                    }
                                } catch (JSONException e) {
                                    tv_status.setText("Status: Server unavailable.");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    ibtn_status.getBackground().setColorFilter(filter);
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv_status.setText("Status: Server unavailable.");
                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                grayscaleMatrix.setSaturation(0);

                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                ibtn_status.getBackground().setColorFilter(filter);

                            }
                        });

                // Add the request to the RequestQueue.
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("E, MMMM d, yyyy, h:mm:ss a");

                tv_time = findViewById(R.id.tv_time);
                tv_time.setText("Timestamp: " + format.format(currentTime).toString());
            }
        });

        btn_toggle.setOnClickListener(new Button.OnClickListener() {
            String tv_value;

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
                                    if (state) {
                                        tv_status.setText("Status: ON");
                                        ibtn_status.getBackground().setColorFilter(null);
                                        isOn = true;
                                    } else {
                                        tv_status.setText("Status: OFF");
                                        final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                        grayscaleMatrix.setSaturation(0);

                                        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                        ibtn_status.getBackground().setColorFilter(filter);
                                        isOn = false;
                                    }
                                } catch (JSONException e) {
                                    tv_status.setText("Status: Server unavailable.");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    ibtn_status.getBackground().setColorFilter(filter);
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv_status.setText("Status: Server unavailable.");
                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                grayscaleMatrix.setSaturation(0);

                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                ibtn_status.getBackground().setColorFilter(filter);
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);

                tv_value = tv_status.getText().toString();
                if (tv_value.equals("Status: ON")) {
                    url_post = "http://206.189.92.99/adaptor/power_off?name=adaptor1";
                } else if (tv_value.equals("Status: OFF")) {
                    url_post = "http://206.189.92.99/adaptor/power_on?name=adaptor1";
                }

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequestPOST = new JsonObjectRequest
                        (Request.Method.POST, url_post, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean state = response.getBoolean("state");
                                    if (state) {
                                        tv_status.setText("Status: ON");
                                        ibtn_status.getBackground().setColorFilter(null);
                                        isOn = true;
                                    } else {
                                        tv_status.setText("Status: OFF");
                                        final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                        grayscaleMatrix.setSaturation(0);

                                        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                        ibtn_status.getBackground().setColorFilter(filter);
                                        isOn = false;
                                    }
                                } catch (JSONException e) {
                                    tv_status.setText("Status: Server unavailable.");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    ibtn_status.getBackground().setColorFilter(filter);
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv_status.setText("Status: Server unavailable.");
                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                grayscaleMatrix.setSaturation(0);

                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                ibtn_status.getBackground().setColorFilter(filter);
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequestPOST);

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("E, MMMM d, yyyy, h:mm:ss a");

                tv_time = findViewById(R.id.tv_time);
                tv_time.setText("Timestamp: " + format.format(currentTime).toString());
            }
        });
        check();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {
        btn_coord.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });
    }

    public void check() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.getTypeName().equalsIgnoreCase("wifi")) {
            hasNotified = false;
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
                                    ibtn_home.getBackground().setColorFilter(null);
                                    isOn = true;
                                }else {
                                    tv_status.setText("Status: OFF");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    ibtn_status.getBackground().setColorFilter(filter);
                                    isOn = false;
                                }
                            } catch (JSONException e) {
                                tv_status.setText("Status: Server unavailable.");
                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                grayscaleMatrix.setSaturation(0);

                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                ibtn_status.getBackground().setColorFilter(filter);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            tv_status.setText("Status: Server unavailable.");
                            final ColorMatrix grayscaleMatrix = new ColorMatrix();
                            grayscaleMatrix.setSaturation(0);

                            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                            ibtn_status.getBackground().setColorFilter(filter);

                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("E, MMMM d, yyyy, h:mm:ss a");

            tv_time = findViewById(R.id.tv_time);
            tv_time.setText("Timestamp: " + format.format(currentTime).toString());
        }else {
            String curr_state = "";
            if(isOn) {
                curr_state = "ON";
                if(!hasNotified) {
                    String title = "SOCKnET";
                    String subject = "SOCKnET";
                    String body = "Your SOCKnET is currently on, and it seems that you are currently out of range. Connect to a network source immediately to turn off SOCKnET from the app.";
                    NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addParentStack(MainActivity.class);

                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(intent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notify=new Notification.Builder
                            (getApplicationContext()).setContentTitle(title).setContentText(body).
                            setContentTitle(subject).setSmallIcon(R.drawable.light_bulb_icon)
                            .setStyle(new Notification.BigTextStyle().bigText(body)).setContentIntent(resultPendingIntent)
                            .build();

                    notify.flags |= Notification.FLAG_AUTO_CANCEL;
                    notif.notify(0, notify);

                    hasNotified = true;
                }
            }else {
                curr_state = "OFF";
            }
            tv_coord.setText("Last known state of adaptor: " + curr_state);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check();
            }
        }, 5000);
    }
}
