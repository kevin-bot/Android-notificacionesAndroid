package com.example.notificacion1;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


       // Toast.makeText(getApplicationContext(),"Di entro!",Toast.LENGTH_LONG).show();

        if(remoteMessage.getData() !=null){
            evniarNotificacion(remoteMessage);
        }

        if(remoteMessage.getNotification() !=null){
            Log.d("TAG","Body notificacion: "+remoteMessage.getNotification().getBody());
            evniarNotificacion(remoteMessage);

        }
    }

    private void evniarNotificacion(RemoteMessage remoteMessage) {
        //Toast.makeText(getApplicationContext(),"Di entro!",Toast.LENGTH_LONG).show();

        Map<String,String> data= remoteMessage.getData();
        String title=data.get("title");
        String body=data.get("body");

        NotificationManager  manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String  NOTIFICATION_CHANNEL_ID = "xcheko51x";





       /* NotificationCompat.Builder builder1=new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL_ID);
        builder1.setSmallIcon(R.drawable.ic_sms_black_24dp);
        builder1.setContentTitle("Notificación Android");
        builder1.setContentText("Apuntes de mis cursos de Udemy");
        builder1.setColor(Color.BLUE);
        builder1.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder1.setLights(Color.MAGENTA,1000,1000);
        builder1.setVibrate(new long[]{1000,1000,100,1000,1000});
        builder1.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(1,builder1.build());*/

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1){
            //solo para android Oreo o superior

            @SuppressLint("WrongConstant") NotificationChannel channel= new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Mi notificacion",
                    NotificationManager.IMPORTANCE_MAX);

            //configuracion del canal de notificacion

            channel .setDescription("xcheko51x channel para app");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long[]{0,1000,500,1000});
            channel.enableLights(true);
            manager.createNotificationChannel(channel);

        }



        Intent intent= new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent  pendingIntent=PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setTicker("Hearty465")
                .setContentTitle(title)
                .setContentText(body)
                .setVibrate(new long[]{0,1000,500,1000})
                .setContentIntent(pendingIntent)
                .setContentInfo("info");

        manager.notify(1,builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Toast.makeText(getApplicationContext(),"Token"+token,Toast.LENGTH_LONG).show();
        Log.d("TAG","Refreshet Token "+token);
        FirebaseMessaging.getInstance().subscribeToTopic("dispositivos");
        enviarTokenToServer(token);
    }

    private void enviarTokenToServer(final String token) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://192.168.62.238/notificaciones/registrarToken.php"
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),"Exito!",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String, String>();
                parametros.put("Token",token);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
