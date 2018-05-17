package com.example.james.socknet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import android.os.Vibrator;

public class MainActivity extends AppCompatActivity {
    private TextView tv_status;
    private Button btn_check, btn_toggle;
    private ImageButton ibtn_status;
    private boolean isOn = true;
    private boolean hasNotified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        tv_status = findViewById(R.id.tv_status);
        btn_check = findViewById(R.id.btn_check);
        btn_toggle = findViewById(R.id.btn_toggle);
        ibtn_status = findViewById(R.id.ibtn_status);
        ibtn_status.setClickable(false);
        NotificationManager notify = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notify.cancelAll();

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
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("E, MMMM d, yyyy, h:mm:ss a");
            }
        });

        btn_toggle.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // Instantiate the RequestQueue.
                final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
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

                // Get instance of Vibrator from current Context
                Vibrator vi = (Vibrator) getSystemService(MainActivity.this.VIBRATOR_SERVICE);

                // Vibrate for 400 milliseconds
                vi.vibrate(400);
            }
        });
        if(isOn) {
            tv_status.setText("Status: ON");
        }
        check();
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
                                    ibtn_status.getBackground().setColorFilter(null);
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
                                if(isOn) {
                                    tv_status.setText("Status: ON");
                                    ibtn_status.getBackground().setColorFilter(null);
                                }else {
                                    tv_status.setText("Status: OFF");
                                    final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                    grayscaleMatrix.setSaturation(0);

                                    final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                    ibtn_status.getBackground().setColorFilter(filter);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(isOn) {
                                tv_status.setText("Status: ON");
                                ibtn_status.getBackground().setColorFilter(null);
                            }else {
                                tv_status.setText("Status: OFF");
                                final ColorMatrix grayscaleMatrix = new ColorMatrix();
                                grayscaleMatrix.setSaturation(0);

                                final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(grayscaleMatrix);
                                ibtn_status.getBackground().setColorFilter(filter);
                            }

                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }else {
            if(isOn) {
                if (!hasNotified) {
                    String title = "SOCKnET";
                    String subject = "SOCKnET";
                    String body = "Your SOCKnET is currently on, and it seems that you are currently out of range. Connect to a network source immediately to turn off SOCKnET from the app.";
                    NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addParentStack(MainActivity.class);

                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(intent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notify = new Notification.Builder
                            (getApplicationContext()).setContentTitle(title).setContentText(body).
                            setContentTitle(subject).setSmallIcon(R.drawable.socknet_logo)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.drawable.socknet_logo_small))
                            .setAutoCancel(true)
                            .setStyle(new Notification.BigTextStyle().bigText(body)).setContentIntent(resultPendingIntent)
                            .build();

                    notify.flags = Notification.FLAG_AUTO_CANCEL;
                    notif.notify(1, notify);

                    hasNotified = true;
                }
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check();
            }
        }, 5000);
    }
}
