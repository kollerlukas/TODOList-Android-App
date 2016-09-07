package com.koller.lukas.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Lukas on 17.11.2015.
 */
public class BroadcastReceiver extends WakefulBroadcastReceiver {

    private int event_color_index;
    private long alarm_time;

    @Override
    public void onReceive(Context context, Intent intent) {
        setResultCode(Activity.RESULT_OK);

        switch(intent.getAction()){
            case "ALARM":
                SharedPreferences sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
                String event = lookForEvent(context, intent);

                if(!event.equals("Error101")){
                    sendNotification(context, event, intent.getLongExtra("EventId", 0), sharedpreferences.getBoolean("vibrate", true));
                }
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                resetAlarms(context, intent);
                break;
            case Intent.ACTION_SHUTDOWN:
                SharedPreferences sharedPreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putLong("shutdown_timestamp", System.currentTimeMillis());
                editor.apply();
                break;
        }
    }

    private void sendNotification(Context context, String event, long event_id, boolean vibrate) {
        if (vibrate) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
        int id = (int) System.currentTimeMillis();
        NotificationManager alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, new Intent(context, MainActivity.class).setAction("START_MAIN_ACTIVITY"), PendingIntent.FLAG_UPDATE_CURRENT);
        Intent remove_event_intent = new Intent(context, MainActivity.class);
        remove_event_intent.putExtra("NotificationId", id);
        remove_event_intent.putExtra("EventId", event_id);
        remove_event_intent.setAction("notification_button");
        remove_event_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent remove_event_pendingIntent = PendingIntent.getActivity(context, id, remove_event_intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.dont_forget))
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentText(event)
                .addAction(R.drawable.ic_done_white_24dp, context.getString(R.string.done), remove_event_pendingIntent)
                .setAutoCancel(true)
                .setColor(new ThemeHelper(context).getEventColor(event_color_index));
        alarmNotificationBuilder.setContentIntent(pendingIntent);
        alarmNotificationBuilder.setAutoCancel(true);
        alarmNotificationManager.notify(id, alarmNotificationBuilder.build());
    }

    private String lookForEvent(Context context, Intent intent){
        try{
            JSONObject json = readFile(context);
            if(json == null){
                return "Error102";
            }
            for (int i = 0; i < json.getInt("todolist.size()"); i++) {
                long alarm_id = json.getLong(i + "Id");
                if(intent.getLongExtra("EventId", 0) == alarm_id){
                    event_color_index = json.getInt(i + "Color");
                    alarm_time = json.getLong(i + "AlarmTime");
                    return json.getString(i + "WhatToDo");
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "Error101";
    }

    private String resetAlarms(Context context, Intent intent){
        try {
            JSONObject json = readFile(context);
            if(json == null){
                return "Error102";
            }
            for (int i = 0; i < json.getInt("todolist.size()"); i++) {
                if (json.getLong(i + "AlarmTime") > System.currentTimeMillis()) {
                    setAlarm(context, json.getLong(i + "Id"), json.getLong(i + "AlarmTime"));
                } else if(json.getLong(i + "AlarmTime") > context.getSharedPreferences("todolist", Context.MODE_PRIVATE).getLong("shutdown_timestamp", System.currentTimeMillis())){
                    event_color_index = json.getInt(i + "Color");
                    alarm_time = json.getInt(i + "AlarmTime");
                    sendNotification(context, json.getString(i + "WhatToDo"), intent.getIntExtra("EventId", 0), context.getSharedPreferences("todolist", Context.MODE_PRIVATE).getBoolean("vibrate", true));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Error102";
    }

    private JSONObject readFile(Context context){
        StringBuilder sb = new StringBuilder();
        try{
            FileInputStream fis = context.openFileInput("events");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            if (fis != null) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n" );
                }
            }
            fis.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        try {
            return new JSONObject(sb.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setAlarm(Context context, long event_id, long alarm_time) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiver.class);
        intent.putExtra("EventId", event_id);
        intent.setAction("ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) event_id, intent, 0);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
    }
}