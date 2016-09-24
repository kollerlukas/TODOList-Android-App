package com.koller.lukas.todolist;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.koller.lukas.todolist.Activities.MainActivity;
import com.koller.lukas.todolist.Todolist.Event;
import com.koller.lukas.todolist.Util.ThemeHelper;
import com.koller.lukas.todolist.Widget.WidgetProvider_List;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Lukas on 17.11.2015.
 */
public class BroadcastReceiver extends WakefulBroadcastReceiver {

    private JSONArray array;
    private Event event;
    private boolean MainActivityRunning;

    @Override
    public void onReceive(Context context, Intent intent) {
        array = readFile(context);
        MainActivityRunning = MainActivity.isRunning;
        long id = intent.getLongExtra("EventId", 0L);
        switch (intent.getAction()) {
            case "ALARM":
                SharedPreferences sharedpreferences
                        = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);

                event = lookForEvent(id);
                if (event == null) {
                    return;
                }
                sendNotification(context, event.getWhatToDo(), event.getId(),
                        sharedpreferences.getBoolean("vibrate", true), event.getColor());
                checkIfEventIsRepeating(context);
                break;
            case "notification_button": //Done Button from the reminder notification of a Event
                NotificationManager notificationManager
                        = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(intent.getIntExtra("NotificationId", 0));
                removeEvent(context, id);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                resetAlarms(context, intent);
                checkForShowingNotification(context);
                break;
            case Intent.ACTION_SHUTDOWN:
                SharedPreferences sharedPreferences
                        = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("shutdown_timestamp", System.currentTimeMillis());
                editor.apply();
                break;
        }
    }

    public void removeEvent(Context context, long event_id) {
        if (MainActivityRunning) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction("removeEventNotifDoneButton");
            intent.putExtra("eventId", event_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                    |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    |Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            try {
                for (int i = 0; i < array.length(); i++) {
                    Event e = new Event(array.getJSONObject(i));
                    if (e.getId() == event_id) {
                        array.remove(i);
                    }
                }
                String data = array.toString();
                try {
                    FileOutputStream fos = context.openFileOutput("events", Context.MODE_PRIVATE);
                    fos.write(data.getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SharedPreferences.Editor editor
                        = context.getSharedPreferences("todolist", Context.MODE_PRIVATE).edit();
                editor.putBoolean("wasEventRemoved", true);
                editor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        updateWidget(context);
        showNotification(context);
    }

    private void sendNotification(Context context, String event, long event_id, boolean vibrate, int colorIndex) {
        if (vibrate) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
        int id = (int) System.currentTimeMillis();
        NotificationManager alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Clicking on the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, new Intent(context,
                MainActivity.class).setAction("START_MAIN_ACTIVITY"), PendingIntent.FLAG_UPDATE_CURRENT);
        //Clicking the notification actionButton
        Intent remove_event_intent = new Intent(context, BroadcastReceiver.class);
        remove_event_intent.putExtra("NotificationId", id);
        remove_event_intent.putExtra("EventId", event_id);
        remove_event_intent.setAction("notification_button");
        PendingIntent remove_event_pendingIntent = PendingIntent.getBroadcast(context, id, remove_event_intent, PendingIntent.FLAG_ONE_SHOT);

        int color = new ThemeHelper(context, colorIndex).getEventColor(colorIndex);
        if(Color.red(color) == 255 && Color.green(color) == 255 && Color.blue(color) == 255){
            color = ContextCompat.getColor(context, R.color.light_grey);
        }

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.dont_forget))
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentText(event)
                .addAction(R.drawable.ic_done_white_24dp, context.getString(R.string.done), remove_event_pendingIntent)
                .setAutoCancel(true)
                .setColor(color);
        alarmNotificationBuilder.setContentIntent(pendingIntent);
        alarmNotificationBuilder.setAutoCancel(true);
        alarmNotificationManager.notify(id, alarmNotificationBuilder.build());
    }

    public void checkIfEventIsRepeating(Context context) {
        if (event.getAlarm() == null) {
            return;
        }
        if (!(boolean) event.getAlarm().get("repeating")) {
            event.removeAlarm();
            return;
        }
        if(((int) event.getAlarm().get("repeatMode")) == 3){
            if((boolean) event.getAlarm().noDaySelected()){
                event.removeAlarm();
                return;
            }
        }

        long alarmTime = event.getAlarm().nextAlarmTime();
        setAlarm(context, event.getId(), alarmTime);

        if (MainActivityRunning) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction("setRepeatingAlarm");
            intent.putExtra("eventId", event.getId());
            intent.putExtra("alarmTime", alarmTime);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                    |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    |Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            try {
                for (int i = 0; i < array.length(); i++) {
                    Event e = new Event(array.getJSONObject(i));
                    if (event.getId() == e.getId()) {
                        array.remove(i);
                        e.getAlarm().setTime(alarmTime);
                        array.put(i, e.saveData());
                    }
                }
                String data = array.toString();
                try {
                    FileOutputStream fos = context.openFileOutput("events", Context.MODE_PRIVATE);
                    fos.write(data.getBytes());
                    fos.close();
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Event lookForEvent(long id) {
        try {
            if (array == null) {
                return null;
            }
            for (int i = 0; i < array.length(); i++) {
                Event e = new Event(array.getJSONObject(i));
                if (id == e.getId()) {
                    return e;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String resetAlarms(Context context, Intent intent) {
        try {
            if (array == null) {
                return "Error102";
            }
            for (int i = 0; i < array.length(); i++) {
                Event e = new Event(array.getJSONObject(i));
                if(e.getAlarm() != null){
                    long alarmTime = (long) e.getAlarm().get("time");
                    if (alarmTime > System.currentTimeMillis()) {
                        setAlarm(context, e.getId(), alarmTime);
                    } else if (alarmTime > context.getSharedPreferences("todolist",
                            Context.MODE_PRIVATE).getLong("shutdown_timestamp", System.currentTimeMillis())) {
                        sendNotification(context,
                                e.getWhatToDo(),
                                intent.getIntExtra("EventId", 0),
                                context.getSharedPreferences("todolist",
                                        Context.MODE_PRIVATE).getBoolean("vibrate", true),
                                e.getColor());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void checkForShowingNotification(Context context) {
        if (context.getSharedPreferences("todolist",
                Context.MODE_PRIVATE).getBoolean("showNotification", true)) {
            showNotification(context);
        }
    }

    public void showNotification(Context context) {
        try {
            String content;
            if (array == null) {
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
                return;
            }
            int todolist_size = array.length();
            if(todolist_size == 0){
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(0);
                return;
            }
            if (todolist_size == 1) {
                content = context.getString(R.string.you_have) + " " + todolist_size + " " + context.getString(R.string.event_in_your_todolist);
            } else {
                content = context.getString(R.string.you_have) + " " + todolist_size + " " + context.getString(R.string.events_in_your_todolist);
            }
            Intent add_event_intent = new Intent(context, MainActivity.class);
            add_event_intent.setAction("notification_add_todo");
            add_event_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent add_event_pendingIntent
                    = PendingIntent.getActivity(context, 6, add_event_intent, 0); // PendingIntent.FLAG_IMMUTABLE

            android.support.v7.app.NotificationCompat.Builder mBuilder
                    = (android.support.v7.app.NotificationCompat.Builder)
                    new android.support.v7.app.NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setContentTitle(context.getString(R.string.app_name))
                    .addAction(R.drawable.ic_add, context.getString(R.string.add_event), add_event_pendingIntent)
                    .setColor(ContextCompat.getColor(context, R.color.button_color))
                    .setContentText(content);
            NotificationManager mNotificationManager
                    = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent
                    = new Intent(context, MainActivity.class);
            PendingIntent resultPendingIntent
                    = PendingIntent.getActivity(context, 666, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            android.support.v7.app.NotificationCompat.InboxStyle inboxStyle
                    = new android.support.v7.app.NotificationCompat.InboxStyle();
            String[] events = new String[6];
            int todoSize;
            if (todolist_size > 5) {
                todoSize = 5;
                events[5] = "...";
            } else {
                todoSize = todolist_size;
            }
            for (int i = 0; i < todoSize; i++) {
                Event e = new Event(array.getJSONObject(i));
                events[i] = e.getWhatToDo();
            }
            inboxStyle.setSummaryText(content);
            inboxStyle.setBigContentTitle(context.getString(R.string.your_events_are));
            for (int i = 0; i < events.length; i++) {
                inboxStyle.addLine(events[i]);
            }
            mBuilder.setStyle(inboxStyle);
            mBuilder.setOngoing(true);
            mNotificationManager.notify(0, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray readFile(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput("events");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            if (fis != null) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new JSONArray(sb.toString());
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

    public void updateWidget(Context context) {
        Intent intent = new Intent(context, WidgetProvider_List.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, WidgetProvider_List.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}