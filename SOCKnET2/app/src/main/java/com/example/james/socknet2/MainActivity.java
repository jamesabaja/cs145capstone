package com.example.james.socknet2;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private CardView card_check, card_toggle, card_wifi, card_about;
    private ImageView img_status, img_wifi, img_toggle;
    private TextView tv_stat, tv_toggle, tv_wifi, tv_time;
    private ProgressBar progressBar, progressBar2;
    private boolean isOn = true, hasNotified = false, hasCurrent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        card_check = findViewById(R.id.card_check);
        card_toggle = findViewById(R.id.card_toggle);
        card_wifi = findViewById(R.id.card_wifi);
        card_about = findViewById(R.id.card_about);

        tv_stat = findViewById(R.id.tv_stat);
        tv_toggle = findViewById(R.id.tv_tog);
        tv_wifi = findViewById(R.id.tv_wifi);
        tv_time = findViewById(R.id.tv_time);

        img_status = findViewById(R.id.img_status);
        img_wifi = findViewById(R.id.img_wifi);
        img_toggle = findViewById(R.id.img_toggle);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        tv_toggle.setVisibility(View.GONE);
        progressBar2 = findViewById(R.id.progressBar2);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://206.189.92.99/adaptor/get_state?name=adaptor1";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            tv_toggle.setVisibility(View.VISIBLE);
                            Boolean state = response.getBoolean("state");
                            if (state) {
                                img_toggle.getBackground().setColorFilter(null);
                                isOn = true;
                                tv_toggle.setText("Restore Connection");
                            } else {
                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                grayscaleMatrix.setSaturation(0);

                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                img_toggle.getBackground().setColorFilter(filter);
                                isOn = false;
                                tv_toggle.setText("Cut off Connection");
                            }
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            tv_toggle.setVisibility(View.VISIBLE);
                            tv_stat.setText("Server unavailable.");
                            final ColorMatrix grayscaleMatrix = new ColorMatrix();
                            grayscaleMatrix.setSaturation(0);

                            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                            img_toggle.getBackground().setColorFilter(filter);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        tv_toggle.setVisibility(View.VISIBLE);
                        if(isOn) {
                            img_toggle.getBackground().setColorFilter(null);
                            tv_toggle.setText("Restore Connection");
                        }else {
                            final ColorMatrix grayscaleMatrix = new ColorMatrix();
                            grayscaleMatrix.setSaturation(0);

                            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                            img_toggle.getBackground().setColorFilter(filter);
                            tv_toggle.setText("Cut off Connection");
                        }
                        Toast.makeText(MainActivity.this, "Network connection required.", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(jsonObjectRequest);

        card_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://206.189.92.99/adaptor/get_state?name=adaptor1";
                tv_stat.setVisibility(View.GONE);
                progressBar2.setVisibility(View.VISIBLE);

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean state = response.getBoolean("opto_state");
                                    String strDate = response.getString("last_on");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                    SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                    String date = df.format(dateFormat.parse(strDate));
                                    progressBar2.setVisibility(View.GONE);
                                    tv_stat.setVisibility(View.VISIBLE);
                                    if (!state) {
                                        tv_stat.setText("ON");
                                        img_status.getBackground().setColorFilter(null);
                                        hasCurrent = true;
                                    } else {
                                        tv_stat.setText("OFF");
                                        final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                        grayscaleMatrix.setSaturation(0);

                                        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                        img_status.getBackground().setColorFilter(filter);
                                        hasCurrent = false;
                                    }
                                    tv_time.setText(date.toString());
                                } catch (JSONException e) {
                                    progressBar2.setVisibility(View.GONE);
                                    tv_stat.setVisibility(View.VISIBLE);
                                    if(hasCurrent) {
                                        tv_stat.setText("ON");
                                        img_status.getBackground().setColorFilter(null);
                                    }else {
                                        tv_stat.setText("OFF");
                                        final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                        grayscaleMatrix.setSaturation(0);

                                        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                        img_status.getBackground().setColorFilter(filter);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar2.setVisibility(View.GONE);
                                tv_stat.setVisibility(View.VISIBLE);
                                if(hasCurrent) {
                                    tv_stat.setText("ON");
                                    img_status.getBackground().setColorFilter(null);
                                }else {
                                    tv_stat.setText("OFF");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    img_status.getBackground().setColorFilter(filter);
                                }

                            }
                        });
                queue.add(jsonObjectRequest);
            }
        });
        card_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_toggle.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url_post = "http://206.189.92.99/adaptor/power_off?name=adaptor1";

                if(!isOn) {
                    url_post = "http://206.189.92.99/adaptor/power_on?name=adaptor1";
                }

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequestPOST = new JsonObjectRequest
                        (Request.Method.POST, url_post, (String) null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    progressBar.setVisibility(View.GONE);
                                    tv_toggle.setVisibility(View.VISIBLE);
                                    Boolean state = response.getBoolean("state");
                                    if (state) {
                                        img_toggle.getBackground().setColorFilter(null);
                                        isOn = true;
                                        tv_toggle.setText("Restore Connection");
                                    } else {
                                        final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                        grayscaleMatrix.setSaturation(0);

                                        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                        img_toggle.getBackground().setColorFilter(filter);
                                        isOn = false;
                                        tv_toggle.setText("Cut off Connection");
                                    }
                                } catch (JSONException e) {
                                    progressBar.setVisibility(View.GONE);
                                    tv_toggle.setVisibility(View.VISIBLE);
                                    tv_stat.setText("Server unavailable.");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    img_toggle.getBackground().setColorFilter(filter);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar.setVisibility(View.GONE);
                                tv_toggle.setVisibility(View.VISIBLE);
                                if(isOn) {
                                    img_toggle.getBackground().setColorFilter(null);
                                    tv_toggle.setText("Restore Connection");
                                }else {
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    img_toggle.getBackground().setColorFilter(filter);
                                    tv_toggle.setText("Cut off Connection");
                                }
                                Toast.makeText(MainActivity.this, "Network connection required.", Toast.LENGTH_SHORT).show();
                            }
                        });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequestPOST);
            }
        });
        card_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String info = "Left an appliance unplugged at home? Worry no more, SOCKnET's got your back!\n" +
                        "*insert more info here* \n *insert tagline here*\n\n" +
                        "With love from the SOCKnET TEAM,\nJames, Ivan, Kristine, Jullie, Luis";
                builder.setMessage(info)
                        .setIcon(R.drawable.socknet_logo)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        progressBar2.setVisibility(View.GONE);
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = cm.getActiveNetworkInfo();

                        if(netInfo != null && netInfo.getTypeName().equalsIgnoreCase("wifi")) {
                            tv_wifi.setText("IN RANGE");
                            img_wifi.getBackground().setColorFilter(null);
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
                                                Boolean state = response.getBoolean("opto_state");
                                                String strDate = response.getString("last_on");
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                                String date = df.format(dateFormat.parse(strDate));
                                                if(!state) {
                                                    tv_stat.setText("ON");
                                                    img_status.getBackground().setColorFilter(null);
                                                    hasCurrent = true;
                                                }else {
                                                    tv_stat.setText("OFF");
                                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                                    grayscaleMatrix.setSaturation(0);

                                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                                    img_status.getBackground().setColorFilter(filter);
                                                    hasCurrent = false;
                                                }
                                                tv_time.setText(date.toString());
                                            } catch (JSONException e) {
                                                if(hasCurrent) {
                                                    tv_stat.setText("ON");
                                                    img_status.getBackground().setColorFilter(null);
                                                }else {
                                                    tv_stat.setText("OFF");
                                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                                    grayscaleMatrix.setSaturation(0);

                                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                                    img_status.getBackground().setColorFilter(filter);
                                                    tv_toggle.setText("Cut off Connection");
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if(hasCurrent) {
                                                tv_stat.setText("ON");
                                                img_status.getBackground().setColorFilter(null);
                                            }else {
                                                tv_stat.setText("OFF");
                                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                                grayscaleMatrix.setSaturation(0);

                                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                                img_status.getBackground().setColorFilter(filter);
                                            }
                                        }
                                    });

                            // Add the request to the RequestQueue.
                            queue.add(jsonObjectRequest);
                        }else {
                            tv_wifi.setText("NOT IN RANGE");
                            final ColorMatrix grayscaleMatrix = new ColorMatrix();
                            grayscaleMatrix.setSaturation(0);

                            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                            img_wifi.getBackground().setColorFilter(filter);
                            if(hasCurrent) {
                                tv_stat.setText("ON");
                                if (!hasNotified) {
                                    String title = "SOCKnET";
                                    String subject = "SOCKnET";
                                    String body = "Your appliance is actively connected, and it seems that you are currently out of range. Connect to a network source immediately to cut the connection from the app.";
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                                    stackBuilder.addParentStack(MainActivity.class);

                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(intent);
                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this, NOTIFICATION_CHANNEL_ID);

                                    notificationBuilder.setAutoCancel(true)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.drawable.socknet_logo)
                                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.socknet_logo_small))
                                            .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                            .setContentTitle("SOCKnET")
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                                            .setContentIntent(resultPendingIntent)
                                            .setContentText(body);

                                    notificationManager.notify(1, notificationBuilder.build());

                                    hasNotified = true;
                                }
                            }else {
                                tv_stat.setText("OFF");
                            }
                        }
                    }
                });
            }
        }, 0, 3000);
        //check();
    }

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void check() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.getTypeName().equalsIgnoreCase("wifi")) {
            tv_wifi.setText("IN RANGE");
            img_wifi.getBackground().setColorFilter(null);
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
                                Boolean state = response.getBoolean("opto_state");
                                if(!state) {
                                    tv_stat.setText("ON");
                                    img_status.getBackground().setColorFilter(null);
                                    hasCurrent = true;
                                }else {
                                    tv_stat.setText("OFF");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    img_status.getBackground().setColorFilter(filter);
                                    hasCurrent = false;
                                }
                            } catch (JSONException e) {
                                if(hasCurrent) {
                                    tv_stat.setText("ON");
                                    img_status.getBackground().setColorFilter(null);
                                }else {
                                    tv_stat.setText("OFF");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    img_status.getBackground().setColorFilter(filter);
                                    tv_toggle.setText("Turn SOCKnET on");
                                }
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(hasCurrent) {
                                tv_stat.setText("ON");
                                img_status.getBackground().setColorFilter(null);
                            }else {
                                tv_stat.setText("OFF");
                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                grayscaleMatrix.setSaturation(0);

                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                img_status.getBackground().setColorFilter(filter);
                            }
                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }else {
            tv_wifi.setText("NOT IN RANGE");
            final ColorMatrix grayscaleMatrix = new ColorMatrix();
            grayscaleMatrix.setSaturation(0);

            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
            img_wifi.getBackground().setColorFilter(filter);
            if(hasCurrent) {
                tv_stat.setText("ON");
                if (!hasNotified) {
                    String title = "SOCKnET";
                    String subject = "SOCKnET";
                    String body = "Your appliance is actively connected, and it seems that you are currently out of range. Connect to a network source immediately to cut the connection from the app.";
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addParentStack(MainActivity.class);

                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(intent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

                    notificationBuilder.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.socknet_logo)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.socknet_logo_small))
                            .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                            .setContentTitle("SOCKnET")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                            .setContentIntent(resultPendingIntent)
                            .setContentText(body);

                    notificationManager.notify(1, notificationBuilder.build());

                    hasNotified = true;
                }
            }else {
                tv_stat.setText("OFF");
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check();
            }
        }, 2000);
    }
}
